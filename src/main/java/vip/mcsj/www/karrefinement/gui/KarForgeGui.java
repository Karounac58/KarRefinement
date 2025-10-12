package vip.mcsj.www.karrefinement.gui;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import vip.mcsj.www.karrefinement.datamanager.EquipmentDataManager;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.main.listener.KarEventListener;
import vip.mcsj.www.karrefinement.utils.KarUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class KarForgeGui {
    public static void initInv(Inventory inv) {
        List<Integer> other = Arrays.asList(0,1,2,3,5,6,7,8,36,37,38,39,40,41,42,43,44);
        List<Integer> greenPanes = Arrays.asList(9,10,11,18,20,27,28,29);
        List<Integer> blackPanes = Arrays.asList(12,13,14,21,23,30,31,32);
        List<Integer> bluePanes = Arrays.asList(15,16,17,24,26,33,34,35);
        ItemStack greenPaneItem = KarRefinement.cm.getItems().get(0);
        ItemStack blackPaneItem = KarRefinement.cm.getItems().get(2);
        ItemStack bluePaneItem = KarRefinement.cm.getItems().get(5);
        ItemStack whitePaneItem = KarRefinement.cm.getItems().get(3);
        ItemStack redStone = new ItemStack(Material.RED_SANDSTONE);
        ItemMeta itemMeta = redStone.getItemMeta();
        itemMeta.setDisplayName("§c§l点击开始锻造");
        redStone.setItemMeta(itemMeta);
        ItemStack oak_sign = KarRefinement.cm.getItems().get(6);
        ItemMeta itemMeta1 = oak_sign.getItemMeta();
        itemMeta1.setDisplayName("§c§l点击查看/刷新成功率");
        oak_sign.setItemMeta(itemMeta1);
        for (Integer index : other) {
            inv.setItem(index,whitePaneItem);
        }
        for (Integer index : greenPanes) {
            inv.setItem(index,greenPaneItem);
        }
        for (Integer index : blackPanes) {
            inv.setItem(index,blackPaneItem);
        }
        for (Integer index : bluePanes) {
            inv.setItem(index,bluePaneItem);
        }
        inv.setItem(22,redStone);
        inv.setItem(4,oak_sign);
    }



    public static ItemStack removeItemStackName(ItemStack itemStack){
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("");
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static boolean judgeInventoryClickMethod(ItemStack equipmentItem1, ItemStack equipmentItem2, Player p) {
        if (equipmentItem1 == null || equipmentItem2 == null) {
            return false;
        }


        if (!(EquipmentDataManager.isEquipmentLegal(equipmentItem1) || EquipmentDataManager.isEquipmentLegal(equipmentItem2))) {
            p.sendMessage("§c§l此物品不能用于锻造");
            return false;
        }

        EquipmentDataManager itemManager1 = new EquipmentDataManager(equipmentItem1);
        EquipmentDataManager itemManager2 = new EquipmentDataManager(equipmentItem2);
        if(EquipmentDataManager.forgeSuccessList.get(itemManager1.carifyEquipmentLevel()+1) == null){
            p.sendMessage("§c§l此等级无法淬炼");
            return false;
        }
        if(itemManager1.carifyEquipmentLevel() != itemManager2.carifyEquipmentLevel()){
            p.sendMessage("§c§l你放入的装备等级不同");
            return false;
        }
        return true;
    }

    public static void KarForgeMethod(ItemStack itemEquipment1, ItemStack itemEquipment2, Player p) {
        if(judgeInventoryClickMethod(itemEquipment1,itemEquipment2,p)){
            EquipmentDataManager manager1 = new EquipmentDataManager(itemEquipment1,p);
            double success = EquipmentDataManager.forgeSuccessList.get(manager1.carifyEquipmentLevel()+1);
            BigDecimal decimal = BigDecimal.valueOf(KarUtils.nextDouble(100)).setScale(2, RoundingMode.HALF_UP);
            //锻造成功
            if((99-success) < decimal.doubleValue()){
                manager1.injuryUpStar();
                p.sendMessage("§a§l锻造成功！装备上星！");
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                KarUtils.removeItemRefinement(itemEquipment2);
            }else{
                p.sendMessage("§c§l锻造失败，消耗副装备");
                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
                KarUtils.removeItemRefinement(itemEquipment2);
            }
        }
    }

    /**
     * @param inv           //用于播放动画
     * @param itemEquipment1     //用于让锻造方法获取参数
     * @param itemEquipment2     //用于让锻造方法获取参数
     * @param p
     */
    public static void playInvVideo(Inventory inv, ItemStack itemEquipment1, ItemStack itemEquipment2, Player p) {
        List<Integer> indexs = Arrays.asList(12,13,14,23,32,31,30,21);

        new BukkitRunnable() {
            @Override
            public void run() {
                //标识正在淬炼中
                KarEventListener.judgeInvForgeOrNot.put(p, 1);
                for (int i = 0; i < indexs.size(); i++) {
                    //标识已关闭菜单,关闭即停止动画
                    if (KarEventListener.judgeForgeInvCloseOrNot.get(p) == null || KarEventListener.judgeForgeInvCloseOrNot.get(p) == 0) {
                        return;
                    }

                    inv.setItem(indexs.get(i), KarRefinement.cm.getItems().get(0));
                    p.updateInventory();
                    KarEventListener.forgeInvs.put(p, inv);
                    try {
                        p.playSound(p.getLocation(), KarRefinement.cs.getSounds().get(0), 1, 1);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (i == indexs.size() - 1) {
                        KarForgeMethod(itemEquipment1,itemEquipment2, p);
                        initInv(inv);
                        //标识已淬炼完毕
                        KarEventListener.judgeInvForgeOrNot.put(p, 0);
                    }
                }
            }
        }.runTaskAsynchronously(KarRefinement.instance);

    }

}
