package vip.mcsj.www.karrefinement.gui;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vip.mcsj.www.karrefinement.datamanager.EquipmentDataManager;
import vip.mcsj.www.karrefinement.main.KarRefinement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KarTransformStarGui {

    public static void init(Inventory inv) {
        List<Integer> whitePanes = Arrays.asList(0,2,3,4,5,6,8,9,17,18,19,20,21,23,24,25,26);
        List<Integer> blackPanes = Arrays.asList(11,12,13,14,15);
        int originSlot = 1;
        int afterSlot = 7;
        int beginButton = 22;

        ItemStack whitePaneItem = KarRefinement.cm.getItems().get(3);
        ItemStack blackPaneItem = KarRefinement.cm.getItems().get(2);
        ItemStack redPaneItem = KarRefinement.cm.getItems().get(0);
        ItemStack originSign =  KarRefinement.cm.getItems().get(6).clone();
        ItemStack afterSign = KarRefinement.cm.getItems().get(6).clone();


        ItemMeta redPaneItemMeta = redPaneItem.getItemMeta();
        redPaneItemMeta.setDisplayName(String.format("§a§l开始移星§7( §6%d金币 §7)",EquipmentDataManager.transformCost));
        redPaneItem.setItemMeta(redPaneItemMeta);
        ItemMeta originSignItemMeta = originSign.getItemMeta();
        originSignItemMeta.setDisplayName("§c§l☼§d§l移星原武器§c§l☼");
        originSign.setItemMeta(originSignItemMeta);

        ItemMeta afterSignItemMeta = afterSign.getItemMeta();
        afterSignItemMeta.setDisplayName("§c§l☼§d§l移星武器§c§l☼");
        afterSign.setItemMeta(afterSignItemMeta);

        for (int i = 0; i < whitePanes.size(); i++) {
            inv.setItem(whitePanes.get(i), whitePaneItem);
        }

        for (int i = 0; i < blackPanes.size(); i++) {
            inv.setItem(blackPanes.get(i), blackPaneItem);
        }

        inv.setItem(originSlot, originSign);
        inv.setItem(afterSlot, afterSign);
        inv.setItem(beginButton, redPaneItem);
    }

    public static boolean judgeInventoryClickMethod(ItemStack equipmentItem1, ItemStack equipmentItem2, Player p){
        if (equipmentItem1 == null || equipmentItem2 == null) {
            return false;
        }

        if (!(EquipmentDataManager.isEquipmentLegal(equipmentItem1) || EquipmentDataManager.isEquipmentLegal(equipmentItem2))) {
            p.sendMessage("§c§l此物品不能用于移星");
            return false;
        }

        EquipmentDataManager itemManager1 =  new EquipmentDataManager(equipmentItem1);
        EquipmentDataManager itemManager2 =  new EquipmentDataManager(equipmentItem2);
        if(itemManager1.carifyEquipmentLevel() == 0){
            p.sendMessage("§c§l移星原武器淬炼等级为0");
            return false;
        }

        if(itemManager1.carifyEquipmentLevel() < itemManager2.carifyEquipmentLevel()){
            p.sendMessage("§c§l移星武器淬炼等级大于移星原武器");
            return false;
        }

        if(!EquipmentDataManager.allowAfterItemIsRefinement){
            if(itemManager2.carifyEquipmentLevel() != 0){
                p.sendMessage("§c§l移星武器淬炼等级不为0");
                return false;
            }
        }

        if(!isEnoughMoney(p)){
            p.sendMessage("§c§l金币不足");
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
            p.sendMessage(String.format("§a§l成功花费金币%d移星",EquipmentDataManager.transformCost));
            for (int i = 0; i < item1Level - item2Level; i++) {
                itemManager2.injuryUpStar();
            }
            p.sendMessage("§a§l移星成功！");
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
