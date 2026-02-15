package vip.mcsj.www.karrefinement.datamanager;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.object.Stone;
import vip.mcsj.www.karrefinement.utils.FileUtil;
import vip.mcsj.www.karrefinement.utils.ReflectionUtils;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 控制淬炼石信息的类
 */
public class StoneDataManager {

    public static Map<String, Stone> stones = new HashMap<>();
    public String stoneNbt;

    public static boolean enableStoneChnace = false;

    public StoneDataManager(String name){
        this.stoneNbt = name;
    }

    /**
     * 从配置文件读取石头列表的方法
     */
    public static void init(){
        if(!stones.isEmpty()){
            stones.clear();
        }

        FileConfiguration config = KarRefinement.instance.getConfig();
        enableStoneChnace = config.getBoolean("settings.enablestonechance");

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
            String nbt = file.getString(s+".Nbt");
            ConfigurationSection cs = file.getConfigurationSection(s+".Chance");
            List<Double> probability = new ArrayList<>();
            Set<String> chanceKeys = cs.getKeys(false);
            List<String> chanceList = chanceKeys.stream().map(Double::parseDouble).sorted().map(String::valueOf).collect(Collectors.toList());
            for (int i = 0; i < chanceList.size(); i++) {
                probability.add(file.getDouble(s+".Chance."+(i+1)));
            }
            Stone stone = new Stone(identifier,name,lore,probability,type,data,dropLevel,customModelData,nbt);
            stones.put(nbt,stone);
        }
    }

    public ItemStack createStone(){
        if(!stones.keySet().contains(this.stoneNbt)){
            return null;
        }
        Stone stone = stones.get(this.stoneNbt);
        ItemStack stoneItem = new ItemStack(stone.getType(),1, (short) stone.getData());
        ItemMeta im = stoneItem.getItemMeta();
        im.setDisplayName(stone.getName());
        im.setLore(stone.getLore());
        stoneItem.setItemMeta(im);
        ReflectionUtils.setCustomModelData(stoneItem,stone.getCustomModelData());
        NBT.modify(stoneItem,nbt -> {
            nbt.setString("refinementstone",this.stoneNbt);
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
            if(!nbtItemStone.hasTag("refinementstone")){
                break;
            }
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

    public static List<Double> getStoneChance(ItemStack itemStone){
        Stone stone = getStone(itemStone);
        return stone.getProbability();
    }
}
