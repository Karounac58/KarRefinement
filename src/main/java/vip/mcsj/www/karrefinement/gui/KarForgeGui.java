package vip.mcsj.www.karrefinement.gui;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import vip.mcsj.www.karrefinement.datamanager.EquipmentDataManager;
import vip.mcsj.www.karrefinement.datamanager.Message;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.main.listener.KarEventListener;
import vip.mcsj.www.karrefinement.object.InvItem;
import vip.mcsj.www.karrefinement.utils.FileUtil;
import vip.mcsj.www.karrefinement.utils.KarUtils;
import vip.mcsj.www.karrefinement.utils.ReflectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static vip.mcsj.www.karrefinement.gui.KarRefinementGui.createInvItem;

public class KarForgeGui {
    public static Map<String, InvItem> fItems = new HashMap<>();
    public static String title = "";

    public static void init(){
        if(!fItems.isEmpty()){
            fItems.clear();
        }
        YamlConfiguration file = FileUtil.getCustomFileYaml("gui/forgegui.yml");
        title = file.getString("Title");
        ConfigurationSection fileCS = file.getConfigurationSection("Item");
        Set<String> keys = fileCS.getKeys(false);
        for (String key : keys) {
            String name = fileCS.getString(key + ".Name");
            Material material = Material.valueOf(fileCS.getString(key + ".Material"));
            int data = fileCS.getInt(key + ".Data");
            int customModelData = fileCS.getInt(key + ".CustomModelData");
            List<String> lore = fileCS.getStringList(key + ".Lore");
            fItems.put(key,new InvItem(name, material, data, customModelData, lore));
        }
    }
    public static void initInv(Inventory inv,Player p) {
        List<Integer> other = Arrays.asList(0,1,2,3,5,6,7,8,36,37,38,39,40,41,42,43,44);
        List<Integer> greenPanes = Arrays.asList(9,10,11,18,20,27,28,29);
        List<Integer> blackPanes = Arrays.asList(12,13,14,21,23,30,31,32);
        List<Integer> bluePanes = Arrays.asList(15,16,17,24,26,33,34,35);
        ItemStack redPaneItem = createInvItem(fItems.get("Barrier2"),p);
        ItemStack blackPaneItem = createInvItem(fItems.get("Barrier3"),p);
        ItemStack bluePaneItem = createInvItem(fItems.get("Barrier4"),p);
        ItemStack whitePaneItem = createInvItem(fItems.get("Barrier"),p);
        ItemStack redStone = createInvItem(fItems.get("ConfirmButton"),p);
        ItemStack oak_sign = createInvItem(fItems.get("InfoButton"),p);
        for (Integer index : other) {
            inv.setItem(index,whitePaneItem);
        }
        for (Integer index : greenPanes) {
            inv.setItem(index,redPaneItem);
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
            p.sendMessage(Message.messages.get("forge_disbaleitem"));
            return false;
        }

        EquipmentDataManager itemManager1 = new EquipmentDataManager(equipmentItem1);
        EquipmentDataManager itemManager2 = new EquipmentDataManager(equipmentItem2);
        if(EquipmentDataManager.forgeSuccessList.get(itemManager1.carifyEquipmentLevel()+1) == null){
            p.sendMessage(Message.messages.get("forge_disablelevel"));
            return false;
        }
        if(itemManager1.carifyEquipmentLevel() != itemManager2.carifyEquipmentLevel()){
            p.sendMessage(Message.messages.get("forge_conflictlevel"));
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
                p.sendMessage(Message.messages.get("forge_upstar"));
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                KarUtils.removeItemRefinement(itemEquipment2);
            }else{
                p.sendMessage(Message.messages.get("forge_failed"));
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
        ItemStack videoItem = createInvItem(fItems.get("VideoItem"),p);
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

                    inv.setItem(indexs.get(i), videoItem);
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
                        initInv(inv,p);
                        //标识已淬炼完毕
                        KarEventListener.judgeInvForgeOrNot.put(p, 0);
                    }
                }
            }
        }.runTaskAsynchronously(KarRefinement.instance);

    }

}
