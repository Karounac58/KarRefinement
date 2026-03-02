package vip.mcsj.www.karrefinement.service.refinement;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.datamanager.EquipmentDataManager;
import vip.mcsj.www.karrefinement.datamanager.LevelDataManager;
import vip.mcsj.www.karrefinement.datamanager.PaperDataManager;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.object.Stone;
import vip.mcsj.www.karrefinement.service.EquipmentService;
import vip.mcsj.www.karrefinement.service.ProbabilityCalculator;

import java.util.List;

/**
 * 普通淬炼策略
 * 从 KarRefinementGui.KarRefinementMethod 提取核心业务逻辑
 */
public class NormalRefinementStrategy implements RefinementStrategy {

    @Override
    public RefinementResult execute(Player player, ItemStack equipment, Stone stone) {
        EquipmentService service = new EquipmentService(equipment, player);
        int currentLevel = service.getLevel();

        // 检查最大等级
        if (currentLevel == LevelDataManager.levels.size()) {
            return RefinementResult.maxLevel(currentLevel);
        }

        // 获取基础成功率
        double baseChance = stone.getProbability().get(currentLevel);

        // 获取药水加成
        double potionBonus = 0;
        List<Object> potionInfo = KarRefinement.pdm.queryPlayerPotionInfoCache(player);
        if (potionInfo != null) {
            potionBonus = (double) potionInfo.get(1) * 100;
        }

        // 获取熔炉加成（从调用方传入，这里默认0）
        double extraBonus = 0;

        // 掷骰判定
        if (ProbabilityCalculator.rollSuccess(baseChance, potionBonus, extraBonus)) {
            // 成功
            if (service.injuryUpStar()) {
                return RefinementResult.success(currentLevel, service.getLevel());
            }
            return RefinementResult.maxLevel(currentLevel);
        } else {
            // 失败
            if (currentLevel == 0) {
                return RefinementResult.failure(currentLevel, 0, 0);
            }
            PaperDataManager paperManager = new PaperDataManager(equipment);
            int paperLevel = paperManager.getPaperLevel();
            int downLevel = service.injuryDownStar(paperLevel, stone);
            int newLevel = service.getLevel();
            return RefinementResult.failure(currentLevel, newLevel, downLevel);
        }
    }
}
