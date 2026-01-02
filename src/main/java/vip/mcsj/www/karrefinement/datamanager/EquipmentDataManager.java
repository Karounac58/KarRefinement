package vip.mcsj.www.karrefinement.datamanager;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.object.*;
import vip.mcsj.www.karrefinement.utils.FileUtil;
import vip.mcsj.www.karrefinement.utils.KarUtils;
import vip.mcsj.www.karrefinement.utils.RandomLoreUtils;

import java.util.*;


/**
 * 控制装备信息的类
 */
public class EquipmentDataManager {

    public static final Map<String, List<String>> canRefinementEquipment = new HashMap<>();

    public static final Map<Integer,Double> forgeSuccessList = new HashMap<>();

    public static final Map<String,String> chinesenames = new HashMap<>();

    public static boolean allowAfterItemIsRefinement = true;

    public static int transformCost = 1000000;

    public static boolean allowTransformOther = true;

    public static String mainLore = "";
    public static String speStoneLore = "";

    public static List<String> sortOrder =  new ArrayList<>();

    public static Map<String, JoinMessage>  joinMessage = new HashMap<>();

    public static boolean enableDisplayNameInfo = true;
    public static String displayNameSuffix = "";

    public static int broadcastLevel = 6;

    private ItemStack equipmentItem;

    private Player p;

    public EquipmentDataManager(ItemStack equipmentItem) {
        this.equipmentItem = equipmentItem;
    }

    public EquipmentDataManager(ItemStack equipmentItem, Player p) {
        this.equipmentItem = equipmentItem;
        this.p = p;
    }

    public static void init() {
        if (!canRefinementEquipment.isEmpty()) {
            canRefinementEquipment.clear();
        }
        if(!chinesenames.isEmpty()) {
            chinesenames.clear();
        }
        YamlConfiguration customFileYaml = FileUtil.getCustomFileYaml("items.yml");
//        List<String> hand = customFileYaml.getStringList("Hand");
//        List<String> helmet = customFileYaml.getStringList("Helmet");
//        List<String> chestPlate = customFileYaml.getStringList("Chestplate");
//        List<String> leggings = customFileYaml.getStringList("Leggings");
//        List<String> boots = customFileYaml.getStringList("Boots");
//
//        canRefinementEquipment.put("Hand", hand);
//        canRefinementEquipment.put("Helmet", helmet);
//        canRefinementEquipment.put("Chestplate", chestPlate);
//        canRefinementEquipment.put("Leggings", leggings);
//        canRefinementEquipment.put("Boots", boots);
        Set<String> keys = customFileYaml.getKeys(false);
        for (String key : keys) {
            List<String> stringList = customFileYaml.getStringList(key);
            canRefinementEquipment.put(key, stringList);
            for (String s : stringList) {
                KarRefinement.ItemType type = new KarRefinement.ItemType(key,s);
                KarRefinement.types.add(type);
            }
        }
        KarRefinement.instance.reloadConfig();
        mainLore = KarRefinement.instance.getConfig().getString("mainLore");
        speStoneLore = KarRefinement.instance.getConfig().getString("speStoneLore");
        enableDisplayNameInfo = KarRefinement.instance.getConfig().getBoolean("DisplayNameInfo.enabled");
        displayNameSuffix = KarRefinement.instance.getConfig().getString("DisplayNameInfo.suffix");
        sortOrder = KarRefinement.instance.getConfig().getStringList("settings.SortOrder");
        broadcastLevel = KarRefinement.instance.getConfig().getInt("broadcastlevel");
        ConfigurationSection jmCS = KarRefinement.instance.getConfig().getConfigurationSection("settings.JoinMessage");
        Set<String> keys1 = jmCS.getKeys(false);
        for (String key : keys1) {
            String permission =  jmCS.getString(key+".permission");
            List<String> message = jmCS.getStringList(key+".msg");
            joinMessage.put(key,new JoinMessage(permission,message));
        }
        YamlConfiguration chineseNameYaml = FileUtil.getCustomFileYaml("chinesename.yml");
        chineseNameYaml.getKeys(false).forEach(key -> {
            chinesenames.put(key,chineseNameYaml.getString(key));
        });
    }

    public static void initForgeData(){
        if(!forgeSuccessList.isEmpty()){
            forgeSuccessList.clear();
        }
        YamlConfiguration customFileYaml = FileUtil.getCustomFileYaml("forge.yml");
        Set<String> chanceSet = customFileYaml.getKeys(false);
        for (String key : chanceSet) {
            Double chance = customFileYaml.getDouble(key+".Chance");
            forgeSuccessList.put(Integer.parseInt(key),chance);
        }
    }

    public static void initTransformData(){
        YamlConfiguration customFileYaml = FileUtil.getCustomFileYaml("transform.yml");
        allowAfterItemIsRefinement =  customFileYaml.getBoolean("allowAfterItemIsRefinement");
        transformCost = customFileYaml.getInt("money");
        allowTransformOther =  customFileYaml.getBoolean("allowTransformOther");
    }

    /**
     * 获取装备淬炼等级(nbt)
     *
     * @return ntb等级
     */
    public int carifyEquipmentLevel() {
        //ItemMeta im = this.equipmentItem.getItemMeta();
//        int refinementLevel = NBT.get(this.equipmentItem,nbt -> nbt.getInteger("refinement"));
        return new NBTItem(this.equipmentItem).getInteger("refinement");
    }

    public static int carifyEquipmentLevel(ItemStack equipmentItem) {
        return new NBTItem(equipmentItem).getInteger("refinement");
    }

    public static int getEquipmentLevel(ItemStack item) {
        return new NBTItem(item).getInteger("refinement");
    }
    /**
     * 剑上星方法
     *
     * @return
     */
    public boolean injuryUpStar() {
        if (carifyEquipmentLevel() == LevelDataManager.levels.size()) {
            return false;
        }
        int oldRefinementLevel = carifyEquipmentLevel();
        Level newlevel = LevelDataManager.levels.get(oldRefinementLevel);
        String newEquipmentIdentifier = getEquipmentIdentifier(this.equipmentItem);
        List<String> newMainLore = newlevel.getMainLore();
        List<String> newExtractLore = newlevel.getExtractLores().get(newEquipmentIdentifier);
        //如果从0升星
        if (oldRefinementLevel == 0) {
            //设置武器淬炼信息
            addItemRefinementInfo(newMainLore, newExtractLore, 1);
            return true;
        }
        Level oldLevel = LevelDataManager.levels.get(oldRefinementLevel - 1);
        List<String> oldMainLore = oldLevel.getMainLore();
        String oldEquipmentIdentifier = getEquipmentIdentifier(this.equipmentItem);
        List<String> oldExtractLore = oldLevel.getExtractLores().get(newEquipmentIdentifier);
        //不是从0开始升星
        //移除原淬炼信息
        removeNowItemRefinementInfo(oldMainLore, oldExtractLore, oldRefinementLevel);
        //下一级的淬炼信息
        addItemRefinementInfo(newMainLore, newExtractLore, oldRefinementLevel + 1);

        return true;
    }

    public boolean setRefinementLevel(int level) {
        if(level > LevelDataManager.levels.size()) {
            return false;
        }

        int oldLevelNum = carifyEquipmentLevel();

        if(oldLevelNum == level) {
            return false;
        }


        if(level == 0){
            Level oldLevel = LevelDataManager.levels.get(oldLevelNum - 1);
            String newEquipmentIdentifier = getEquipmentIdentifier(equipmentItem);
            List<String> oldMainLore = oldLevel.getMainLore();
            List<String> oldExtractLore = oldLevel.getExtractLores().get(newEquipmentIdentifier);
            removeNowItemRefinementInfo(oldMainLore, oldExtractLore, oldLevelNum);
            return true;
        }
        Level newLevel =  LevelDataManager.levels.get(level - 1);
        String newEquipmentIdentifier = getEquipmentIdentifier(equipmentItem);
        List<String> newMainLore = newLevel.getMainLore();
        List<String> newExtractLore = newLevel.getExtractLores().get(newEquipmentIdentifier);

        if(oldLevelNum == 0) {
            addItemRefinementInfo(newMainLore, newExtractLore, level);
            return true;
        }
        Level oldLevel = LevelDataManager.levels.get(oldLevelNum - 1);
        List<String> oldMainLore = oldLevel.getMainLore();
        List<String> oldExtractLore = oldLevel.getExtractLores().get(newEquipmentIdentifier);

        removeNowItemRefinementInfo(oldMainLore, oldExtractLore, oldLevelNum);

        addItemRefinementInfo(newMainLore, newExtractLore, level);

        return true;

    }

    //
    public boolean setRefinementLevel(int level,Map<String,List<String>> map,int paperLevel,List<SpeStone> speStones,int soulLevel) {
        if(level > LevelDataManager.levels.size()) {
            return false;
        }

        int oldLevelNum = carifyEquipmentLevel();

        if(oldLevelNum == level) {
            return false;
        }


        if(level == 0){
            Level oldLevel = LevelDataManager.levels.get(oldLevelNum - 1);
            String newEquipmentIdentifier = getEquipmentIdentifier(equipmentItem);
            List<String> oldMainLore = oldLevel.getMainLore();
            List<String> oldExtractLore = oldLevel.getExtractLores().get(newEquipmentIdentifier);
            removeNowItemRefinementInfo(oldMainLore, oldExtractLore, oldLevelNum);
            return true;
        }
        Level newLevel =  LevelDataManager.levels.get(level - 1);
        String newEquipmentIdentifier = getEquipmentIdentifier(equipmentItem);
        List<String> newMainLore = newLevel.getMainLore();
        List<String> newExtractLore = newLevel.getExtractLores().get(newEquipmentIdentifier);

        if(oldLevelNum == 0) {
            addItemRefinementInfo(newMainLore, newExtractLore, level,map,paperLevel,speStones,soulLevel);
            return true;
        }
        Level oldLevel = LevelDataManager.levels.get(oldLevelNum - 1);
        List<String> oldMainLore = oldLevel.getMainLore();
        List<String> oldExtractLore = oldLevel.getExtractLores().get(newEquipmentIdentifier);

        removeNowItemRefinementInfo(oldMainLore, oldExtractLore, oldLevelNum,true);

        addItemRefinementInfo(newMainLore, newExtractLore, level,map,paperLevel,speStones,soulLevel);

        return true;

    }

    private void judgeSoul(int refinementLevel){
        int soulLevel = new NBTItem(this.equipmentItem).getInteger("infinite");
        if(soulLevel == 0){
            return;
        }
        if(refinementLevel > soulLevel || refinementLevel == 0){
            String soulIdentifier = InfiniteSoulManager.getSoulName(this.equipmentItem);
            ItemMeta itemMeta = this.equipmentItem.getItemMeta();
            List<String> lore = itemMeta.getLore();
            lore.remove(soulIdentifier);
            itemMeta.setLore(lore);
            this.equipmentItem.setItemMeta(itemMeta);
            NBT.modify(this.equipmentItem, nbt -> {
                nbt.removeKey("infinite");
            });
            this.p.sendMessage("§c无限耐久精魂破碎!");
        }
    }

    /**
     * 上星后给物品添加lore信息方法
     *
     * @param mainLore
     * @param extractLore
     * @param refinementNBTNum
     */
    private void addItemRefinementInfo(List<String> mainLore, List<String> extractLore, int refinementNBTNum) {
        ItemMeta im = equipmentItem.getItemMeta();
        if(enableDisplayNameInfo){
            String displayName = im.hasDisplayName() ? im.getDisplayName() : chinesenames.get(equipmentItem.getType().name());
            displayName +=  displayNameSuffix.replace("{level}",refinementNBTNum+"");
            im.setDisplayName(displayName);
        }
        //获取淬炼lore
        List<String> lores = new ArrayList<>();
        List<String> metaLore = im.getLore();
        if (metaLore == null) {
            metaLore = new ArrayList<>();
        }


        boolean isRandomLore = false;
        if(RandomLoreUtils.isRandomLore(extractLore)){
            extractLore = RandomLoreUtils.replaceWithRandom(extractLore);
            isRandomLore = true;
        }

        Map<String, List<String>> equipmentInfoLore = getEquipmentInfoLore(this.equipmentItem);
        for (String s : equipmentInfoLore.keySet()) {
            List<String> strList = equipmentInfoLore.get(s);
            metaLore.removeAll(strList);
        }

        //排序
        for (String s : sortOrder) {
            if(s.equals("{lore}")){
                lores.addAll(metaLore);
            }
            if(s.equals("{refinement}")){
                //添加淬炼lore
                lores.add(EquipmentDataManager.mainLore);
                lores.addAll(mainLore);
                lores.addAll(extractLore);
            }

            if(s.equals("{spestone}")){
                if(equipmentInfoLore.containsKey("spestone")){
                    lores.addAll(equipmentInfoLore.get("spestone"));
                }
            }

            if(s.equals("{paper}")){
                if(equipmentInfoLore.containsKey("paper")) {
                    lores.addAll(equipmentInfoLore.get("paper"));
                }
            }

            if(s.equals("{soul}")){
                if(equipmentInfoLore.containsKey("soul")){
                    lores.addAll(equipmentInfoLore.get("soul"));
                }
            }
        }



        im.setLore(lores);
        this.equipmentItem.setItemMeta(im);
        NBT.modify(this.equipmentItem, nbt -> {
            nbt.setInteger("refinement", refinementNBTNum);
        });
        if(isRandomLore) {
            RandomLoreUtils.addRandomLoreWithNBT(this.equipmentItem, extractLore);
        }

        judgeSoul(refinementNBTNum);
    }

    private void addItemRefinementInfo(List<String> mainLore, List<String> extractLore, int refinementNBTNum,Map<String,List<String>> map,int paperLevel,List<SpeStone> speStones,int soulLevel) {
        ItemMeta im = equipmentItem.getItemMeta();
        if(enableDisplayNameInfo){
            String displayName = im.hasDisplayName() ? im.getDisplayName() : chinesenames.get(equipmentItem.getType().name());
            displayName +=  displayNameSuffix.replace("{level}",refinementNBTNum+"");
            im.setDisplayName(displayName);
        }
        //获取淬炼lore
        List<String> lores = new ArrayList<>();
        List<String> metaLore = im.getLore();
        if (metaLore == null) {
            metaLore = new ArrayList<>();
        }


        boolean isRandomLore = false;
        if(RandomLoreUtils.isRandomLore(extractLore)){
            extractLore = RandomLoreUtils.replaceWithRandom(extractLore);
            isRandomLore = true;
        }

        Map<String, List<String>> equipmentInfoLore = map;
        for (String s : equipmentInfoLore.keySet()) {
            List<String> strList = equipmentInfoLore.get(s);
            metaLore.removeAll(strList);
        }

        //排序
        for (String s : sortOrder) {
            if(s.equals("{lore}")){
                lores.addAll(metaLore);
            }
            if(s.equals("{refinement}")){
                //添加淬炼lore
                lores.add(EquipmentDataManager.mainLore);
                lores.addAll(mainLore);
                lores.addAll(extractLore);
            }

            if(s.equals("{spestone}")){
                if(equipmentInfoLore.containsKey("spestone")){
                    lores.addAll(equipmentInfoLore.get("spestone"));
                }
            }

            if(s.equals("{paper}")){
                if(equipmentInfoLore.containsKey("paper")) {
                    lores.addAll(equipmentInfoLore.get("paper"));
                }
            }

            if(s.equals("{soul}")){
                if(equipmentInfoLore.containsKey("soul")){
                    lores.addAll(equipmentInfoLore.get("soul"));
                }
            }
        }



        im.setLore(lores);
        this.equipmentItem.setItemMeta(im);
        NBT.modify(this.equipmentItem, nbt -> {
            nbt.setInteger("refinement", refinementNBTNum);
        });
        if(isRandomLore) {
            RandomLoreUtils.addRandomLoreWithNBT(this.equipmentItem, extractLore);
        }
        NBT.modify(this.equipmentItem, nbt -> {
            nbt.setInteger("protector", paperLevel);
        });

        for (SpeStone speStone : speStones) {
            NBT.modify(this.equipmentItem, nbt -> {
                nbt.setInteger(speStone.getNbtKey(),speStone.getLevel());
            });
        }

        NBT.modify(this.equipmentItem, nbt -> {
            nbt.setInteger("infinite",soulLevel);
        });

        judgeSoul(refinementNBTNum);
    }

    public static Map<String,List<String>> getEquipmentInfoLore(ItemStack equipmentItem){

        Map<String,List<String>> map = new HashMap<>();
        NBTItem nbt = new NBTItem(equipmentItem);
        //获取淬炼lore
        List<String> refineLore = new ArrayList<>();
        int refinementLevel = carifyEquipmentLevel(equipmentItem);
        if(refinementLevel != 0){
            refineLore.add(EquipmentDataManager.mainLore);
            Level level = LevelDataManager.levels.get(refinementLevel - 1);
            refineLore.addAll(level.getMainLore());
            String equipmentIdentifier = getEquipmentIdentifier(equipmentItem);
            if(nbt.hasKey("randomlore")){
                refineLore.addAll(RandomLoreUtils.getRandomLore(equipmentItem));
            }else {
                refineLore.addAll(level.getExtractLores().get(equipmentIdentifier));
            }
            map.put("refinement",refineLore);
        }

        //获取宝石lore
        List<SpeStone> speStones = SpecialStoneDataManager.getSpeStones(equipmentItem);
        List<String> speStoneLore = new ArrayList<>();
        if(!speStones.isEmpty()){
            speStoneLore.add(EquipmentDataManager.speStoneLore);
            for (SpeStone speStone : speStones) {
                speStoneLore.addAll(speStone.getEquipmentLore());
            }
            map.put("spestone",speStoneLore);
        }

        //获取保护符lore
        String paperIdentifier = PaperDataManager.getPaperIdentifier(equipmentItem);
        String paperLore = "";
        if(paperIdentifier != null){
            paperLore = PaperDataManager.papers.get(paperIdentifier).getName();
            map.put("paper",Arrays.asList(paperLore));
        }

        String soulName = InfiniteSoulManager.getSoulName(equipmentItem);
        if(soulName != null){
            map.put("soul",Arrays.asList(soulName));
        }


        return map;
    }
    /**
     * 移除原淬炼信息方法，为装备上星方法的辅助方法(先移除，再上星)
     */
    private Map<String,List<String>> removeNowItemRefinementInfo(List<String> mainLore, List<String> extractLore, int refinementNBTNum) {
        ItemMeta im = equipmentItem.getItemMeta();
        if(enableDisplayNameInfo){
            String displayName = im.getDisplayName();
            String suffix = displayNameSuffix.replace("{level}",refinementNBTNum+"");
            displayName = displayName.replace(suffix,"");
            im.setDisplayName(displayName);
        }
        //移除淬炼lore
        List<String> lores = im.getLore();
        if (lores == null) {
            lores = new ArrayList<>();
        }
        int j = 0;
        for (int i = 0; i < lores.size(); i++) {
            if (lores.get(i).equals(EquipmentDataManager.mainLore)) {
                j = i;
                break;
            }
        }
        lores.remove(j);
        lores.removeAll(mainLore);

        if(RandomLoreUtils.hasRandomLore(this.equipmentItem)){
            extractLore = RandomLoreUtils.getRandomLore(this.equipmentItem);
        }

        lores.removeAll(extractLore);

        Map<String, List<String>> equipmentInfoLore = getEquipmentInfoLore(this.equipmentItem);
        if(equipmentInfoLore.containsKey("spestone")) {
            List<String> speStoneLore = equipmentInfoLore.get("spestone");
            for (String s : speStoneLore) {
                lores.remove(s);
            }
        }

        if(equipmentInfoLore.containsKey("paper")) {
            List<String> paperLore = equipmentInfoLore.get("paper");
            lores.removeAll(paperLore);
        }

        if(equipmentInfoLore.containsKey("soul")) {
            List<String> soulLore = equipmentInfoLore.get("soul");
            lores.removeAll(soulLore);
        }


        im.setLore(lores);
        this.equipmentItem.setItemMeta(im);



//        //移除宝石lore
//        List<SpeStone> speStones = SpecialStoneDataManager.getSpeStones(equipmentItem);
//        if(!speStones.isEmpty()){
//            SpecialStoneDataManager.removeNowSpeStoneLore(speStones,equipmentItem);
//        }
//
//        //移除保护符lore
//        String paperIdentifier = PaperDataManager.getPaperIdentifier(equipmentItem);
//        if(paperIdentifier != null){
//            ProtectPaper protectPaper = PaperDataManager.papers.get(paperIdentifier);
//            ItemMeta im2 = equipmentItem.getItemMeta();
//            List<String> lore = im2.getLore();
//            lore.remove(protectPaper.getName());
//            im2.setLore(lore);
//            this.equipmentItem.setItemMeta(im2);
//        }
//
//        //无限耐久精魂lore
//        String soulName = InfiniteSoulManager.getSoulName(equipmentItem);
//        if(soulName != null){
//            ItemMeta im3 = equipmentItem.getItemMeta();
//            List<String> lore = im3.getLore();
//            lore.remove(soulName);
//            im3.setLore(lore);
//            this.equipmentItem.setItemMeta(im3);
//        }




        NBT.modify(equipmentItem,nbt -> {
            nbt.removeKey("refinement");
        });
        NBT.modify(equipmentItem, nbt -> {
            nbt.removeKey("randomlore");
        });

        return equipmentInfoLore;
    }

    private Map<String,List<String>> removeNowItemRefinementInfo(List<String> mainLore, List<String> extractLore, int refinementNBTNum,boolean isTransform) {
        ItemMeta im = equipmentItem.getItemMeta();
        if(enableDisplayNameInfo){
            String displayName = im.getDisplayName();
            String suffix = displayNameSuffix.replace("{level}",refinementNBTNum+"");
            displayName = displayName.replace(suffix,"");
            im.setDisplayName(displayName);
        }
        //移除淬炼lore
        List<String> lores = im.getLore();
        if (lores == null) {
            lores = new ArrayList<>();
        }
        int j = 0;
        for (int i = 0; i < lores.size(); i++) {
            if (lores.get(i).equals(EquipmentDataManager.mainLore)) {
                j = i;
                break;
            }
        }
        lores.remove(j);
        lores.removeAll(mainLore);

        if(RandomLoreUtils.hasRandomLore(this.equipmentItem)){
            extractLore = RandomLoreUtils.getRandomLore(this.equipmentItem);
        }

        lores.removeAll(extractLore);

        Map<String, List<String>> equipmentInfoLore = getEquipmentInfoLore(this.equipmentItem);
        if(equipmentInfoLore.containsKey("spestone")) {
            List<String> speStoneLore = equipmentInfoLore.get("spestone");
            for (String s : speStoneLore) {
                lores.remove(s);
            }
        }

        if(equipmentInfoLore.containsKey("paper")) {
            List<String> paperLore = equipmentInfoLore.get("paper");
            lores.removeAll(paperLore);
        }

        if(equipmentInfoLore.containsKey("soul")) {
            List<String> soulLore = equipmentInfoLore.get("soul");
            lores.removeAll(soulLore);
        }


        im.setLore(lores);
        this.equipmentItem.setItemMeta(im);



//        //移除宝石lore
//        List<SpeStone> speStones = SpecialStoneDataManager.getSpeStones(equipmentItem);
//        if(!speStones.isEmpty()){
//            SpecialStoneDataManager.removeNowSpeStoneLore(speStones,equipmentItem);
//        }
//
//        //移除保护符lore
//        String paperIdentifier = PaperDataManager.getPaperIdentifier(equipmentItem);
//        if(paperIdentifier != null){
//            ProtectPaper protectPaper = PaperDataManager.papers.get(paperIdentifier);
//            ItemMeta im2 = equipmentItem.getItemMeta();
//            List<String> lore = im2.getLore();
//            lore.remove(protectPaper.getName());
//            im2.setLore(lore);
//            this.equipmentItem.setItemMeta(im2);
//        }
//
//        //无限耐久精魂lore
//        String soulName = InfiniteSoulManager.getSoulName(equipmentItem);
//        if(soulName != null){
//            ItemMeta im3 = equipmentItem.getItemMeta();
//            List<String> lore = im3.getLore();
//            lore.remove(soulName);
//            im3.setLore(lore);
//            this.equipmentItem.setItemMeta(im3);
//        }




        NBT.modify(equipmentItem,nbt -> {
            nbt.removeKey("refinement");
        });
        NBT.modify(equipmentItem, nbt -> {
            nbt.removeKey("randomlore");
        });
        List<SpeStone> speStones = SpecialStoneDataManager.getSpeStones(equipmentItem);
        for (SpeStone speStone : speStones) {
            NBT.modify(equipmentItem, nbt -> {
                nbt.removeKey(speStone.getNbtKey());
            });
        }



        NBT.modify(equipmentItem, nbt -> {
           nbt.removeKey("protector");
        });

        NBT.modify(equipmentItem, nbt -> {
            nbt.removeKey("infinite");
        });

        return equipmentInfoLore;
    }
    /**
     * 根据等级直接去除淬炼信息
     * @param nowLevel
     */
    public Map<String,List<String>> removeNowItemRefinementInfo(int nowLevel){
        Level newlevel = LevelDataManager.levels.get(nowLevel-1);
        String newEquipmentIdentifier = getEquipmentIdentifier(this.equipmentItem);
        List<String> newMainLore = newlevel.getMainLore();
        List<String> newExtractLore = newlevel.getExtractLores().get(newEquipmentIdentifier);
        if(RandomLoreUtils.hasRandomLore(this.equipmentItem)){
            newExtractLore = RandomLoreUtils.getRandomLore(this.equipmentItem);
        }
        return removeNowItemRefinementInfo(newMainLore, newExtractLore, nowLevel,true);
    }



    public int injuryDownStar(int protectPaperLevel, Stone stone) {

        //现在的淬炼信息，用于消除lore
        int oldRefinementNBTNum = carifyEquipmentLevel();
        Level oldLevel = LevelDataManager.levels.get(oldRefinementNBTNum - 1);
        List<String> oldMainLore = oldLevel.getMainLore();
        String oldEquipmentIdentifier = getEquipmentIdentifier(this.equipmentItem);
        List<String> oldExtractLore = oldLevel.getExtractLores().get(oldEquipmentIdentifier);
        int downLevel = randomDownLevel(stone.getDownLevels());
        int realDownLevel;
        //小于0直接归0
        if (oldRefinementNBTNum - downLevel < 0) {
            removeNowItemRefinementInfo(oldMainLore, oldExtractLore, oldRefinementNBTNum);

            return oldRefinementNBTNum;

        } else if (oldRefinementNBTNum - downLevel == 0) {
            //有保护符时
            if (oldRefinementNBTNum == protectPaperLevel) {
                return 0;
                //无保护符时
            } else {
                removeNowItemRefinementInfo(oldMainLore, oldExtractLore, oldRefinementNBTNum);
                return downLevel;
            }
        }

        //掉星前淬炼星级小于保护符星级
//        if(nowRefinementNBTNum < protectPaperLevel){
//            willRefinement = equipmentData2.get(nowRefinementNBTNum - downLevel);
//            realDownLevel = downLevel;
//
//        }else
        Level newLevel;
        List<String> newMainLore;
        List<String> newExtractLore;
        int newRefinementNBTNum;
        //掉星后淬炼星级小于保护符星级
        if (oldRefinementNBTNum - downLevel < protectPaperLevel) {
            realDownLevel = oldRefinementNBTNum - protectPaperLevel;
            newRefinementNBTNum = oldRefinementNBTNum - realDownLevel;
            newLevel = LevelDataManager.levels.get(newRefinementNBTNum-1);
            newMainLore = newLevel.getMainLore();
            String newEquipmentIdentifier = getEquipmentIdentifier(this.equipmentItem);
            newExtractLore = newLevel.getExtractLores().get(newEquipmentIdentifier);
            //掉星后淬炼等级仍大于保护符星级
        } else {
            realDownLevel = downLevel;
            newRefinementNBTNum = oldRefinementNBTNum - realDownLevel;
            newLevel = LevelDataManager.levels.get(newRefinementNBTNum-1);
            newMainLore = newLevel.getMainLore();
            String newEquipmentIdentifier = getEquipmentIdentifier(this.equipmentItem);
            newExtractLore = newLevel.getExtractLores().get(newEquipmentIdentifier);

        }
        //正常掉星
        removeNowItemRefinementInfo(oldMainLore, oldExtractLore, oldRefinementNBTNum);

        addItemRefinementInfo(newMainLore, newExtractLore, newRefinementNBTNum);
        return realDownLevel;
    }

//    public boolean protectUpStar() {
//        if (carifyEquipmentLevel() == 18) {
//            return false;
//        }
//        if (carifyEquipmentLevel() == 0) {
//            String refinement = equipmentData2.get(carifyEquipmentLevel() + 1);
//            int refinementNBTNum = 1;
//            double protect = protectData.get(refinementNBTNum);
////            ItemMeta im = equipmentItem.getItemMeta();
////            //List<String> lores = Arrays.asList("§e====装备淬炼====","§d"+refinement,"§c附加防御: "+protect);
////            List<String> lores = im.getLore();
////            if(lores == null){
////                lores = new ArrayList<>();
////            }
////            lores.add("§e====装备淬炼====");
////            lores.add("§d"+refinement);
////            lores.add("§c附加防御: "+protect);
////            im.setLore(lores);
////            this.equipmentItem.setItemMeta(im);
////            NBT.modify(this.equipmentItem,nbt -> {
////                nbt.setInteger("refinement",refinementNBTNum);
////            });
//            addItemRefinementInfo(refinement, refinementNBTNum, protect, "防御");
//            return true;
//        }
//
//        int nowRefinementNBTNum = carifyEquipmentLevel();
//        String nowRefinement = equipmentData2.get(nowRefinementNBTNum);
//        double nowProtect = protectData.get(nowRefinementNBTNum);
//        removeNowItemRefinementInfo(nowRefinement, -1, nowProtect, "防御");
//
//        int willRefinementNBTNum = carifyEquipmentLevel() + 1;
//        String willRefinement = equipmentData2.get(carifyEquipmentLevel() + 1);
//        double willProtect = protectData.get(willRefinementNBTNum);
//        addItemRefinementInfo(willRefinement, willRefinementNBTNum, willProtect, "防御");
////        ItemMeta im = equipmentItem.getItemMeta();
////        List<String> lores = im.getLore();
//////        lores.set(1,"§d"+willRefinement);
//////        lores.set(2,"§c附加防御: "+willProtect);
////        lores.remove("§e====装备淬炼====");
////        lores.remove("§d"+nowRefinement);
////        lores.remove("§c附加防御: "+nowProtect);
////
////        lores.add("§e====装备淬炼====");
////        lores.add("§d"+willRefinement);
////        lores.add("§c附加防御: "+willProtect);
////        im.setLore(lores);
////        this.equipmentItem.setItemMeta(im);
////        NBT.modify(this.equipmentItem,nbt -> {
////            nbt.setInteger("refinement",willRefinementNBTNum);
////        });
//        return true;
//    }

//    public int protectDownStar(int protectPaperLevel) {
//        int nowRefinementNBTNum = carifyEquipmentLevel();
//        String nowRefinement = equipmentData2.get(nowRefinementNBTNum);
//        double nowProtect = protectData.get(nowRefinementNBTNum);
//
//        Random rand = new Random();
//        int downLevel = rand.nextInt(0, 4);
//        int realDownLevel;
//        if (nowRefinementNBTNum - downLevel < 0) {
////            ItemMeta equipmentItemMeta = this.equipmentItem.getItemMeta();
////            List<String> lores = equipmentItemMeta.getLore();
////            lores.remove("§e====装备淬炼====");
////            lores.remove("§d"+nowRefinement);
////            lores.remove("§c附加防御: "+nowProtect);
////            System.out.println(nowProtect);
////            equipmentItemMeta.setLore(lores);
////
////            this.equipmentItem.setItemMeta(equipmentItemMeta);
////            NBT.modify(this.equipmentItem,nbt -> {
////                nbt.setInteger("refinement",0);
////            });
//            removeNowItemRefinementInfo(nowRefinement, 0, nowProtect, "防御");
//            return nowRefinementNBTNum;
//            //三星掉三星情况
//        } else if (nowRefinementNBTNum - downLevel == 0) {
//            //有保护符时
//            if (nowRefinementNBTNum == protectPaperLevel) {
//                return 0;
//                //无保护符时
//            } else {
////                ItemMeta equipmentItemMeta = this.equipmentItem.getItemMeta();
////                List<String> lores = equipmentItemMeta.getLore();
////                lores.remove("§e====装备淬炼====");
////                lores.remove("§d"+nowRefinement);
////                lores.remove("§c附加防御: "+nowProtect);
////                //消除附加伤害
////                equipmentItemMeta.setLore(lores);
////                //equipmentItemMeta.removeAttributeModifier(EquipmentSlot.HAND);
////
////                this.equipmentItem.setItemMeta(equipmentItemMeta);
////                //当掉星数大于淬炼星数，设置nbt星级为0
////                NBT.modify(this.equipmentItem,nbt -> {
////                    nbt.setInteger("refinement",0);
////                });
//                removeNowItemRefinementInfo(nowRefinement, 0, nowProtect, "防御");
//                return downLevel;
//            }
//        }
//        String willRefinement;
//        //掉星前淬炼等级小于保护符等级
//        if (nowRefinementNBTNum < protectPaperLevel) {
//            willRefinement = equipmentData2.get(nowRefinementNBTNum - downLevel);
//            realDownLevel = downLevel;
//            //掉星后淬炼星级小于保护符星级
//        } else if (nowRefinementNBTNum - downLevel < protectPaperLevel) {
//            willRefinement = equipmentData2.get(protectPaperLevel);
//            realDownLevel = nowRefinementNBTNum - protectPaperLevel;
//        } else {
//            willRefinement = equipmentData2.get(nowRefinementNBTNum - downLevel);
//            realDownLevel = downLevel;
//        }
//        //正常掉星
//        int willRefinementNBTNum = nowRefinementNBTNum - realDownLevel;
//        double willProtect = protectData.get(willRefinementNBTNum);
////        ItemMeta im = equipmentItem.getItemMeta();
////        List<String> lores = im.getLore();//"§d" + refinement, "§c附加防御: " + protect);
//////        lores.set(1,"§d"+willRefinement);
//////        lores.set(2,"§c附加防御: "+willProtect);
////        lores.remove("§e====装备淬炼====");
////        lores.remove("§d"+nowRefinement);
////        lores.remove("§c附加防御: "+nowProtect);
//        removeNowItemRefinementInfo(nowRefinement, -1, nowProtect, "防御");
////        lores.add("§e====装备淬炼====");
////        lores.add("§d"+willRefinement);
////        lores.add("§c附加防御: "+willProtect);
////        im.setLore(lores);
////        this.equipmentItem.setItemMeta(im);
////        NBT.modify(this.equipmentItem,nbt -> {
////            nbt.setInteger("refinement",refimentLevel - realDownLevel);
////        });
//        addItemRefinementInfo(willRefinement, willRefinementNBTNum, willProtect, "防御");
//        return realDownLevel;
//    }
    //装上保护符，返回 是否成功赋上

    public static int randomDownLevel(int[] nums) {
        Random random = new Random();
        int randomNumber = random.nextInt(100);
        if (nums.length > 4 || nums.length == 0) {
            return 0;
        }
        if (nums.length == 4) {
            if (randomNumber < 40) {
                // 40% 的几率抽到 3
                return nums[3];
            } else if (randomNumber < 70) {
                // 30% 的几率抽到 2
                return nums[2];
            } else if (randomNumber < 90) {
                // 20% 的几率抽到 1
                return nums[1];
            } else {
                return nums[0];
            }
        } else if (nums.length == 3) {
            if (randomNumber < 60) {
                // 60% 的几率抽到 3
                return nums[2];
            } else if (randomNumber < 90) {
                // 30% 的几率抽到 2
                return nums[1];
            } else {
                return nums[0];
            }
        } else if (nums.length == 2) {
            if (randomNumber < 60) {
                // 60% 的几率抽到 3
                return nums[1];
            } else {
                return nums[0];
            }
        } else {
            return nums[0];
        }
    }

    public static boolean isEquipmentLegal(ItemStack itemEquipment) {
        for (String s : canRefinementEquipment.keySet()) {
            List<String> equipmentList = canRefinementEquipment.get(s);
            for (String s1 : equipmentList) {
                if (s1.equals(itemEquipment.getType().name())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getEquipmentIdentifier(ItemStack itemEquipment) {
        for (String s : canRefinementEquipment.keySet()) {
            List<String> equipmentList = canRefinementEquipment.get(s);
            for (String s1 : equipmentList) {
                if (s1.equals(itemEquipment.getType().name())) {
                    return s;
                }
            }
        }
        return null;
    }

    public static int getMinLevelFromEquipments(Player p){
        ItemStack[] armors = p.getInventory().getArmorContents();
        for (int i = 0; i < armors.length; i++) {
            if(armors[i] == null){
                return 0;
            }
        }
        //装备
        int minLevel = 100000;
        for (ItemStack armor : armors) {
            EquipmentDataManager manager = new EquipmentDataManager(armor);
            int equipmentLevel = manager.carifyEquipmentLevel();
            if(equipmentLevel < minLevel){
                minLevel = equipmentLevel;
            }
        }
        ItemStack itemInMainHand = p.getInventory().getItemInMainHand();
        if(itemInMainHand.getType() == Material.AIR){
            return 0;
        }
        //武器
        EquipmentDataManager manager = new EquipmentDataManager(itemInMainHand);
        int equipmentLevel = manager.carifyEquipmentLevel();
        if(equipmentLevel < minLevel){
            minLevel = equipmentLevel;
        }
        return minLevel;
    }
}
