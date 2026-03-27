package vip.mcsj.www.karrefinement.main.listener;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.api.KarRefinementAPI;
import vip.mcsj.www.karrefinement.datamanager.*;
import vip.mcsj.www.karrefinement.object.Detach;
import vip.mcsj.www.karrefinement.object.SpeStone;
import vip.mcsj.www.karrefinement.utils.KarUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class KarDetachListener implements Listener {
    //保护符拆卸
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
                    String paperIdentifier = PaperDataManager.getIdentifier(equipmentItem);
                    Detach paperDetach = DetachDataManager.paperDetachs.get(paperIdentifier);
                    Double chance = paperDetach.getChance();
                    BigDecimal decimal = BigDecimal.valueOf(KarUtils.nextDouble(100)).setScale(2, RoundingMode.HALF_UP);
                    if((99-chance) < decimal.doubleValue()){
                        DetachDataManager ddm = new DetachDataManager(paperIdentifier);
                        ddm.paperDetachItemUp(detachItem, equipmentItem);
                        ItemStack protectedPaper = KarRefinementAPI.createPaper(paperIdentifier);
                        p1.getInventory().addItem(protectedPaper);
                        p1.sendMessage(Message.messages.get("detach_success"));
                        p1.playSound(p1.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    }else{
                        DetachDataManager ddm = new DetachDataManager(paperIdentifier);
                        ItemStack itemStack = ddm.paperDetachItemUp(detachItem, equipmentItem);
                        p1.getInventory().addItem(itemStack);
                        p1.sendMessage(Message.messages.get("detach_failed"));
                        p1.playSound(p1.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
                    }
                }else{
                    p1.sendMessage(Message.messages.get("detach_disable"));
                }
            }
        }
    }

    //宝石拆卸
    @EventHandler
    public void onInventoryClcik2(InventoryClickEvent e){
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

            if(DetachDataManager.isSpeStoneDetachItemLegal(detachItem)){
                if(DetachDataManager.canSpeStoneDetachUp(detachItem,equipmentItem)){
                    List<SpeStone> speStones = SpecialStoneDataManager.getSpeStones(equipmentItem);
                    SpeStone speStone = speStones.get(ThreadLocalRandom.current().nextInt(speStones.size()));
                    String speStoneIdentifier = speStone.getIdentifier();
                    Detach detach = DetachDataManager.speStoneDetachs.get(speStoneIdentifier);
                    Double chance = detach.getChance();
                    BigDecimal decimal = BigDecimal.valueOf(KarUtils.nextDouble(100)).setScale(2, RoundingMode.HALF_UP);
                    if((99-chance) < decimal.doubleValue()){
                        DetachDataManager ddm = new DetachDataManager(speStoneIdentifier);
                        ddm.speStoneDetachItemUp(detachItem, equipmentItem);
                        ItemStack speStoneItemStack =  KarRefinementAPI.createSpeStone(speStoneIdentifier);
                        p1.getInventory().addItem(speStoneItemStack);
                        p1.sendMessage(Message.messages.get("detach_success"));
                        p1.playSound(p1.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    }else{
                        DetachDataManager ddm = new DetachDataManager(speStoneIdentifier);
                        ItemStack item = ddm.speStoneDetachItemUp(detachItem, equipmentItem);
                        p1.getInventory().addItem(item);
                        p1.sendMessage(Message.messages.get("detach_failed"));
                        p1.playSound(p1.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
                    }
                }else{
                    p1.sendMessage(Message.messages.get("detach_disable"));
                }
            }
        }
    }
}
