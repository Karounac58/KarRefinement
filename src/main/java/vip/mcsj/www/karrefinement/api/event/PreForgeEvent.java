package vip.mcsj.www.karrefinement.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * 锻造前事件（可取消）
 * 在锻造概率计算之前触发
 */
public class PreForgeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final ItemStack equipment1;
    private final ItemStack equipment2;
    private boolean cancelled = false;

    public PreForgeEvent(Player player, ItemStack equipment1, ItemStack equipment2) {
        this.player = player;
        this.equipment1 = equipment1;
        this.equipment2 = equipment2;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getEquipment1() {
        return equipment1;
    }

    public ItemStack getEquipment2() {
        return equipment2;
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
