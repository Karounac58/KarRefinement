package vip.mcsj.www.karrefinement.service;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.datamanager.EquipmentDataManager;
import vip.mcsj.www.karrefinement.datamanager.LevelDataManager;
import vip.mcsj.www.karrefinement.object.Level;
import vip.mcsj.www.karrefinement.object.SpeStone;
import vip.mcsj.www.karrefinement.object.Stone;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 装备业务逻辑服务
 * 从 EquipmentDataManager 提取，负责升星/降星/设等级等操作
 */
public class EquipmentService {

    private final ItemStack equipmentItem;
    private final Player player;
    private final LoreBuilder loreBuilder;

    public EquipmentService(ItemStack equipmentItem) {
        this.equipmentItem = equipmentItem;
        this.player = null;
        this.loreBuilder = new LoreBuilder(equipmentItem);
    }

    public EquipmentService(ItemStack equipmentItem, Player player) {
        this.equipmentItem = equipmentItem;
        this.player = player;
        this.loreBuilder = new LoreBuilder(equipmentItem, player);
    }

    /**
     * 获取装备淬炼等级 (NBT)
     */
    public int getLevel() {
        return new NBTItem(this.equipmentItem).getInteger("refinement");
    }

    /**
     * 获取任意装备的淬炼等级
     */
    public static int getLevel(ItemStack item) {
        return new NBTItem(item).getInteger("refinement");
    }

    /**
     * 升星操作
     *
     * @return 是否成功
     */
    public boolean injuryUpStar() {
        if (getLevel() == LevelDataManager.levels.size()) {
            return false;
        }
        int oldLevel = getLevel();
        Level newLevelData = LevelDataManager.levels.get(oldLevel);
        String identifier = EquipmentDataManager.getEquipmentIdentifier(this.equipmentItem);
        List<String> newMainLore = newLevelData.getMainLore();
        List<String> newExtractLore = newLevelData.getExtractLores().get(identifier);

        // 从 0 升星
        if (oldLevel == 0) {
            loreBuilder.addRefinementInfo(newMainLore, newExtractLore, 1);
            return true;
        }

        // 从非 0 升星：先移除旧信息再添加新信息
        Level oldLevelData = LevelDataManager.levels.get(oldLevel - 1);
        List<String> oldMainLore = oldLevelData.getMainLore();
        List<String> oldExtractLore = oldLevelData.getExtractLores().get(identifier);
        loreBuilder.removeRefinementInfo(oldMainLore, oldExtractLore, oldLevel);
        loreBuilder.addRefinementInfo(newMainLore, newExtractLore, oldLevel + 1);

        return true;
    }

    /**
     * 设置淬炼等级（基本版本）
     *
     * @param level 目标等级
     * @return 是否成功
     */
    public boolean setRefinementLevel(int level) {
        if (level > LevelDataManager.levels.size()) {
            return false;
        }

        int oldLevel = getLevel();
        if (oldLevel == level) {
            return false;
        }

        String identifier = EquipmentDataManager.getEquipmentIdentifier(equipmentItem);

        // 设为 0 级：只移除
        if (level == 0) {
            Level oldLevelData = LevelDataManager.levels.get(oldLevel - 1);
            loreBuilder.removeRefinementInfo(oldLevelData.getMainLore(),
                    oldLevelData.getExtractLores().get(identifier), oldLevel);
            return true;
        }

        Level newLevelData = LevelDataManager.levels.get(level - 1);
        List<String> newMainLore = newLevelData.getMainLore();
        List<String> newExtractLore = newLevelData.getExtractLores().get(identifier);

        // 从 0 级设为目标等级
        if (oldLevel == 0) {
            loreBuilder.addRefinementInfo(newMainLore, newExtractLore, level);
            return true;
        }

        // 从非 0 级变更
        Level oldLevelData = LevelDataManager.levels.get(oldLevel - 1);
        loreBuilder.removeRefinementInfo(oldLevelData.getMainLore(),
                oldLevelData.getExtractLores().get(identifier), oldLevel);
        loreBuilder.addRefinementInfo(newMainLore, newExtractLore, level);

        return true;
    }

    /**
     * 设置淬炼等级（移星版本 — 保留宝石/保护符/精魂信息）
     *
     * @param level      目标等级
     * @param map        装备信息 Map
     * @param paperLevel 保护符等级
     * @param speStones  宝石列表
     * @param soulLevel  精魂等级
     * @return 是否成功
     */
    public boolean setRefinementLevel(int level, Map<String, List<String>> map, int paperLevel,
                                      List<SpeStone> speStones, int soulLevel) {
        if (level > LevelDataManager.levels.size()) {
            return false;
        }

        int oldLevel = getLevel();
        if (oldLevel == level) {
            return false;
        }

        String identifier = EquipmentDataManager.getEquipmentIdentifier(equipmentItem);

        // 设为 0 级
        if (level == 0) {
            Level oldLevelData = LevelDataManager.levels.get(oldLevel - 1);
            loreBuilder.removeRefinementInfo(oldLevelData.getMainLore(),
                    oldLevelData.getExtractLores().get(identifier), oldLevel);
            return true;
        }

        Level newLevelData = LevelDataManager.levels.get(level - 1);
        List<String> newMainLore = newLevelData.getMainLore();
        List<String> newExtractLore = newLevelData.getExtractLores().get(identifier);

        // 从 0 级设为目标等级
        if (oldLevel == 0) {
            loreBuilder.addRefinementInfo(newMainLore, newExtractLore, level, map, paperLevel, speStones, soulLevel);
            return true;
        }

        // 从非 0 级变更（移星模式：移除时也清除 NBT）
        Level oldLevelData = LevelDataManager.levels.get(oldLevel - 1);
        loreBuilder.removeRefinementInfoForTransform(oldLevelData.getMainLore(),
                oldLevelData.getExtractLores().get(identifier), oldLevel);
        loreBuilder.addRefinementInfo(newMainLore, newExtractLore, level, map, paperLevel, speStones, soulLevel);

        return true;
    }

    /**
     * 降星操作
     *
     * @param protectPaperLevel 保护符等级
     * @param stone             淬炼石（包含掉级信息）
     * @return 实际掉落的等级数
     */
    public int injuryDownStar(int protectPaperLevel, Stone stone) {
        int oldLevel = getLevel();
        Level oldLevelData = LevelDataManager.levels.get(oldLevel - 1);
        List<String> oldMainLore = oldLevelData.getMainLore();
        String identifier = EquipmentDataManager.getEquipmentIdentifier(this.equipmentItem);
        List<String> oldExtractLore = oldLevelData.getExtractLores().get(identifier);

        int downLevel = randomDownLevel(stone.getDownLevels());

        // 掉到 0 以下：直接归零
        if (oldLevel - downLevel < 0) {
            loreBuilder.removeRefinementInfoWithDownLevel(oldMainLore, oldExtractLore, oldLevel, downLevel);
            return oldLevel;
        }

        // 恰好掉到 0
        if (oldLevel - downLevel == 0) {
            if (oldLevel == protectPaperLevel) {
                return 0; // 保护符保护
            } else {
                loreBuilder.removeRefinementInfoWithDownLevel(oldMainLore, oldExtractLore, oldLevel, downLevel);
                return downLevel;
            }
        }

        // 正常掉星
        int realDownLevel;
        int newLevel;

        if (oldLevel - downLevel < protectPaperLevel) {
            // 掉星后低于保护符等级：只掉到保护符等级
            realDownLevel = oldLevel - protectPaperLevel;
            newLevel = oldLevel - realDownLevel;
        } else {
            realDownLevel = downLevel;
            newLevel = oldLevel - realDownLevel;
        }

        Level newLevelData = LevelDataManager.levels.get(newLevel - 1);
        List<String> newMainLore = newLevelData.getMainLore();
        List<String> newExtractLore = newLevelData.getExtractLores().get(identifier);

        loreBuilder.removeRefinementInfo(oldMainLore, oldExtractLore, oldLevel);
        loreBuilder.addRefinementInfo(newMainLore, newExtractLore, newLevel);

        return realDownLevel;
    }

    /**
     * 根据等级直接移除淬炼信息
     */
    public Map<String, List<String>> removeRefinementInfo(int nowLevel) {
        return loreBuilder.removeRefinementInfo(nowLevel);
    }

    /**
     * 随机掉级计算
     */
    public static int randomDownLevel(int[] nums) {
        Random random = new Random();
        int randomNumber = random.nextInt(100);
        if (nums.length > 4 || nums.length == 0) {
            return 0;
        }
        if (nums.length == 4) {
            if (randomNumber < 40) {
                return nums[3];
            } else if (randomNumber < 70) {
                return nums[2];
            } else if (randomNumber < 90) {
                return nums[1];
            } else {
                return nums[0];
            }
        } else if (nums.length == 3) {
            if (randomNumber < 60) {
                return nums[2];
            } else if (randomNumber < 90) {
                return nums[1];
            } else {
                return nums[0];
            }
        } else if (nums.length == 2) {
            if (randomNumber < 60) {
                return nums[1];
            } else {
                return nums[0];
            }
        } else {
            return nums[0];
        }
    }

    public LoreBuilder getLoreBuilder() {
        return loreBuilder;
    }
}
