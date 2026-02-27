package vip.mcsj.www.karrefinement.datamanager;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.object.InfiniteSoul;
import vip.mcsj.www.karrefinement.object.Level;
import vip.mcsj.www.karrefinement.object.ProtectPaper;
import vip.mcsj.www.karrefinement.object.SpeStone;
import vip.mcsj.www.karrefinement.utils.RandomLoreUtils;

import java.util.*;

/**
 * 装备Lore动态更新管理器
 * 用于检测和更新装备上的淬炼lore，当配置文件变更时自动同步
 */
public class LoreUpdateManager {

    // 当前lore版本号（基于配置文件的hash）
    private static String currentLoreVersion = "";
    
    // 是否启用动态lore更新
    private static boolean enableDynamicLoreUpdate = true;
    
    // 更新检查冷却时间（毫秒），防止频繁检查
    private static final long CHECK_COOLDOWN = 1000;
    
    // 玩家上次检查时间记录
    private static final Map<UUID, Long> lastCheckTime = new HashMap<>();
    
    // 已处理过的物品缓存（防止同一tick内重复处理）
    private static final Set<String> processedItems = new HashSet<>();

    /**
     * 初始化lore版本管理器
     */
    public static void init() {
        enableDynamicLoreUpdate = KarRefinement.instance.getConfig().getBoolean("settings.enableDynamicLoreUpdate", true);
        updateLoreVersion();
    }

    /**
     * 重新加载配置时更新版本号
     */
    public static void updateLoreVersion() {
        // 使用当前时间戳作为版本号（每次重载配置时更新）
        currentLoreVersion = String.valueOf(System.currentTimeMillis());
    }

    /**
     * 获取当前lore版本号
     */
    public static String getCurrentLoreVersion() {
        return currentLoreVersion;
    }

    /**
     * 检查并更新玩家背包中的所有淬炼装备
     * @param player 玩家
     */
    public static void checkAndUpdatePlayerInventory(Player player) {
        if (!enableDynamicLoreUpdate) {
            return;
        }

        // 检查冷却时间
        long now = System.currentTimeMillis();
        Long lastCheck = lastCheckTime.get(player.getUniqueId());
        if (lastCheck != null && (now - lastCheck) < CHECK_COOLDOWN) {
            return;
        }
        lastCheckTime.put(player.getUniqueId(), now);

        // 异步处理避免阻塞主线程
        new BukkitRunnable() {
            @Override
            public void run() {
                // 检查主手装备
                ItemStack mainHand = player.getInventory().getItemInMainHand();
                if (shouldUpdateItem(mainHand)) {
                    updateItemLore(mainHand);
                }

                // 检查副手装备
                ItemStack offHand = player.getInventory().getItemInOffHand();
                if (shouldUpdateItem(offHand)) {
                    updateItemLore(offHand);
                }

                // 检查盔甲
                for (ItemStack armor : player.getInventory().getArmorContents()) {
                    if (shouldUpdateItem(armor)) {
                        updateItemLore(armor);
                    }
                }

                // 检查背包物品（限制数量以优化性能）
                ItemStack[] contents = player.getInventory().getStorageContents();
                int checkLimit = Math.min(contents.length, 36); // 只检查前36格
                for (int i = 0; i < checkLimit; i++) {
                    if (shouldUpdateItem(contents[i])) {
                        updateItemLore(contents[i]);
                    }
                }
            }
        }.runTask(KarRefinement.instance);
    }

    /**
     * 检查单个物品是否需要更新
     * @param item 物品
     * @return 是否需要更新
     */
    public static boolean shouldUpdateItem(ItemStack item) {
        if (item == null || item.getType().name().equals("AIR")) {
            return false;
        }

        // 检查是否为可淬炼装备
        if (!EquipmentDataManager.isEquipmentLegal(item)) {
            return false;
        }

        // 检查是否有淬炼等级
        NBTItem nbtItem = new NBTItem(item);
        int refinementLevel = nbtItem.getInteger("refinement");
        if (refinementLevel <= 0) {
            return false;
        }

        // 检查lore版本
        String itemVersion = nbtItem.getString("loreversion");
        return !currentLoreVersion.equals(itemVersion);
    }

    /**
     * 更新单个物品的lore
     * @param item 物品
     */
    public static void updateItemLore(ItemStack item) {
        if (item == null || item.getType().name().equals("AIR")) {
            return;
        }

        NBTItem nbtItem = new NBTItem(item);
        int refinementLevel = nbtItem.getInteger("refinement");
        if (refinementLevel <= 0) {
            return;
        }

        // 获取装备类型标识
        String equipmentIdentifier = EquipmentDataManager.getEquipmentIdentifier(item);
        if (equipmentIdentifier == null) {
            return;
        }

        // 获取当前等级的配置（新配置）
        Level level = LevelDataManager.levels.get(refinementLevel - 1);
        if (level == null) {
            return;
        }

        // 获取装备上的所有附加信息
        List<SpeStone> speStones = SpecialStoneDataManager.getSpeStones(item);
        int paperLevel = nbtItem.getInteger("protector");
        int soulLevel = nbtItem.getInteger("infinite");

        // 获取随机lore（如果有）
        List<String> randomLore = null;
        if (RandomLoreUtils.hasRandomLore(item)) {
            randomLore = RandomLoreUtils.getRandomLore(item);
        }

        // 获取物品当前的lore
        ItemMeta meta = item.getItemMeta();
        List<String> currentLore = meta.getLore();
        if (currentLore == null) {
            currentLore = new ArrayList<>();
        }

        // 创建一个新的lore列表，用于保留原始lore（非系统lore）
        List<String> originalLore = new ArrayList<>();

        // 遍历当前lore，识别并跳过系统lore
        int i = 0;
        while (i < currentLore.size()) {
            String line = currentLore.get(i);

            // 检查是否是淬炼主标题
            if (line.equals(EquipmentDataManager.mainLore)) {
                // 跳过整个淬炼lore块（标题 + 等级lore + 属性lore）
                i++; // 跳过标题
                // 跳过等级lore（可能有多行）
                while (i < currentLore.size() && !isNextSystemSection(currentLore.get(i))) {
                    i++;
                }
                continue;
            }

            // 检查是否是宝石标题
            if (line.equals(EquipmentDataManager.speStoneLore)) {
                // 跳过整个宝石lore块
                i++; // 跳过标题
                // 跳过所有宝石lore行
                while (i < currentLore.size() && !isNextSystemSection(currentLore.get(i))) {
                    i++;
                }
                continue;
            }

            // 检查是否是保护符lore
            if (isPaperLore(line, paperLevel)) {
                i++;
                continue;
            }

            // 检查是否是精魂lore
            if (isSoulLore(line, soulLevel, item)) {
                i++;
                continue;
            }

            // 不是系统lore，保留
            originalLore.add(line);
            i++;
        }

        // 构建新的lore列表
        List<String> newLore = new ArrayList<>();

        // 按照sortOrder顺序构建lore
        for (String order : EquipmentDataManager.sortOrder) {
            switch (order) {
                case "{lore}":
                    newLore.addAll(originalLore);
                    break;

                case "{refinement}":
                    // 添加淬炼主标题
                    newLore.add(EquipmentDataManager.mainLore);
                    // 添加等级lore
                    newLore.addAll(level.getMainLore());
                    // 添加属性lore
                    List<String> extractLore = level.getExtractLores().get(equipmentIdentifier);
                    if (extractLore != null) {
                        if (randomLore != null) {
                            newLore.addAll(randomLore);
                        } else {
                            newLore.addAll(extractLore);
                        }
                    }
                    break;

                case "{spestone}":
                    if (!speStones.isEmpty()) {
                        newLore.add(EquipmentDataManager.speStoneLore);
                        for (SpeStone stone : speStones) {
                            newLore.addAll(stone.getEquipmentLore());
                        }
                    }
                    break;

                case "{paper}":
                    if (paperLevel > 0) {
                        String paperIdentifier = PaperDataManager.getPaperIdentifier(paperLevel);
                        if (paperIdentifier != null) {
                            newLore.add(PaperDataManager.papers.get(paperIdentifier).getName());
                        }
                    }
                    break;

                case "{soul}":
                    if (soulLevel > 0) {
                        String soulName = InfiniteSoulManager.getSoulName(item);
                        if (soulName != null) {
                            newLore.add(soulName);
                        }
                    }
                    break;
            }
        }

        // 应用新的lore
        meta.setLore(newLore);
        item.setItemMeta(meta);

        // 更新lore版本号
        NBT.modify(item, nbt -> {
            nbt.setString("loreversion", currentLoreVersion);
        });
    }

    /**
     * 检查是否是下一个系统区块的开始
     */
    private static boolean isNextSystemSection(String line) {
        return line.equals(EquipmentDataManager.mainLore)
                || line.equals(EquipmentDataManager.speStoneLore)
                || isPaperLorePattern(line)
                || isSoulLorePattern(line);
    }

    /**
     * 检查是否是保护符lore模式
     */
    private static boolean isPaperLorePattern(String line) {
        if (line == null) return false;
        // 保护符lore通常包含"保护符"或类似关键词
        for (ProtectPaper paper : PaperDataManager.papers.values()) {
            if (line.equals(paper.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否是精魂lore模式
     */
    private static boolean isSoulLorePattern(String line) {
        if (line == null) return false;
        // 精魂lore通常包含"精魂"或类似关键词
        for (InfiniteSoul soul : InfiniteSoulManager.infiniteSouls.values()) {
            if (line.equals(soul.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查指定行是否是该装备的保护符lore
     */
    private static boolean isPaperLore(String line, int paperLevel) {
        if (paperLevel <= 0) return false;
        String paperIdentifier = PaperDataManager.getPaperIdentifier(paperLevel);
        if (paperIdentifier == null) return false;
        return line.equals(PaperDataManager.papers.get(paperIdentifier).getName());
    }

    /**
     * 检查指定行是否是该装备的精魂lore
     */
    private static boolean isSoulLore(String line, int soulLevel, ItemStack item) {
        if (soulLevel <= 0) return false;
        String soulName = InfiniteSoulManager.getSoulName(item);
        if (soulName == null) return false;
        return line.equals(soulName);
    }

    /**
     * 强制刷新指定物品的lore（用于管理员命令）
     * @param item 物品
     */
    public static void forceUpdateItemLore(ItemStack item) {
        // 清除版本号强制更新
        NBT.modify(item, nbt -> {
            nbt.removeKey("loreversion");
        });
        updateItemLore(item);
    }

    /**
     * 清除所有玩家的检查冷却
     */
    public static void clearAllCooldowns() {
        lastCheckTime.clear();
        processedItems.clear();
    }
}
