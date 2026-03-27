package vip.mcsj.www.karrefinement.api.event;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.api.model.IStone;
import vip.mcsj.www.karrefinement.object.Stone;

/**
 * 淬炼前事件（可取消）
 * 在淬炼概率计算之前触发，外部插件可以修改成功率或取消淬炼
 */
public class PreRefinementEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final OfflinePlayer player;
    private final ItemStack equipment;
    private final IStone stone;
    private double successRate;
    private boolean cancelled = false;

    public PreRefinementEvent(OfflinePlayer player, ItemStack equipment, IStone stone, double successRate) {
        this.player = player;
        this.equipment = equipment;
        this.stone = stone;
        this.successRate = successRate;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public ItemStack getEquipment() {
        return equipment;
    }

    public IStone getStone() {
        return stone;
    }

    public double getSuccessRate() {
        return successRate;
    }

    /**
     * 修改成功率以影响淬炼结果
     */
    public void setSuccessRate(double successRate) {
        this.successRate = successRate;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
