package vip.mcsj.www.karrefinement.api.model;

public interface IRefinementResult {
    boolean isCancelled(); // 是否被事件取消

    boolean isSuccess();

    int getOldLevel();

    int getNewLevel();

    int getDownLevels(); // 失败时降的级数(成功时为0)

    boolean isMaxLevel();

    boolean isProtectorWorked(); // 保护符是否生效


}
