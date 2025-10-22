package vip.mcsj.www.karrefinement.main.listener;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.datamanager.Message;
import vip.mcsj.www.karrefinement.gui.KarTakeItemGuiInvHolder;
import vip.mcsj.www.karrefinement.object.CustomInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KarTakeItemGuiListener implements Listener {
    public static Map<UUID, CustomInventory> playerInventories = new HashMap<>();

    //翻页操作
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();

        // 检查是否是我们的自定义库存
        if (inventory != null && inventory.getHolder() instanceof KarTakeItemGuiInvHolder) {

            event.setCancelled(true); // 取消默认操作

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) return;

            String displayName = clickedItem.getItemMeta().getDisplayName();

            // 获取关联的 CustomInventory 实例（需要根据你的实现调整）
            CustomInventory customInv = getCustomInventory(player);
            if (customInv == null) return;

            // 处理按钮点击
            if (displayName.contains("上一页")) {
                customInv.previousPage();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            } else if (displayName.contains("下一页")) {
                customInv.nextPage();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            } else if (displayName.contains("关闭")) {
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1.0f, 1.0f);
            }
        }
    }

    // 获取玩家的自定义库存
    public CustomInventory getCustomInventory(Player player) {
        return playerInventories.get(player.getUniqueId());
    }

    // 设置玩家的自定义库存
    public static void setCustomInventory(Player player, CustomInventory customInventory) {
        playerInventories.put(player.getUniqueId(), customInventory);
    }

    // 移除玩家的自定义库存
    public static void removeCustomInventory(Player player) {
        playerInventories.remove(player.getUniqueId());
    }

    // 玩家退出时清理数据
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerInventories.remove(event.getPlayer().getUniqueId());
    }

    //获取物品操作
    @EventHandler
    public void onInventoryClick2(InventoryClickEvent e){
        if (!(e.getWhoClicked() instanceof Player)) return;

        Player player = (Player) e.getWhoClicked();
        Inventory inventory = e.getClickedInventory();

        if(inventory != null && inventory.getHolder() instanceof KarTakeItemGuiInvHolder
        && e.getCurrentItem() != null && e.getSlot() < 45){
            e.setCancelled(true);
            player.getInventory().addItem(e.getCurrentItem());
            player.sendMessage(Message.messages.get("takeitemgui_get").replace("{item}", e.getCurrentItem().getItemMeta().getDisplayName()));
            //e.getCurrentItem().getItemMeta().getDisplayName()
        }
    }
}
