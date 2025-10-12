package vip.mcsj.www.karrefinement.datamanager;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vip.mcsj.www.karrefinement.object.SpeStone;
import vip.mcsj.www.karrefinement.utils.FileUtil;
import vip.mcsj.www.karrefinement.utils.KarUtils;

import java.util.*;

public class SpecialStoneDataManager {

    public static Map<String, SpeStone> speStones = new HashMap<>();

    public static Set<String> nbtKeys = new HashSet<>();

    public static Set<Material> stoneMaterials = new HashSet<>();
    //附加宝石防御列表
    public static Map<Integer,Integer> specialProtectStoneList = new HashMap<>();
    //保护等级：宝石名
    public static Map<Integer,String> specialProtectStoneToLevelList = new HashMap<>();

    private String speStoneName;
//    public int getSpeStoneLevel(){
//        return NBT.get(speStoneItem,nbt -> nbt.getInteger("stone"));
//    }


    public static void init(){
        if(!speStones.isEmpty()){
            speStones.clear();
        }
        if(!nbtKeys.isEmpty()){
            nbtKeys.clear();
        }
        YamlConfiguration file = FileUtil.getCustomFileYaml("spestone.yml");
        for (String key : file.getKeys(false)) {
            String name = file.getString(key+".Name");
            Material type = Material.valueOf(file.getString(key+".Type"));
            int data = file.getInt(key+".Data");
            int cmd = file.getInt(key+".CustomModelData");
            int level = file.getInt(key+".Level");
            String nbtKey = file.getString(key+".Nbtkey");
            List<String> canUseItems = file.getStringList(key+".Items");
            List<String> lore = file.getStringList(key+".Lore");
            List<String> equipmentLore = file.getStringList(key+".Equipmentlore");
            SpeStone speStone = new SpeStone(key,name,lore,level,nbtKey,canUseItems,type,data,cmd,equipmentLore);
            speStones.put(key,speStone);
            stoneMaterials.add(type);
        }

        for (String s : speStones.keySet()) {
            nbtKeys.add(speStones.get(s).getNbtKey());
        }
    }

    public SpecialStoneDataManager(String speStoneName){
        this.speStoneName = speStoneName;
    }

    public ItemStack createSpeStone(){
        SpeStone speStone = speStones.get(this.speStoneName);
        ItemStack speStoneItem = new ItemStack(speStone.getType(),1, (short) speStone.getData());
        ItemMeta im = speStoneItem.getItemMeta();
        im.setDisplayName(speStone.getName());
        im.setLore(speStone.getLore());
        speStoneItem.setItemMeta(im);
        NBT.modify(speStoneItem,nbt -> {
            nbt.setInteger(speStone.getNbtKey(),speStone.getLevel());
        });
        return speStoneItem;
    }

    public static boolean isSpeStoneLegal(ItemStack speStoneItem){
        if(speStoneItem == null || speStoneItem.getType() == Material.AIR){
            return false;
        }
        NBTItem nbtItem = new NBTItem(speStoneItem);
        //检查物品材料类型
        if(!stoneMaterials.contains(speStoneItem.getType())){
            return false;
        }
        for (String s : speStones.keySet()) {
            SpeStone speStone = speStones.get(s);
            //先检查有无标签
            if(nbtItem.hasTag(speStone.getNbtKey())){
                //再检查宝石等级
                if(nbtItem.getInteger(speStone.getNbtKey()) == speStone.getLevel()){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 通过item获取SpeStone
     * @param speStoneItem
     * @return
     */
    public static SpeStone getSpeStone(ItemStack speStoneItem){
        NBTItem nbtItem = new NBTItem(speStoneItem);
        for (String s : speStones.keySet()) {
            SpeStone speStone = speStones.get(s);
            if(nbtItem.hasTag(speStone.getNbtKey())){
                if(nbtItem.getInteger(speStone.getNbtKey()) == speStone.getLevel()){
                    return speStone;
                }
            }
        }
        return null;
    }

    /**
     * 通过nbt键和值获取SpeStone
     * @param key
     * @param value
     * @return
     */
    public static SpeStone getSpeStone(String key,int value){
        for (String s : speStones.keySet()) {
            SpeStone speStone = speStones.get(s);
            if(speStone.getNbtKey().equals(key)){
                if(speStone.getLevel() == value){
                    return speStone;
                }
            }
        }
        return null;
    }
    public static boolean specialStoneUp(ItemStack speStoneItem,ItemStack equipmentItem){
//        speStones.forEach((k,v) -> System.out.println(k+":"+v));
        ItemMeta stoneMeta = speStoneItem.getItemMeta();
        ItemMeta equipmentMeta = equipmentItem.getItemMeta();
        //将要镶嵌的宝石
        SpeStone speStone = getSpeStone(speStoneItem);

        //装备上有的宝石
        List<SpeStone> speStoneList = new ArrayList<>();
        //去重后的宝石列表
        List<SpeStone> speStoneList2 = new ArrayList<>();
        boolean lowerThenNewStone = false;
        //查找装备上有的宝石
        for (String nbtKey : nbtKeys) {
            NBTItem equipmentNBTItem = new NBTItem(equipmentItem);
            if(equipmentNBTItem.hasTag(nbtKey)){
                int NBTValue = equipmentNBTItem.getInteger(nbtKey);
                speStoneList.add(getSpeStone(nbtKey, NBTValue));
                //如果现有同类宝石等级比将要镶嵌的宝石等级低，不加入
                if(nbtKey.equals(speStone.getNbtKey()) && NBTValue < speStone.getLevel()){
                    lowerThenNewStone = true;
                    continue;
                }
                if(nbtKey.equals(speStone.getNbtKey())){
                    continue;
                }
                speStoneList2.add(getSpeStone(nbtKey,NBTValue));
            }
        }


        /**
         * speStoneList.size() == speStoneList2.size() 有两种情况
         * 1.有宝石，但没有同类宝石
         * 2.有宝石，但同类宝石高于将要赋予的宝石
         */
        //如果没有宝石
        if(speStoneList.size() == 0){
            addItemSpeStoneInfo(speStone,speStoneList2,equipmentItem);
            return true;
        //如果有宝石，但没有将要镶嵌的宝石的同类宝石
        }else if(speStoneList.size() == speStoneList2.size()){
            removeNowItemRefinementInfo(speStoneList,equipmentItem);
            addItemSpeStoneInfo(speStone,speStoneList2,equipmentItem);
            return true;
        //如果有宝石，且原有同类宝石比将要镶嵌的宝石等级低
        }else if(speStoneList2.size() < speStoneList.size() && lowerThenNewStone){
            removeNowItemRefinementInfo(speStoneList,equipmentItem);
            addItemSpeStoneInfo(speStone,speStoneList2,equipmentItem);
            System.out.println(speStone.getEquipmentLore());
            return true;
        }
        return false;
    }

    /**
     *
     * @param speStone  新镶嵌宝石
     * @param originSpeStoneList 去重(去掉较低级的同类宝石)后的装备原有宝石列表
     * @param equipmentItem 装备
     */
    private static void addItemSpeStoneInfo(SpeStone speStone,List<SpeStone> originSpeStoneList,ItemStack equipmentItem) {
        ItemMeta im = equipmentItem.getItemMeta();
        List<String> lores = im.getLore();
        if (lores == null) {
            lores = new ArrayList<>();
        }

        lores.add(KarUtils.applyTextFormatting(KarUtils.createColorGradientMessage(true))
                + "§e§l宝石镶嵌"
                + KarUtils.applyTextFormatting(KarUtils.createColorGradientMessage(false)));
        lores.addAll(speStone.getEquipmentLore());
        for (SpeStone speStone1 : originSpeStoneList) {
            lores.addAll(speStone1.getEquipmentLore());
        }
        im.setLore(lores);
        equipmentItem.setItemMeta(im);

        NBT.modify(equipmentItem, nbt -> {
            nbt.setInteger(speStone.getNbtKey(), speStone.getLevel());
        });
        for (SpeStone stone : originSpeStoneList) {
            NBT.modify(equipmentItem, nbt -> {
                nbt.setInteger(stone.getNbtKey(), stone.getLevel());
            });
        }
    }

    /**
     *
     * @param originSpeStoneList 装备原有宝石列表
     * @param equipmentItem 装备
     */
    private static void removeNowItemRefinementInfo(List<SpeStone> originSpeStoneList,ItemStack equipmentItem) {
        ItemMeta im = equipmentItem.getItemMeta();
        List<String> lores = im.getLore();
        if (lores == null) {
            lores = new ArrayList<>();
        }
        for (int i = 0; i < lores.size(); i++) {
            if (lores.get(i).contains("宝石镶嵌")) {
                lores.remove(i);
            }
        }
        for (SpeStone speStone : originSpeStoneList) {
            lores.removeAll(speStone.getEquipmentLore());
            NBT.modify(equipmentItem,nbt -> {
                nbt.removeKey(speStone.getNbtKey());
            });
        }
        im.setLore(lores);
        equipmentItem.setItemMeta(im);
    }

    public static boolean specialProtectStoneUp(ItemStack speStone,ItemStack equipmentItem){
        ItemMeta stoneMeta = speStone.getItemMeta();
        ItemMeta equipmentMeta = equipmentItem.getItemMeta();
//        int nowStoneLevel = NBT.get(equipmentItem,nbt -> nbt.getInteger("protectstone"));
        int nowStoneLevel = new NBTItem(equipmentItem).getInteger("protectstone");
//        int willStoneLevel = NBT.get(speStone,nbt -> nbt.getInteger("protectstone"));
        int willStoneLevel = new NBTItem(speStone).getInteger("protectstone");
        List<String> lores;
        //没宝石时直接上
        if(nowStoneLevel == 0) {
            //将镶嵌宝石附加防御值
            int stoneProtect = specialProtectStoneList.get(willStoneLevel);
            String stoneName = stoneMeta.getDisplayName();

            lores = equipmentMeta.getLore();
            if(lores == null){
                lores = new ArrayList<>();
            }
            lores.add(KarUtils.applyTextFormatting(KarUtils.createColorGradientMessage(true))
                    +"§e§l宝石镶嵌"
                    +KarUtils.applyTextFormatting(KarUtils.createColorGradientMessage(false)));
            lores.add(stoneName + " §f- §a附加防御+§6"+stoneProtect);
            equipmentMeta.setLore(lores);
            equipmentItem.setItemMeta(equipmentMeta);
            NBT.modify(equipmentItem,nbt -> {
                nbt.setInteger("protectstone",willStoneLevel);
            });
            return true;
        }
        //有宝石时，判断已镶嵌宝石等级是否小于将镶嵌宝石等级
        if(nowStoneLevel < willStoneLevel){
            //将镶嵌宝石附加防御值
            int nowStoneProtect = specialProtectStoneList.get(nowStoneLevel);
            String nowStoneName = specialProtectStoneToLevelList.get(nowStoneLevel);

            int willStoneProtect = specialProtectStoneList.get(willStoneLevel);
            String willStoneName = stoneMeta.getDisplayName();

            lores = equipmentMeta.getLore();
            for (int i = 0; i < lores.size(); i++) {
                if(lores.get(i).contains("宝石镶嵌")){
                    lores.remove(i);
                }
            }
            lores.remove(nowStoneName + " §f- §a附加防御+§6"+nowStoneProtect);
            lores.add(KarUtils.applyTextFormatting(KarUtils.createColorGradientMessage(true))
                    +"§e§l宝石镶嵌"
                    +KarUtils.applyTextFormatting(KarUtils.createColorGradientMessage(false)));
            lores.add(willStoneName + " §f- §a附加防御+§6"+willStoneProtect);
            equipmentMeta.setLore(lores);
            equipmentItem.setItemMeta(equipmentMeta);
            NBT.modify(equipmentItem,nbt -> {
                nbt.setInteger("protectstone",willStoneLevel);
            });
            return true;
        }
        return false;
    }

    /**
     * 判断要用这个宝石的装备是否在可用装备列表里
     * @param speStone
     * @param equipmentItem
     * @return
     */
    public static boolean isEquipmentLegal(SpeStone speStone,ItemStack equipmentItem){
        List<String> canUseItems = speStone.getCanUseItems();
        for (String canUseItem : canUseItems) {
            List<String> materials = EquipmentDataManager.canRefinementEquipment.get(canUseItem);
            for (String material : materials) {
                if(equipmentItem.getType().name().equals(material)){
                    return true;
                }
            }
        }
        return false;
    }
}
