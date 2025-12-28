package vip.mcsj.www.karrefinement.datamanager;


import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vip.mcsj.www.karrefinement.object.ProtectPaper;
import vip.mcsj.www.karrefinement.utils.FileUtil;
import vip.mcsj.www.karrefinement.utils.ReflectionUtils;

import java.util.*;

/**
 * 控制淬炼保护符的信息的类
 */
public class PaperDataManager {

    public static Map<String, ProtectPaper> papers = new HashMap<>();

    public static Set<Material> paperMaterials = new HashSet<>();

    private ItemStack equipmentItem;

    private String paperName;
    /**
     * 此方法用于后续获取装备保护符等级
     * @param equipmentItem
     */
    public PaperDataManager(ItemStack equipmentItem){
        this.equipmentItem = equipmentItem;
    }

    /**
     * 此构造方法用于创建保护符
     * @param paperName
     */
    public PaperDataManager(String paperName){
        this.paperName = paperName;
    }

    public static void init(){
        if(!papers.isEmpty()){
            papers.clear();
        }
        YamlConfiguration customFileYaml = FileUtil.getCustomFileYaml("protectpaper.yml");
        Set<String> keys = customFileYaml.getKeys(false);
        for (String key : keys) {
            String name = customFileYaml.getString(key+".Name");
            Material type = Material.valueOf(customFileYaml.getString(key+".Type"));
            int customModelData = customFileYaml.getInt(key+".CustomModelData");
            int data = customFileYaml.getInt(key+"Data");
            int level = customFileYaml.getInt(key+".Level");
            List<String> lores = customFileYaml.getStringList(key+".Lore");
            ProtectPaper paper = new ProtectPaper(key,name,lores,type,data,customModelData,level);
            papers.put(key,paper);
            paperMaterials.add(type);
        }
    }

    public ItemStack createProtectedPaper(){
        ProtectPaper paper = papers.get(this.paperName);
        ItemStack paperItem = new ItemStack(paper.getType(),1, (short) paper.getData());
        ItemMeta im = paperItem.getItemMeta();
        im.setDisplayName(paper.getName());
        //setLore
        im.setLore(paper.getLore());
        paperItem.setItemMeta(im);
        ReflectionUtils.setCustomModelData(paperItem,paper.getCustomModelData());
        NBT.modify(paperItem,nbt -> {
            nbt.setInteger("protector",paper.getLevel());
        });
        return paperItem;
    }

    /**
     * 实现保护符附装备上的方法
     * @param itemPaper
     * @param equipmentItem
     * @return
     */
    public static boolean protectorPaperUp(ItemStack itemPaper,ItemStack equipmentItem){
        ItemMeta paperMeta = itemPaper.getItemMeta();
        ItemMeta equipmentMeta = equipmentItem.getItemMeta();
        int refinementLevel = new NBTItem(equipmentItem).getInteger("refinement");
        int nowLevel = new NBTItem(equipmentItem).getInteger("protector");
        int willLevel = new NBTItem(itemPaper).getInteger("protector");
        String paperIdentifier = getPaperIdentifier(itemPaper);
        List<String> lores = new ArrayList<>();
        //如果淬炼等级为0
        if(refinementLevel <= 0) {
            return false;
        }
//            refinementLevel = KarUtils.getIntFromMap(EquipmentDataManager.equipmentData,equipmentMeta.getLore().get(1));
//            willLevel = ProtectorData.get(KarUtils.regxChinese(paperMeta.getDisplayName()));
        //如果淬炼等级小于保护符等级
        if (refinementLevel < willLevel) {
            return false;
        }
        //没保，且将要附上的保护符等级小于淬炼等级
        if (nowLevel == 0) {
            lores = new ArrayList<>();
            List<String> metaLore = equipmentMeta.getLore();


            Map<String, List<String>> equipmentInfoLore = EquipmentDataManager.getEquipmentInfoLore(equipmentItem);

            for (String s : equipmentInfoLore.keySet()) {
                List<String> strList = equipmentInfoLore.get(s);
                metaLore.removeAll(strList);
            }

            List<String> sortOrder = EquipmentDataManager.sortOrder;
            for (String s : sortOrder) {
                if(s.equals("{lore}")){
                    lores.addAll(metaLore);
                }

                if(s.equals("{refinement}")){
                    if(equipmentInfoLore.containsKey("refinement")){
                        lores.addAll(equipmentInfoLore.get("refinement"));
                    }
                }


                if(s.equals("{spestone}")){
                    if(equipmentInfoLore.containsKey("spestone")){
                        lores.addAll(equipmentInfoLore.get("spestone"));
                    }
                }

                if(s.equals("{paper}")){
                    lores.add(papers.get(paperIdentifier).getName());
                }

                if(s.equals("{soul}")){
                    if(equipmentInfoLore.containsKey("soul")){
                        lores.addAll(equipmentInfoLore.get("soul"));
                    }
                }
            }


            equipmentMeta.setLore(lores);
            equipmentItem.setItemMeta(equipmentMeta);
            NBT.modify(equipmentItem,nbt -> {
                nbt.setInteger("protector",willLevel);
            });
            return true;
            //有Lore有保
        } else{
            lores = new ArrayList<>();
            List<String> metaLore = equipmentMeta.getLore();


            if (willLevel > nowLevel) {
                Map<String, List<String>> equipmentInfoLore = EquipmentDataManager.getEquipmentInfoLore(equipmentItem);

                for (String s : equipmentInfoLore.keySet()) {
                    List<String> strList = equipmentInfoLore.get(s);
                    metaLore.removeAll(strList);
                }

                List<String> sortOrder = EquipmentDataManager.sortOrder;
                for (String s : sortOrder) {

                    if(s.equals("{lore}")){
                        lores.addAll(metaLore);
                    }

                    if(s.equals("{refinement}")){
                        if(equipmentInfoLore.containsKey("refinement")){
                            lores.addAll(equipmentInfoLore.get("refinement"));
                        }
                    }


                    if(s.equals("{spestone}")){
                        if(equipmentInfoLore.containsKey("spestone")){
                            lores.addAll(equipmentInfoLore.get("spestone"));
                        }
                    }

                    if(s.equals("{paper}")){
                        for (String s1 : papers.keySet()) {
                            if(papers.get(s1).getLevel() == nowLevel){
                                lores.remove(papers.get(s1).getName());
                            }
                        }
                        lores.add(papers.get(paperIdentifier).getName());
                    }

                    if(s.equals("{soul}")){
                        if(equipmentInfoLore.containsKey("soul")){
                            lores.addAll(equipmentInfoLore.get("soul"));
                        }
                    }
                }


                equipmentMeta.setLore(lores);
                equipmentItem.setItemMeta(equipmentMeta);
                NBT.modify(equipmentItem,nbt -> {
                    nbt.setInteger("protector",willLevel);
                });
                return true;
            }
            return false;
        }
    }

    public static boolean isPaperLegal(ItemStack paperItem){
        if(paperItem == null || paperItem.getType() == Material.AIR){
            return false;
        }
        //检查材料类型
        if(!paperMaterials.contains(paperItem.getType())){
            return false;
        }
        int paperLevel = new NBTItem(paperItem).getInteger("protector");
        for (String s : papers.keySet()) {
            if(papers.get(s).getLevel() == paperLevel){
                return true;
            }
        }
        return false;
    }

    public static String getPaperIdentifier(ItemStack itemPaper){
        int paperLevel = new NBTItem(itemPaper).getInteger("protector");
        return getPaperIdentifier(paperLevel);
    }

    public static String getPaperIdentifier(int level){
        for (String s : papers.keySet()) {
            if(papers.get(s).getLevel() == level){
                return s;
            }
        }
        return null;
    }


    public int getPaperLevel(){
//        return NBT.get(this.equipmentItem,nbt -> nbt.getInteger("protector"));
        return new NBTItem(this.equipmentItem).getInteger("protector");
    }

}
