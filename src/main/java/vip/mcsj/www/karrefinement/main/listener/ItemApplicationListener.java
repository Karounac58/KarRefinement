package vip.mcsj.www.karrefinement.main.listener;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.api.KarRefinementAPI;
import vip.mcsj.www.karrefinement.core.Service;
import vip.mcsj.www.karrefinement.datamanager.*;
import vip.mcsj.www.karrefinement.gui.GuiStateManager;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.object.SpeStone;
import vip.mcsj.www.karrefinement.utils.KarUtils;

/**
 * 物品应用监听器
 * 从 KarEventListener 提取：保护符/宝石/精魂应用 + 无限耐久 + PvP着火
 */
public class ItemApplicationListener implements Listener {

    /**
     * 保护符事件
     */
    @EventHandler
    public void onProtectPaperApply(InventoryClickEvent e) {
        if (!e.getAction().equals(InventoryAction.SWAP_WITH_CURSOR)) {
            return;
        }
        Player p = (Player) e.getWhoClicked();
        if (GuiStateManager.isRefinementRunning(p)) {
            return;
        }
        ItemStack itemPaper = e.getCursor();
        ItemStack itemEquipment = e.getCurrentItem();
        Service service = KarRefinementAPI.getService(PaperDataManager.class);
        if (service.isLegal(itemPaper)) {
            if (EquipmentDataManager.isEquipmentLegal(itemEquipment)) {
                if (service.up(itemPaper, itemEquipment)) {
                    p.sendMessage(Message.messages.get("paper_up"));
                    p.playSound(p.getLocation(), KarRefinement.cs.getSounds().get(0), 1, 1);
                    KarUtils.removeItemRefinement(itemPaper);
                }
            }
        }
    }

    /**
     * 宝石事件
     */
    @EventHandler
    public void onSpecialStoneApply(InventoryClickEvent e) {
        if (!e.getAction().equals(InventoryAction.SWAP_WITH_CURSOR)) {
            return;
        }
        Player p = (Player) e.getWhoClicked();
        if (GuiStateManager.isRefinementRunning(p)) {
            return;
        }
        ItemStack itemStone = e.getCursor();
        ItemStack itemEquipment = e.getCurrentItem();
        Service service = KarRefinementAPI.getService(SpecialStoneDataManager.class);
        if (service.isLegal(itemStone)) {
            SpeStone speStone = (SpeStone) service.get(itemStone);
            if (SpecialStoneDataManager.isEquipmentLegal(speStone, itemEquipment)) {
                if (service.up(itemStone, itemEquipment)) {
                    p.sendMessage(Message.messages.get("spestone_up"));
                    p.playSound(p.getLocation(), KarRefinement.cs.getSounds().get(0), 1, 1);
                    KarUtils.removeItemRefinement(itemStone);
                }
            }
        }
    }

    /**
     * 无限耐久精魂事件
     */
    @EventHandler
    public void onInfiniteSoulApply(InventoryClickEvent e) {
        if (!e.getAction().equals(InventoryAction.SWAP_WITH_CURSOR)) {
            return;
        }
        Player p = (Player) e.getWhoClicked();
        if (GuiStateManager.isRefinementRunning(p)) {
            return;
        }
        ItemStack itemSoul = e.getCursor();
        ItemStack itemEquipment = e.getCurrentItem();
        Service service = KarRefinementAPI.getService(InfiniteSoulManager.class);
        if (service.isLegal(itemSoul)) {
            if (EquipmentDataManager.isEquipmentLegal(itemEquipment)) {
                if (service.up(itemSoul, itemEquipment)) {
                    p.sendMessage(Message.messages.get("soul_up"));
                    p.playSound(p.getLocation(), KarRefinement.cs.getSounds().get(0), 1, 1);
                    KarUtils.removeItemRefinement(itemSoul);
                }
            }
        }
    }

    /**
     * 无限耐久装备消耗耐久事件
     */
    @EventHandler
    public void onInfiniteEquipmentConsumeDurability(PlayerItemDamageEvent e) {
        ItemStack damageItem = e.getItem();
        NBTItem nbtItem = new NBTItem(damageItem);
        if (nbtItem.hasTag("infinite")) {
            e.setCancelled(true);
        }
    }

//    /**
//     * PvP 着火效果事件
//     */
//    @EventHandler
//    public void onPlayerDamageOtherEvent(EntityDamageByEntityEvent e) {
//        if (!(e.getDamager() instanceof Player) || !(e.getEntity() instanceof Player)) {
//            return;
//        }
//        Player attacker = (Player) e.getDamager();
//        Player victim = (Player) e.getEntity();
//        if (attacker.getInventory().getItemInMainHand().getType() == Material.AIR) {
//            return;
//        }
//        ItemStack itemInUse = attacker.getInventory().getItemInMainHand();
//        if (!EquipmentDataManager.canRefinementEquipment.containsKey("Hand") ||
//                !EquipmentDataManager.canRefinementEquipment.get("Hand").contains(itemInUse.getType().name().toUpperCase())) {
//            return;
//        }
//        NBTItem nbtItem = new NBTItem(itemInUse);
//        if (!nbtItem.hasTag("lfs")) {
//            return;
//        }
//        int lfs = nbtItem.getInteger("lfs");
//        if (lfs >= 1 && lfs <= 5) {
//            victim.setFireTicks(lfs * 10 * 20);
//        }
//    }
}
