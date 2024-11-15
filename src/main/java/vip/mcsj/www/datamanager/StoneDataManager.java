package vip.mcsj.www.datamanager;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vip.mcsj.www.object.Stone;
import vip.mcsj.www.utils.FileUtil;
import vip.mcsj.www.utils.KarUtils;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 控制淬炼石信息的类
 */
public class StoneDataManager {

    public static Map<String,Stone> stones = new HashMap<>();
    public final List<String> stoneLoreList = new Vector<>();
    public static final List<String> stoneList = Arrays.asList("普通","中等","高等","完美","会员");
    /**
     * 淬炼石成功倍数
     */
    public static final Map<String,Double> stoneSuccessList = new HashMap<>();
    /**
     * 淬炼石成功率String，例如"极低"
     */
    public static final Map<String,String> stoneSuccessStringList = new HashMap<>();

    public static final Map<Double,Double> stoneCostList = new HashMap<>();
    public String stoneName;

    static{
        stoneSuccessList.put("普通",1.0);
        stoneSuccessList.put("中等",1.5);
        stoneSuccessList.put("高等",2.5);
        stoneSuccessList.put("完美",5.0);
        stoneSuccessList.put("会员",10.0);

        stoneSuccessStringList.put("普通","极低");
        stoneSuccessStringList.put("中等","较低");
        stoneSuccessStringList.put("高等","一般");
        stoneSuccessStringList.put("完美","较高");
        stoneSuccessStringList.put("会员","极高");

        stoneCostList.put(1.0, 2500.0);
        stoneCostList.put(1.5, 5000.0);
        stoneCostList.put(2.5, 20000.0);
        stoneCostList.put(5.0, 40000.0);
        stoneCostList.put(10.0, 80000.0);
    }
    public StoneDataManager(String name){
        this.stoneName = name;
    }

    /**
     * 从配置文件读取石头列表的方法
     */
    public static void init(){
        if(!stones.isEmpty()){
            stones.clear();
        }
        Set<String> stoneNameList = FileUtil.getDataFileKeys("stone.yml",false);
        YamlConfiguration file = FileUtil.getCustomFileYaml("stone.yml");
        for (String s : stoneNameList) {
            String fileName = "stone.yml";
            String identifier = s;
            String name = file.getString(s+".Name");
            Material type = Material.valueOf(file.getString(s+".Type"));
            int customModelData = Integer.parseInt(file.getString(s+".Data"));
            int[] dropLevel = Arrays.stream(file.getString(s+".dropLevel").split(" ")).mapToInt(Integer::parseInt).toArray();
            List<String> lore = file.getStringList(s+".Lore");
            ConfigurationSection cs = file.getConfigurationSection(s+".Chance");
            List<Double> probability = new ArrayList<>();
            Set<String> chanceKeys = cs.getKeys(false);
            List<String> chanceList = chanceKeys.stream().map(Double::parseDouble).sorted().map(String::valueOf).collect(Collectors.toList());
            for (int i = 0; i < chanceList.size(); i++) {
                probability.add(file.getDouble(s+".Chance."+(i+1)));
            }
            Stone stone = new Stone(identifier,name,lore,probability,type,dropLevel,customModelData);
            stones.put(identifier,stone);
        }
    }

    public ItemStack createStone(){
        if(!stones.keySet().contains(this.stoneName)){
            return null;
        }
        Stone stone = stones.get(this.stoneName);
        ItemStack stoneItem = new ItemStack(stone.getType());
        ItemMeta im = stoneItem.getItemMeta();
        im.setDisplayName(stone.getName());
        im.setLore(stone.getLore());
        stoneItem.setItemMeta(im);
        NBT.modify(stoneItem,nbt -> {
            nbt.setString("refinementstone",this.stoneName);
        });
        return stoneItem;
    }

    /**
     * 工具方法，判断石头是否符合要求
     * @param itemStone
     * @return
     */
    public static boolean isStoneLegal(ItemStack itemStone){
        for (String s : stones.keySet()) {
            NBTItem nbtItemStone = new NBTItem(itemStone);
            if(nbtItemStone.getString("refinementstone").equals(s)){
                return true;
            }
        }
        return false;
    }

    public static Stone getStone(ItemStack itemStone){
        NBTItem nbtItemStone = new NBTItem(itemStone);
        return stones.get(nbtItemStone.getString("refinementstone"));
    }
}
