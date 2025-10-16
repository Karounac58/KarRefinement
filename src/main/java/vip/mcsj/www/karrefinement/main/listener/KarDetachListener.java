package vip.mcsj.www.karrefinement.main.listener;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.datamanager.DetachDataManager;
import vip.mcsj.www.karrefinement.datamanager.EquipmentDataManager;
import vip.mcsj.www.karrefinement.datamanager.PaperDataManager;
import vip.mcsj.www.karrefinement.object.Detach;
import vip.mcsj.www.karrefinement.utils.KarUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class KarDetachListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        ItemStack detachItem;
        ItemStack equipmentItem;
        if(e.getAction().equals(InventoryAction.SWAP_WITH_CURSOR)){
            Player p1 = (Player) e.getWhoClicked();
            if(KarEventListener.judgeInvRefinementOrNot.get(p1) == 1){
                return;
            }
            if(KarEventListener.judgeInvForgeOrNot.get(p1) == 1){
                return;
            }
            detachItem = e.getCursor();
            equipmentItem = e.getCurrentItem();
            if(!DetachDataManager.enabled){
                return;
            }
            if(DetachDataManager.isPaperDetachItemLegal(detachItem)){
                if(DetachDataManager.canPaperDetachUp(detachItem,equipmentItem)){
                    String paperIdentifier = PaperDataManager.getPaperIdentifier(equipmentItem);
                    Detach paperDetach = DetachDataManager.paperDetachs.get(paperIdentifier);
                    Double chance = paperDetach.getChance();
                    BigDecimal decimal = BigDecimal.valueOf(KarUtils.nextDouble(100)).setScale(2, RoundingMode.HALF_UP);
                    if((99-chance) < decimal.doubleValue()){
                        DetachDataManager ddm = new DetachDataManager(paperIdentifier);
                        ddm.paperDetachItemUp(detachItem, equipmentItem);
                        ItemStack protectedPaper = new PaperDataManager(paperIdentifier).createProtectedPaper();
                        p1.getInventory().addItem(protectedPaper);
                        p1.sendMessage("§a完美卸下保护符!");
                        p1.playSound(p1.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    }else{
                        DetachDataManager ddm = new DetachDataManager(paperIdentifier);
                        ItemStack itemStack = ddm.paperDetachItemUp(detachItem, equipmentItem);
                        p1.getInventory().addItem(itemStack);
                        p1.sendMessage("§c在卸下保护符的过程中出现了意外！你得到了保护符碎片.");
                        p1.playSound(p1.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
                    }
                }else{
                    p1.sendMessage("§c此装备无保护符或保护符不可被拆卸!");
                }
            }
        }
    }
}
