package vip.mcsj.www.karrefinement.service.refinement;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.api.event.PostRefinementEvent;
import vip.mcsj.www.karrefinement.api.event.PreRefinementEvent;
import vip.mcsj.www.karrefinement.api.model.IRefinementResult;
import vip.mcsj.www.karrefinement.api.model.IRefinementService;
import vip.mcsj.www.karrefinement.api.model.IRefinementStrategy;
import vip.mcsj.www.karrefinement.api.model.IStone;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.object.Stone;

import java.util.List;

/**
 * 淬炼服务 — 协调类
 * 根据条件选择合适的淬炼策略，并触发自定义事件
 */
public class RefinementService implements IRefinementService {

    private final NormalIRefinementStrategy normalStrategy = new NormalIRefinementStrategy();
    private final DarkChangeIRefinementStrategy darkChangeStrategy = new DarkChangeIRefinementStrategy();

    private final StrategyRegistry strategyRegistry = new StrategyRegistry();;

    public RefinementService(){
        //初始化注册内置策略
        this.strategyRegistry.register(new NormalIRefinementStrategy());
        this.strategyRegistry.register(new DarkChangeIRefinementStrategy());
    }

    public StrategyRegistry getStrategyRegistry() {
        return strategyRegistry;
    }

    /**
     * 执行淬炼（自动选择策略，触发事件）
     *
     * @param player    玩家
     * @param equipment 装备物品
     * @param stone     淬炼石
     * @return 淬炼结果
     */
    public RefinementResult refine1(OfflinePlayer player, ItemStack equipment, Stone stone, double extraBonus) {
        // 触发 PreRefinementEvent
        PreRefinementEvent preEvent = new PreRefinementEvent(player, equipment, stone, extraBonus);
        Bukkit.getPluginManager().callEvent(preEvent);
        if (preEvent.isCancelled()) {
            return RefinementResult.cancelled();
        }

        // 使用可能被修改过的成功率
        double finalBonus = preEvent.getSuccessRate();

        // 选择策略并执行
        RefinementResult result;
        List<Object> darkChangeData = KarRefinement.dcdm.getPlayerDarkChangeData(player);
        if (darkChangeData != null) {
            result = darkChangeStrategy.execute(player, equipment, stone, 0);
        } else {
            result = normalStrategy.execute(player, equipment, stone, finalBonus);
        }

        // 触发 PostRefinementEvent
        PostRefinementEvent postEvent = new PostRefinementEvent(player, equipment, result);
        Bukkit.getPluginManager().callEvent(postEvent);

        return result;
    }

    /**
     * 使用指定策略执行淬炼
     */
    public IRefinementResult customRefine(Player player, ItemStack equipment, IStone stone, IRefinementStrategy strategy) {
        // 触发 PreRefinementEvent
        PreRefinementEvent preEvent = new PreRefinementEvent(player, equipment, stone, 0);
        Bukkit.getPluginManager().callEvent(preEvent);
        if (preEvent.isCancelled()) {
            return RefinementResult.cancelled();
        }

        IRefinementResult result = strategy.execute(player, equipment, stone, preEvent.getSuccessRate());

        // 触发 PostRefinementEvent
        PostRefinementEvent postEvent = new PostRefinementEvent(player, equipment, result);
        Bukkit.getPluginManager().callEvent(postEvent);

        return result;
    }

    @Override
    public IRefinementResult refine(OfflinePlayer player,ItemStack equipment,IStone stone,double extraBonus){
        // 触发 PreRefinementEvent
        PreRefinementEvent preEvent = new PreRefinementEvent(player, equipment, stone, extraBonus);
        Bukkit.getPluginManager().callEvent(preEvent);
        if (preEvent.isCancelled()) {
            return RefinementResult.cancelled();
        }

        // 使用可能被修改过的成功率
        double finalBonus = preEvent.getSuccessRate();

        IRefinementStrategy strategy = strategyRegistry.findBestStrategy(player, equipment, stone).orElseThrow(() -> new IllegalStateException("找不到淬炼策略!"));

        IRefinementResult result = strategy.execute(player,equipment,stone,finalBonus);

        // 触发 PostRefinementEvent
        PostRefinementEvent postEvent = new PostRefinementEvent(player, equipment, result);
        Bukkit.getPluginManager().callEvent(postEvent);

        return result;
    }

}
