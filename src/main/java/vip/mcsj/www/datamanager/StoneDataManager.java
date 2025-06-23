package vip.mcsj.www.datamanager;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
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
    public String stoneName;

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
            int data = file.getInt(s+".Data");
            int customModelData = Integer.parseInt(file.getString(s+".CustomModelData"));
            int[] dropLevel = Arrays.stream(file.getString(s+".dropLevel").split(" ")).mapToInt(Integer::parseInt).toArray();
            List<String> lore = file.getStringList(s+".Lore");
            ConfigurationSection cs = file.getConfigurationSection(s+".Chance");
            List<Double> probability = new ArrayList<>();
            Set<String> chanceKeys = cs.getKeys(false);
            List<String> chanceList = chanceKeys.stream().map(Double::parseDouble).sorted().map(String::valueOf).collect(Collectors.toList());
            for (int i = 0; i < chanceList.size(); i++) {
                probability.add(file.getDouble(s+".Chance."+(i+1)));
            }
            Stone stone = new Stone(identifier,name,lore,probability,type,data,dropLevel,customModelData);
            stones.put(identifier,stone);
        }
    }

    public ItemStack createStone(){
        if(!stones.keySet().contains(this.stoneName)){
            return null;
        }
        Stone stone = stones.get(this.stoneName);
        ItemStack stoneItem = new ItemStack(stone.getType(),1, (short) stone.getData());
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
