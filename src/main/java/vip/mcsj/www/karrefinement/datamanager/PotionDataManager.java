package vip.mcsj.www.karrefinement.datamanager;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vip.mcsj.www.karrefinement.core.Service;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.object.RPotion;
import vip.mcsj.www.karrefinement.utils.FileUtil;
import vip.mcsj.www.karrefinement.utils.ReflectionUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PotionDataManager implements Service {
    public static Map<String, RPotion> potions = new HashMap<>();
    private String identifier;
    private ItemStack itemPotion;
    private Player p;

    public static Map<String,List<Object>> cache = new ConcurrentHashMap<>();
    //创建物品
    public PotionDataManager(String identifier) {
        this.identifier = identifier;
    }
    //更改
    public PotionDataManager(ItemStack itemPotion, Player p) {
        this.itemPotion = itemPotion;
        this.p = p;
    }
    //查询
    public PotionDataManager() {

    }

    @Override
    public void initialize() {
        init();
    }

    public static void init(){
        if(!potions.isEmpty()){
            potions.clear();
        }

        YamlConfiguration customFileYaml = FileUtil.getCustomFileYaml("potion.yml");
        Set<String> keys = customFileYaml.getKeys(false);
        for (String key : keys) {
            String name =  customFileYaml.getString(key+".Name");
            Material material = Material.valueOf(customFileYaml.getString(key+".Type"));
            int data =  customFileYaml.getInt(key+".Data");
            int customModelData =  customFileYaml.getInt(key+".CustomModelData");
            double success =  customFileYaml.getDouble(key+".Success");
            int duration =   customFileYaml.getInt(key+".Duration");
            List<String> lore = customFileYaml.getStringList(key+".Lore");

            potions.put(key,new RPotion(name,material,data,customModelData,success,duration,lore));
        }
    }

    public ItemStack createPotion(){
        RPotion rPotion = potions.get(identifier);
        if(rPotion == null){
            return null;
        }
        ItemStack item = new ItemStack(rPotion.getType(),1,(short)rPotion.getData());
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(rPotion.getName());
        itemMeta.setLore(rPotion.getLore());
        item.setItemMeta(itemMeta);
        ReflectionUtils.setCustomModelData(item, rPotion.getCustomModelData());
        NBT.modify(item,nbt -> {
            nbt.setString("refinementpotion",identifier);
        });
        return item;
    }

    public void drinkPotion(){
        RPotion rPotion = getPotionFromItemStack(itemPotion);
        if(rPotion == null){
            return;
        }
        changePlayerPotionInfo(p, rPotion.getSuccess(), rPotion.getDuration());
    }

    public static RPotion getPotionFromItemStack(ItemStack itemPotion){
        NBTItem nbtItem = new NBTItem(itemPotion);
        return potions.get(nbtItem.getString("refinementpotion"));
    }

    public static boolean isPotionLegal(ItemStack itemPotion){
        NBTItem nbtItem = new NBTItem(itemPotion);
        return nbtItem.hasKey("refinementpotion");
    }

    public void changePlayerPotionInfo(Player p,double success,int duration){
        List<Object> objects = queryPlayerPotionInfo(p);
        if(objects == null){
            insertPlayerPotionInfo(p,success,duration);
            return;
        }
        if((double)objects.get(1)*100 == success){
            updatePlayerPotionInfo(p,duration,(Long) objects.get(0));
        }else{
            insertPlayerPotionInfo(p,success,duration);
        }
    }

    public void updatePlayerPotionInfo(Player p,int duration,Long end_timestamp){
        String sql = "UPDATE refinementpotion_data SET end_timestamp = ? WHERE player_uuid = ?";
        try(Connection conn = KarRefinement.dm.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            long endTime = end_timestamp + (duration * 1000L);
            pstmt.setLong(1, endTime);
            pstmt.setString(2, p.getUniqueId().toString());
            pstmt.executeUpdate();
        }catch (SQLException e){
            KarRefinement.instance.getLogger().severe("更新玩家淬炼药水数据失败:" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void insertPlayerPotionInfo(Player p,double success,int duration){
        String sql = "INSERT INTO refinementpotion_data (player_uuid,start_timestamp,end_timestamp,success_rate) VALUES (?,?,?,?)";
        try(Connection conn = KarRefinement.dm.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            String uuid = p.getUniqueId().toString();
            double successRate = success / 100;
            long current = System.currentTimeMillis();
            long endTime =  current + (duration * 1000L);
            pstmt.setString(1, uuid);
            pstmt.setLong(2, current);
            pstmt.setLong(3, endTime);
            pstmt.setDouble(4, successRate);
            pstmt.execute();
        }catch (SQLException e){
            KarRefinement.instance.getLogger().severe("插入玩家淬炼药水数据失败:" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String formatTimeRemaining(long endTime) {
        long remaining = endTime - System.currentTimeMillis();
        if (remaining <= 0) return "已结束";

        long seconds = remaining / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + "天" + (hours % 24) + "小时";
        } else if (hours > 0) {
            return hours + "小时" + (minutes % 60) + "分";
        } else {
            return minutes + "分" + (seconds % 60) + "秒";
        }
    }

    public List<Object> queryPlayerPotionInfo(OfflinePlayer p){
        String sql = "SELECT * FROM refinementpotion_data WHERE player_uuid = ?";
        try(Connection conn = KarRefinement.dm.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1,p.getUniqueId().toString());
            try(ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    List<Object> list = new ArrayList<>();
                    list.add(rs.getLong("end_timestamp"));
                    list.add(rs.getDouble("success_rate"));
                    //更新缓存
                    cache.put(p.getUniqueId().toString(), list);
                    return list;
                }
            }
            return null;
        }catch (SQLException e){
            KarRefinement.instance.getLogger().severe("查询玩家淬炼药水数据失败:" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<Object> queryPlayerPotionInfoCache(OfflinePlayer p){
        //先查缓存
        if(cache.get(p.getUniqueId().toString())!=null){
            return cache.get(p.getUniqueId().toString());
        }
        //查不到就查数据库
        List<Object> objects = queryPlayerPotionInfo(p);
        return objects;
    }

    public List<String> queryOudatedPlayerPotionInfo(){
        String sql = "SELECT * FROM refinementpotion_data WHERE end_timestamp < ?";

        try(Connection conn = KarRefinement.dm.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setLong(1,System.currentTimeMillis());
            try(ResultSet rs = pstmt.executeQuery()) {
                List<String> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(rs.getString("player_uuid"));
                }
                return list;
            }
        }catch (SQLException e){
            KarRefinement.instance.getLogger().severe("查询玩家过期淬炼药水数据失败:" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public int deleteOudatedPlayerPotionInfo(){
        List<String> uuids = queryOudatedPlayerPotionInfo();

        if(uuids == null) return 0;
        if(uuids.isEmpty()) return 0;

        //删除缓存
        for (String uuid : uuids) {
            cache.remove(uuid);
        }

        String placeholders = uuids.stream().map(uuid -> "?").collect(Collectors.joining(","));
        String sql = "DELETE FROM refinementpotion_data WHERE player_uuid IN ("+placeholders+")";
        try(Connection conn = KarRefinement.dm.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < uuids.size(); i++) {
                pstmt.setString(i+1, uuids.get(i));
            }
            return pstmt.executeUpdate();
        }catch (SQLException e){
            KarRefinement.instance.getLogger().severe("删除玩家过期淬炼药水数据失败:" + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
}
