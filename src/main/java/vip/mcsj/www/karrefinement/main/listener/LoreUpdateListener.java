package vip.mcsj.www.karrefinement.main.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.datamanager.LoreUpdateManager;
import vip.mcsj.www.karrefinement.main.KarRefinement;

/**
 * 动态 Lore 更新监听器
 * 从 KarEventListener 提取：背包打开/物品切换/装备变更时更新 Lore
 */
public class LoreUpdateListener implements Listener {

    /**
     * 玩家打开背包时检查并更新装备lore
     */
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        if (e.getPlayer() instanceof Player) {
            Player player = (Player) e.getPlayer();
            Bukkit.getScheduler().runTaskLater(KarRefinement.instance, () -> {
                LoreUpdateManager.checkAndUpdatePlayerInventory(player);
            }, 2L);
        }
    }

    /**
     * 玩家装备/卸下盔甲时检查并更新装备lore
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) e.getWhoClicked();
        Bukkit.getScheduler().runTaskLater(KarRefinement.instance, () -> {
            ItemStack clickedItem = e.getCurrentItem();
            if (LoreUpdateManager.shouldUpdateItem(clickedItem)) {
                LoreUpdateManager.updateItemLore(clickedItem);
            }
            ItemStack cursorItem = e.getCursor();
            if (LoreUpdateManager.shouldUpdateItem(cursorItem)) {
                LoreUpdateManager.updateItemLore(cursorItem);
            }
        }, 1L);
    }
}
