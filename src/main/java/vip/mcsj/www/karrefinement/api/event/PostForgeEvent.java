package vip.mcsj.www.karrefinement.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * 锻造后事件（不可取消）
 * 锻造结果已确定后触发
 */
public class PostForgeEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final ItemStack equipment1;
    private final ItemStack equipment2;
    private final boolean success;

    public PostForgeEvent(Player player, ItemStack equipment1, ItemStack equipment2, boolean success) {
        this.player = player;
        this.equipment1 = equipment1;
        this.equipment2 = equipment2;
        this.success = success;
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

    public boolean isSuccess() {
        return success;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
