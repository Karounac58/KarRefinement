package vip.mcsj.www.karrefinement.api.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * GUI 上下文
 * 提供对 GUI 中已有功能槽位物品的访问
 */
public interface GuiContext {

    /**
     * 获取当前 GUI 类型
     */
    GuiType getGuiType();

    /**
     * 获取打开此 GUI 的玩家
     */
    Player getPlayer();

    /**
     * 获取底层 Inventory
     */
    Inventory getInventory();

    /**
     * 获取指定槽位的物品
     */
    ItemStack getItem(int slot);

    // ========= 淬炼 GUI 特有的便捷方法 =========

    /**
     * 获取淬炼石槽位的物品（槽位29）
     * 仅在 GuiType.REFINEMENT 中有效
     */
    Optional<ItemStack> getStone();

    /**
     * 获取装备槽位的物品（槽位33）
     * 仅在 GuiType.REFINEMENT 中有效
     */
    Optional<ItemStack> getEquipment();

    // ========= 锻造 GUI 特有的便捷方法 =========

    /**
     * 获取锻造物品1（槽位19）
     * 仅在 GuiType.FORGE 中有效
     */
    Optional<ItemStack> getForgeItem1();

    /**
     * 获取锻造物品2（槽位25）
     * 仅在 GuiType.FORGE 中有效
     */
    Optional<ItemStack> getForgeItem2();

    /**
     * 在 GUI 中设置指定槽位的物品（仅对自定义槽位有效）
     */
    void setItem(int slot, ItemStack item);

    /**
     * 刷新所有自定义槽位的显示
     */
    void refreshCustomSlots();
}
