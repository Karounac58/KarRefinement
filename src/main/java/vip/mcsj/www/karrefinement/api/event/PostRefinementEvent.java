package vip.mcsj.www.karrefinement.api.event;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.service.refinement.RefinementResult;

/**
 * 淬炼后事件（不可取消）
 * 淬炼结果已确定后触发
 */
public class PostRefinementEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final OfflinePlayer player;
    private final ItemStack equipment;
    private final RefinementResult result;

    public PostRefinementEvent(OfflinePlayer player, ItemStack equipment, RefinementResult result) {
        this.player = player;
        this.equipment = equipment;
        this.result = result;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public ItemStack getEquipment() {
        return equipment;
    }

    public RefinementResult getResult() {
        return result;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
