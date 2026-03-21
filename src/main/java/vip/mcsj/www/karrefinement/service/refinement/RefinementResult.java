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
    private final boolean protectorWorked; // 保护符是否生效

    public RefinementResult(boolean success, int oldLevel, int newLevel, int downLevels, boolean protectorWorked) {
        this.success = success;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
        this.downLevels = downLevels;
        this.protectorWorked = protectorWorked;
    }

    public RefinementResult(boolean success, int oldLevel, int newLevel, int downLevels) {
        this(success, oldLevel, newLevel, downLevels, false);
    }

    public static RefinementResult success(int oldLevel, int newLevel) {
        return new RefinementResult(true, oldLevel, newLevel, 0, false);
    }

    public static RefinementResult failure(int oldLevel, int newLevel, int downLevels) {
        return new RefinementResult(false, oldLevel, newLevel, downLevels, false);
    }

    public static RefinementResult failure(int oldLevel, int newLevel, int downLevels, boolean protectorWorked) {
        return new RefinementResult(false, oldLevel, newLevel, downLevels, protectorWorked);
    }

    public static RefinementResult maxLevel(int level) {
        return new RefinementResult(false, level, level, 0, false);
    }

    /**
     * 创建一个被取消的淬炼结果（由 PreRefinementEvent 取消时使用）
     */
    public static RefinementResult cancelled() {
        return new RefinementResult(false, -1, -1, 0, false);
    }

    /**
     * 是否被取消
     */
    public boolean isCancelled() {
        return oldLevel == -1 && newLevel == -1;
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

    public boolean isProtectorWorked() {
        return protectorWorked;
    }
}
