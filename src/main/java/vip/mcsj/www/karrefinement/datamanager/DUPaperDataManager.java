package vip.mcsj.www.karrefinement.datamanager;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vip.mcsj.www.karrefinement.object.DirectUpgradePaper;
import vip.mcsj.www.karrefinement.utils.FileUtil;
import vip.mcsj.www.karrefinement.utils.ReflectionUtils;

import java.util.*;

/**
 * 控制直升符的信息的类
 */
public class DUPaperDataManager {
    public static Map<String, DirectUpgradePaper> duPapers = new HashMap<>();

    public static Set<Material> duMaterials = new HashSet<>();


//    /**
//     * 用于后续应用直升符的操作时对物品做条件判断
//     * @param equipmentItem
//     */
//    public DUPaperDataManager(ItemStack equipmentItem){this.equipmentItem = equipmentItem;}

    public static void init(){
        if(!duPapers.isEmpty()){
            duPapers.clear();
        }
        YamlConfiguration customFileYaml = FileUtil.getCustomFileYaml("directupgradepaper.yml");
        Set<String> keys = customFileYaml.getKeys(false);
        for (String key : keys) {
            String name = customFileYaml.getString(key+".Name");
            Material type = Material.valueOf(customFileYaml.getString(key+".Type"));
            int data = customFileYaml.getInt(key+".Data");
            int customModelData = customFileYaml.getInt(key+".CustomModelData");
            int level = customFileYaml.getInt(key+".Level");
            List<String> lores = customFileYaml.getStringList(key+".Lore");
            DirectUpgradePaper duPaper = new DirectUpgradePaper(level,name,lores,type,data,customModelData);
            duPapers.put(key,duPaper);
            duMaterials.add(type);
        }
    }

    public static ItemStack createDUPaper(String identifier){
        DirectUpgradePaper duPaper = duPapers.get(identifier);
        ItemStack duPaperItem = new ItemStack(duPaper.getMaterial(), 1, (short) duPaper.getData());
        ItemMeta im = duPaperItem.getItemMeta();
        im.setDisplayName(duPaper.getName());
        im.setLore(duPaper.getLore());
        duPaperItem.setItemMeta(im);
        ReflectionUtils.setCustomModelData(duPaperItem,duPaper.getCustomModelData());
        NBT.modify(duPaperItem,nbt -> {
            nbt.setInteger("dulevel",duPaper.getLevel());
        });
        return duPaperItem;
    }

    public static boolean duPaperUp(ItemStack itemUpPaper, ItemStack equipmentItem, Player p){
        int refinementLevel = new NBTItem(equipmentItem).getInteger("refinement");
        int upLevel = new NBTItem(itemUpPaper).getInteger("dulevel");
        if(refinementLevel == 0){
            EquipmentDataManager manager = new EquipmentDataManager(equipmentItem);
            for (int i = 0; i < upLevel; i++) {
                manager.injuryUpStar();
            }
            return true;
        }
        if(refinementLevel >= upLevel){
            return false;
        }
        int num = upLevel - refinementLevel;
        for (int i = 0; i < num; i++) {
            EquipmentDataManager manager = new EquipmentDataManager(equipmentItem,p);
            manager.injuryUpStar();
        }
        return true;
    }

    public static boolean isDUPaperLegal(ItemStack duPaperItem){
        if(duPaperItem == null || duPaperItem.getType() == Material.AIR){
            return false;
        }
        if(!duMaterials.contains(duPaperItem.getType())){
            return false;
        }
        int duPaperLevel = new NBTItem(duPaperItem).getInteger("dulevel");
        for (String s : duPapers.keySet()) {
            if(duPapers.get(s).getLevel() == duPaperLevel){
                return true;
            }
        }
        return false;
    }
}
