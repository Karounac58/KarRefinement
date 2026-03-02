package vip.mcsj.www.karrefinement.service.refinement;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.datamanager.LevelDataManager;
import vip.mcsj.www.karrefinement.datamanager.PaperDataManager;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.object.Stone;
import vip.mcsj.www.karrefinement.service.EquipmentService;

import java.util.List;

/**
 * 暗改淬炼策略
 * 根据暗改数据决定成功/失败，不进行随机掷骰
 */
public class DarkChangeRefinementStrategy implements RefinementStrategy {

    @Override
    public RefinementResult execute(Player player, ItemStack equipment, Stone stone) {
        EquipmentService service = new EquipmentService(equipment, player);
        int currentLevel = service.getLevel();

        // 检查最大等级
        if (currentLevel == LevelDataManager.levels.size()) {
            return RefinementResult.maxLevel(currentLevel);
        }

        // 查询暗改数据
        List<Object> darkChangeData = KarRefinement.dcdm.getPlayerDarkChangeData(player);
        if (darkChangeData == null) {
            // 无暗改数据，不应使用此策略
            return RefinementResult.failure(currentLevel, currentLevel, 0);
        }

        boolean isSuccess = (boolean) darkChangeData.get(1);
        int count = (int) darkChangeData.get(2);

        // 更新暗改计数
        KarRefinement.dcdm.updatePlayerDarkChangeData(player, isSuccess, count - 1);

        if (isSuccess) {
            if (service.injuryUpStar()) {
                return RefinementResult.success(currentLevel, service.getLevel());
            }
            return RefinementResult.maxLevel(currentLevel);
        } else {
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
