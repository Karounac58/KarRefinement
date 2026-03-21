package vip.mcsj.www.karrefinement.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import vip.mcsj.www.karrefinement.api.gui.GuiType;

/**
 * KarRefinement GUI 打开事件（可取消）
 */
public class KarGuiOpenEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final GuiType guiType;
    private final Inventory inventory;
    private boolean cancelled = false;

    public KarGuiOpenEvent(Player player, GuiType guiType, Inventory inventory) {
        this.player = player;
        this.guiType = guiType;
        this.inventory = inventory;
    }

    public Player getPlayer() {
        return player;
    }

    public GuiType getGuiType() {
        return guiType;
    }

    public Inventory getInventory() {
        return inventory;
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
