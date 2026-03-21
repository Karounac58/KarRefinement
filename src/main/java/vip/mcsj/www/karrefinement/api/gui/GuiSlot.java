package vip.mcsj.www.karrefinement.api.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 自定义 GUI 槽位
 * 外部插件实现此接口来定义自定义槽位的行为
 */
public interface GuiSlot {

    /**
     * 槽位唯一标识（如 "myplugin:luck_boost"）
     */
    String getId();

    /**
     * 该槽位占据的位置索引
     * 注意：不能与 GUI 的核心功能槽位冲突
     */
    int getSlotIndex();

    /**
     * 构建该槽位显示的物品
     * 每次 GUI 初始化/刷新时调用
     *
     * @param player  查看 GUI 的玩家
     * @param context GUI 上下文，可获取其他槽位的物品
     * @return 要显示的 ItemStack
     */
    ItemStack buildDisplayItem(Player player, GuiContext context);

    /**
     * 玩家点击此槽位时的回调
     *
     * @param event   原始点击事件（已取消默认行为）
     * @param context GUI 上下文
     */
    void onClick(InventoryClickEvent event, GuiContext context);

    /**
     * 是否允许玩家将物品放入此槽位
     * 默认 false（纯展示/按钮）
     */
    default boolean acceptsItem() {
        return false;
    }

    /**
     * 当玩家将物品放入此槽位时的验证
     * 仅在 acceptsItem() 返回 true 时调用
     *
     * @param item   玩家放入的物品
     * @param player 操作玩家
     * @return 是否接受该物品
     */
    default boolean validateItem(ItemStack item, Player player) {
        return true;
    }

    /**
     * 槽位优先级，数值越小越先注册
     * 当多个插件注册到同一位置时，优先级高的生效
     */
    default int getPriority() {
        return 0;
    }
}
