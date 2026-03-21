package vip.mcsj.www.karrefinement.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.api.gui.GuiType;

/**
 * KarRefinement GUI 槽位点击事件（可取消）
 */
public class KarGuiClickEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final GuiType guiType;
    private final int slot;
    private final ItemStack clickedItem;
    private boolean cancelled = false;

    public KarGuiClickEvent(Player player, GuiType guiType, int slot, ItemStack clickedItem) {
        this.player = player;
        this.guiType = guiType;
        this.slot = slot;
        this.clickedItem = clickedItem;
    }

    public Player getPlayer() {
        return player;
    }

    public GuiType getGuiType() {
        return guiType;
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getClickedItem() {
        return clickedItem;
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
