package vip.mcsj.www.karrefinement.service.refinement;

import org.bukkit.OfflinePlayer;
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
    public RefinementResult execute(OfflinePlayer player, ItemStack equipment, Stone stone, double extraBonus) {
        EquipmentService service = new EquipmentService(equipment);
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
                return RefinementResult.failure(currentLevel, 0, 0, false);
            }
            PaperDataManager paperManager = new PaperDataManager(equipment);
            int paperLevel = paperManager.getPaperLevel();
            
            // 使用带结果返回的降星方法
            EquipmentService.DownStarResult downResult = service.injuryDownStarWithResult(paperLevel, stone);
            int downLevel = downResult.getDownLevel();
            boolean protectorWorked = downResult.isProtectorWorked();
            int newLevel = service.getLevel();
            
            return RefinementResult.failure(currentLevel, newLevel, downLevel, protectorWorked);
        }
    }
}
