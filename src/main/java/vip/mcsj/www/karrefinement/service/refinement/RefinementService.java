package vip.mcsj.www.karrefinement.service.refinement;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.datamanager.LevelDataManager;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.object.Stone;

import java.util.List;

/**
 * 淬炼服务 — 协调类
 * 根据条件选择合适的淬炼策略
 */
public class RefinementService {

    private final NormalRefinementStrategy normalStrategy = new NormalRefinementStrategy();
    private final DarkChangeRefinementStrategy darkChangeStrategy = new DarkChangeRefinementStrategy();

    /**
     * 执行淬炼（自动选择策略）
     *
     * @param player    玩家
     * @param equipment 装备物品
     * @param stone     淬炼石
     * @return 淬炼结果
     */
    public RefinementResult refine(Player player, ItemStack equipment, Stone stone) {
        // 检查暗改数据，决定使用哪个策略
        List<Object> darkChangeData = KarRefinement.dcdm.getPlayerDarkChangeData(player);
        if (darkChangeData != null) {
            return darkChangeStrategy.execute(player, equipment, stone);
        }
        return normalStrategy.execute(player, equipment, stone);
    }

    /**
     * 使用指定策略执行淬炼
     */
    public RefinementResult refine(Player player, ItemStack equipment, Stone stone, RefinementStrategy strategy) {
        return strategy.execute(player, equipment, stone);
    }
}
