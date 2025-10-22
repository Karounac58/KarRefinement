package vip.mcsj.www.karrefinement.gui;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vip.mcsj.www.karrefinement.datamanager.EquipmentDataManager;
import vip.mcsj.www.karrefinement.datamanager.Message;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.object.InvItem;
import vip.mcsj.www.karrefinement.utils.FileUtil;

import java.util.*;

public class KarTransformStarGui {
    public static Map<String, InvItem> tItems = new HashMap<>();
    public static String title = "";
    public static int size = 0;
    public static int originSlot = 0;
    public static int afterSlot = 0;

    public static void init(){
        if(!tItems.isEmpty()){
            tItems.clear();
        }
        YamlConfiguration file = FileUtil.getCustomFileYaml("gui/transformgui.yml");
        title = file.getString("Title");
        size = file.getInt("Size");
        originSlot = file.getInt("OriginSlot");
        afterSlot = file.getInt("AfterSlot");
        ConfigurationSection fileCS = file.getConfigurationSection("Item");
        Set<String> keys = fileCS.getKeys(false);
        for (String key : keys) {
            String name = fileCS.getString(key + ".Name");
            Material material = Material.valueOf(fileCS.getString(key + ".Material"));
            int data = fileCS.getInt(key + ".Data");
            List<Integer> slots = fileCS.getIntegerList(key + ".Slots");
            int customModelData = fileCS.getInt(key + ".CustomModelData");
            List<String> lore = fileCS.getStringList(key + ".Lore");
            tItems.put(key,new InvItem(name, material, slots,data,customModelData, lore));
        }
    }
    public static void initInv(Inventory inv,Player p) {

        InvItem barrier = tItems.get("Barrier");
        InvItem barrier2 = tItems.get("Barrier2");
        InvItem confirmButton = tItems.get("ConfirmButton");
        InvItem originInfo = tItems.get("OriginInfo");
        InvItem afterInfo = tItems.get("AfterInfo");


        ItemStack whitePaneItem = KarRefinementGui.createInvItem(barrier,p);
        ItemStack blackPaneItem = KarRefinementGui.createInvItem(barrier2,p);
        ItemStack redPaneItem = KarRefinementGui.createInvItem(confirmButton,p);
        ItemStack originSign =  KarRefinementGui.createInvItem(originInfo,p);
        ItemStack afterSign = KarRefinementGui.createInvItem(afterInfo,p);

        for (Integer slot : barrier.getSlots()) {
            inv.setItem(slot, whitePaneItem);
        }


        for (Integer slot : barrier2.getSlots()) {
            inv.setItem(slot, blackPaneItem);
        }

        for (Integer slot : confirmButton.getSlots()) {
            inv.setItem(slot, redPaneItem);
        }

        for (Integer slot : originInfo.getSlots()) {
            inv.setItem(slot, originSign);
        }

        for (Integer slot : afterInfo.getSlots()) {
            inv.setItem(slot, afterSign);
        }
    }

    public static boolean judgeInventoryClickMethod(ItemStack equipmentItem1, ItemStack equipmentItem2, Player p){
        if (equipmentItem1 == null || equipmentItem2 == null) {
            return false;
        }

        if (!(EquipmentDataManager.isEquipmentLegal(equipmentItem1) || EquipmentDataManager.isEquipmentLegal(equipmentItem2))) {
            p.sendMessage(Message.messages.get("transform_diableitem"));
            return false;
        }

        EquipmentDataManager itemManager1 =  new EquipmentDataManager(equipmentItem1);
        EquipmentDataManager itemManager2 =  new EquipmentDataManager(equipmentItem2);
        if(itemManager1.carifyEquipmentLevel() == 0){
            p.sendMessage(Message.messages.get("transform_zerolevel"));
            return false;
        }

        if(itemManager1.carifyEquipmentLevel() < itemManager2.carifyEquipmentLevel()){
            p.sendMessage(Message.messages.get("transform_conflictlevel"));
            return false;
        }

        if(!EquipmentDataManager.allowAfterItemIsRefinement){
            if(itemManager2.carifyEquipmentLevel() != 0){
                p.sendMessage(Message.messages.get("transform_notzerolevel"));
                return false;
            }
        }

        if(!isEnoughMoney(p)){
            p.sendMessage(Message.messages.get("transform_notenoughmoney"));
            return false;
        }
        return true;
    }

    public static void KarTransformMethod(ItemStack equipmentItem1, ItemStack equipmentItem2, Player p){
        if(judgeInventoryClickMethod(equipmentItem1, equipmentItem2, p)){
            EquipmentDataManager itemManager1 =  new EquipmentDataManager(equipmentItem1);
            EquipmentDataManager itemManager2 =  new EquipmentDataManager(equipmentItem2);
            int item1Level = itemManager1.carifyEquipmentLevel();
            int item2Level = itemManager2.carifyEquipmentLevel();
            //移除淬炼信息
            itemManager1.removeNowItemRefinementInfo(item1Level);
            //扣钱
            KarRefinement.econ.withdrawPlayer(p,EquipmentDataManager.transformCost);
            p.sendMessage(Message.messages.get("transform_moneycost").replace("{money}",EquipmentDataManager.transformCost+""));
            for (int i = 0; i < item1Level - item2Level; i++) {
                itemManager2.injuryUpStar();
            }
            p.sendMessage(Message.messages.get("transform_success"));
            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        }
    }

    public static class KarTransformStarGuiInvHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    public static boolean isEnoughMoney(Player p){
        double balance = KarRefinement.econ.getBalance(p);
        return balance >= EquipmentDataManager.transformCost;
    }
}
