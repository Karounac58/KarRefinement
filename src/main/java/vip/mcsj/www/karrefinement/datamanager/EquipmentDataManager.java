package vip.mcsj.www.karrefinement.datamanager;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vip.mcsj.www.karrefinement.object.Level;
import vip.mcsj.www.karrefinement.object.Stone;
import vip.mcsj.www.karrefinement.utils.FileUtil;
import vip.mcsj.www.karrefinement.utils.KarUtils;

import java.util.*;


/**
 * 控制装备信息的类
 */
public class EquipmentDataManager {

    public static final Map<String, List<String>> canRefinementEquipment = new HashMap<>();

    public static final Map<Integer,Double> forgeSuccessList = new HashMap<>();
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
        YamlConfiguration customFileYaml = FileUtil.getCustomFileYaml("items.yml");
        List<String> hand = customFileYaml.getStringList("Hand");
        List<String> helmet = customFileYaml.getStringList("Helmet");
        List<String> chestPlate = customFileYaml.getStringList("Chestplate");
        List<String> leggings = customFileYaml.getStringList("Leggings");
        List<String> boots = customFileYaml.getStringList("Boots");

        canRefinementEquipment.put("Hand", hand);
        canRefinementEquipment.put("Helmet", helmet);
        canRefinementEquipment.put("Chestplate", chestPlate);
        canRefinementEquipment.put("Leggings", leggings);
        canRefinementEquipment.put("Boots", boots);
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
        List<String> lores = im.getLore();
        if (lores == null) {
            lores = new ArrayList<>();
        }

        lores.add(KarUtils.applyTextFormatting(KarUtils.createColorGradientMessage(true))
                + "§e§l装备淬炼"
                + KarUtils.applyTextFormatting(KarUtils.createColorGradientMessage(false)));
        lores.addAll(mainLore);
        lores.addAll(extractLore);
        im.setLore(lores);
        this.equipmentItem.setItemMeta(im);
        NBT.modify(this.equipmentItem, nbt -> {
            nbt.setInteger("refinement", refinementNBTNum);
        });
        judgeSoul(refinementNBTNum);
    }

    /**
     * 移除原淬炼信息方法，为装备上星方法的辅助方法(先移除，再上星)
     */
    private void removeNowItemRefinementInfo(List<String> mainLore, List<String> extractLore, int refinementNBTNum) {
        ItemMeta im = equipmentItem.getItemMeta();
        List<String> lores = im.getLore();
        if (lores == null) {
            lores = new ArrayList<>();
        }
        int j = 0;
        for (int i = 0; i < lores.size(); i++) {
            if (lores.get(i).contains("装备淬炼")) {
                lores.remove(i);
            }
        }
        lores.removeAll(mainLore);
        lores.removeAll(extractLore);
        im.setLore(lores);
        this.equipmentItem.setItemMeta(im);
        NBT.modify(equipmentItem,nbt -> {
            nbt.removeKey("refinement");
        });

    }


    public int injuryDownStar(int protectPaperLevel, Stone stone) {

        //现在的淬炼信息，用于消除lore
        int oldRefinementNBTNum = carifyEquipmentLevel();
        Level oldLevel = LevelDataManager.levels.get(oldRefinementNBTNum - 1);
        List<String> oldMainLore = oldLevel.getMainLore();
        String oldEquipmentIdentifier = getEquipmentIdentifier(this.equipmentItem);
        List<String> oldExtractLore = oldLevel.getExtractLores().get(oldEquipmentIdentifier);
        System.out.println("1"+oldMainLore);
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
        int minLevel = 18;
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
