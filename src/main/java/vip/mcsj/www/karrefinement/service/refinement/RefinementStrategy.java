package vip.mcsj.www.karrefinement.service.refinement;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.object.Stone;

/**
 * 淬炼策略接口
 * 不同淬炼方式（普通、暗改、快速）实现此接口
 */
public interface RefinementStrategy {

    /**
     * 执行淬炼
     *
     * @param player    玩家
     * @param equipment 装备物品
     * @param stone     淬炼石
     * @return 淬炼结果
     */
    RefinementResult execute(Player player, ItemStack equipment, Stone stone);
}
