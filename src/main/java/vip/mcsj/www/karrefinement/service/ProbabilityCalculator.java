package vip.mcsj.www.karrefinement.service;

import vip.mcsj.www.karrefinement.utils.KarUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/**
 * 统一概率计算器
 * 从 KarRefinementGui / KarForgeGui 提取概率计算逻辑
 */
public class ProbabilityCalculator {

    /**
     * 掷骰判定是否成功
     *
     * @param baseChance  基础成功率（百分比，如 50.0 表示 50%）
     * @param potionBonus 药水加成（百分比，如 10.0 表示 10%）
     * @param extraBonus  额外加成（百分比，如 5.0 表示 5%）
     * @return 是否成功
     */
    public static boolean rollSuccess(double baseChance, double potionBonus, double extraBonus) {
        double roll = BigDecimal.valueOf(KarUtils.nextDouble(100))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
        return roll < (baseChance + potionBonus + extraBonus);
    }

    /**
     * 掷骰判定是否成功（无额外加成）
     *
     * @param chance 成功率（百分比）
     * @return 是否成功
     */
    public static boolean rollSuccess(double chance) {
        return rollSuccess(chance, 0, 0);
    }

    /**
     * 随机掉级计算
     * 根据掉级数组的长度决定每个级别的概率分布
     *
     * @param downLevels 掉级数组（长度 1-4）
     * @return 掉落的等级数
     */
    public static int rollDownLevel(int[] downLevels) {
        if (downLevels == null || downLevels.length == 0 || downLevels.length > 4) {
            return 0;
        }
        int roll = new Random().nextInt(100);
        switch (downLevels.length) {
            case 4:
                if (roll < 40) return downLevels[3];
                if (roll < 70) return downLevels[2];
                if (roll < 90) return downLevels[1];
                return downLevels[0];
            case 3:
                if (roll < 60) return downLevels[2];
                if (roll < 90) return downLevels[1];
                return downLevels[0];
            case 2:
                if (roll < 60) return downLevels[1];
                return downLevels[0];
            default:
                return downLevels[0];
        }
    }
}
