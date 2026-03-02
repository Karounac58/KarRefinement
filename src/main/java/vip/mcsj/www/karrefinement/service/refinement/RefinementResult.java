package vip.mcsj.www.karrefinement.service.refinement;

import vip.mcsj.www.karrefinement.datamanager.LevelDataManager;

/**
 * 淬炼结果
 */
public class RefinementResult {
    private final boolean success;
    private final int oldLevel;
    private final int newLevel;
    private final int downLevels; // 失败时降的级数（成功时为0）

    public RefinementResult(boolean success, int oldLevel, int newLevel, int downLevels) {
        this.success = success;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
        this.downLevels = downLevels;
    }

    public static RefinementResult success(int oldLevel, int newLevel) {
        return new RefinementResult(true, oldLevel, newLevel, 0);
    }

    public static RefinementResult failure(int oldLevel, int newLevel, int downLevels) {
        return new RefinementResult(false, oldLevel, newLevel, downLevels);
    }

    public static RefinementResult maxLevel(int level) {
        return new RefinementResult(false, level, level, 0);
    }

    public boolean isSuccess() {
        return success;
    }

    public int getOldLevel() {
        return oldLevel;
    }

    public int getNewLevel() {
        return newLevel;
    }

    public int getDownLevels() {
        return downLevels;
    }

    public boolean isMaxLevel() {
        return oldLevel == LevelDataManager.levels.size();
    }
}
