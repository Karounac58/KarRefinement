package vip.mcsj.www.karrefinement.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * 装备等级变更事件（不可取消）
 * 装备淬炼等级发生变化后触发
 */
public class EquipmentLevelChangeEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final ItemStack equipment;
    private final int oldLevel;
    private final int newLevel;
    private final Cause cause;

    public EquipmentLevelChangeEvent(ItemStack equipment, int oldLevel, int newLevel, Cause cause) {
        this.equipment = equipment;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
        this.cause = cause;
    }

    public ItemStack getEquipment() {
        return equipment;
    }

    public int getOldLevel() {
        return oldLevel;
    }

    public int getNewLevel() {
        return newLevel;
    }

    public Cause getCause() {
        return cause;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * 等级变更原因
     */
    public enum Cause {
        REFINEMENT_SUCCESS,   // 淬炼成功升星
        REFINEMENT_FAILURE,   // 淬炼失败降星
        FORGE,                // 锻造
        TRANSFORM,            // 转星
        DIRECT_UPGRADE,       // 直升符
        API                   // API 调用
    }
}
