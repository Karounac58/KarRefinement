package vip.mcsj.www.karrefinement.api.model;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.object.Stone;
import vip.mcsj.www.karrefinement.service.refinement.RefinementResult;

/**
 * 淬炼策略接口
 * 不同淬炼方式（普通、暗改、快速）实现此接口
 */
public interface IRefinementStrategy {

    /**
     * 执行淬炼
     *
     * @param player    玩家
     * @param equipment 装备物品
     * @param stone     淬炼石
     * @return 淬炼结果
     */
    IRefinementResult execute(OfflinePlayer player, ItemStack equipment, IStone stone, double extraBonus);

    /**
     * 策略标识符
     */
    String getId();

    /**
     * 策略优先级。当多个策略同时满足条件时，值越大优先级越高。
     */
    default int getPriority() { return 0; }

    /**
     * 自动调度判断：当前上下文（玩家、装备、石头）是否应使用此策略？
     */
    boolean canHandle(OfflinePlayer player, ItemStack equipment, IStone stone);
}
