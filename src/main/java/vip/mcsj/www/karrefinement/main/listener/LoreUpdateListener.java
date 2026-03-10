package vip.mcsj.www.karrefinement.main.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import vip.mcsj.www.karrefinement.datamanager.LoreUpdateManager;
import vip.mcsj.www.karrefinement.main.KarRefinement;

/**
 * 动态 Lore 更新监听器
 * 从 KarEventListener 提取：背包打开/物品切换/装备变更时更新 Lore
 */
public class LoreUpdateListener implements Listener {

    private static boolean enableLoreUpdate = false;
    /**
     * 玩家打开背包时检查并更新装备lore
     */
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        if(!LoreUpdateManager.enableDynamicLoreUpdate){
            return;
        }
        if (e.getPlayer() instanceof Player) {
            Player player = (Player) e.getPlayer();
            Bukkit.getScheduler().runTaskLater(KarRefinement.instance, () -> {
                LoreUpdateManager.checkAndUpdatePlayerInventory(player);
            }, 2L);
        }
    }

    /**
     * 玩家装备、卸下盔甲时检查并更新装备Lore
     * 仅监听盔甲槽位的相关操作
     */
    @EventHandler
    public void onArmorChange(InventoryClickEvent e) {
        if(!LoreUpdateManager.enableDynamicLoreUpdate){
            return;
        }

        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) e.getWhoClicked();
        
        // 检查是否涉及盔甲槽位的操作
        if (!isArmorSlotOperation(e)) {
            return;
        }
        
        Bukkit.getScheduler().runTaskLater(KarRefinement.instance, () -> {
            // 更新玩家所有盔甲槽的物品
            for (ItemStack armor : player.getInventory().getArmorContents()) {
                if (LoreUpdateManager.shouldUpdateItem(armor)) {
                    LoreUpdateManager.updateItemLore(armor);
                }
            }
        }, 1L);
    }

    /**
     * 判断是否是盔甲槽位操作
     * @param e 背包点击事件
     * @return 是否涉及盔甲槽位
     */
    private boolean isArmorSlotOperation(InventoryClickEvent e) {
        // 情况1: 点击的是玩家背包中的盔甲槽位
        if (e.getClickedInventory() instanceof PlayerInventory) {
            InventoryType.SlotType slotType = e.getSlotType();
            if (slotType == InventoryType.SlotType.ARMOR) {
                return true;
            }
        }
        
        // 情况2: Shift+点击，可能将盔甲装备到身上或卸下
        if (e.isShiftClick() && e.getClickedInventory() instanceof PlayerInventory) {
            ItemStack clickedItem = e.getCurrentItem();
            if (clickedItem != null && isArmorMaterial(clickedItem)) {
                return true;
            }
        }
        
        // 情况3: 使用数字键快速装备盔甲（按1-9将物品放到盔甲槽）
        if (e.getHotbarButton() >= 0 && e.getClickedInventory() instanceof PlayerInventory) {
            InventoryType.SlotType slotType = e.getSlotType();
            if (slotType == InventoryType.SlotType.ARMOR) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * 判断物品是否是盔甲材质
     * @param item 物品
     * @return 是否是盔甲
     */
    private boolean isArmorMaterial(ItemStack item) {
        if (item == null) {
            return false;
        }
        String typeName = item.getType().name();
        return typeName.endsWith("_HELMET") 
            || typeName.endsWith("_CHESTPLATE") 
            || typeName.endsWith("_LEGGINGS") 
            || typeName.endsWith("_BOOTS")
            || typeName.equals("ELYTRA");
    }

    /**
     * 玩家切换手持物品时检查并更新装备lore
     */
    @EventHandler
    public void onItemHeldChange(PlayerItemHeldEvent e) {
        if(!LoreUpdateManager.enableDynamicLoreUpdate){
            return;
        }
        Player player = e.getPlayer();
        // 延迟一tick执行，确保物品已切换
        Bukkit.getScheduler().runTaskLater(KarRefinement.instance, () -> {
            ItemStack newItem = player.getInventory().getItem(e.getNewSlot());
            if (LoreUpdateManager.shouldUpdateItem(newItem)) {
                LoreUpdateManager.updateItemLore(newItem);
            }
        }, 1L);
    }
}
