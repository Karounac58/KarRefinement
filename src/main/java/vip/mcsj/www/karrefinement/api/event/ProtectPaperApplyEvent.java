package vip.mcsj.www.karrefinement.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * 保护符应用事件（可取消）
 * 在保护符附着到装备之前触发
 */
public class ProtectPaperApplyEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final ItemStack paper;
    private final ItemStack equipment;
    private boolean cancelled = false;

    public ProtectPaperApplyEvent(Player player, ItemStack paper, ItemStack equipment) {
        this.player = player;
        this.paper = paper;
        this.equipment = equipment;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getPaper() {
        return paper;
    }

    public ItemStack getEquipment() {
        return equipment;
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
