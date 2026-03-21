package vip.mcsj.www.karrefinement.api;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.api.gui.GuiSlotRegistry;
import vip.mcsj.www.karrefinement.api.gui.GuiType;
import vip.mcsj.www.karrefinement.api.placeholder.PlaceholderRegistry;
import vip.mcsj.www.karrefinement.datamanager.*;
import vip.mcsj.www.karrefinement.gui.*;
import vip.mcsj.www.karrefinement.gui.holder.KarCompoundStoneInvHolder;
import vip.mcsj.www.karrefinement.gui.holder.KarRefinementInvHolder;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.object.EquipmentMaterial;
import vip.mcsj.www.karrefinement.object.Level;
import vip.mcsj.www.karrefinement.object.Stone;
import vip.mcsj.www.karrefinement.service.EquipmentService;
import vip.mcsj.www.karrefinement.service.gui.DefaultGuiSlotRegistry;
import vip.mcsj.www.karrefinement.service.placeholder.DefaultPlaceholderRegistry;
import vip.mcsj.www.karrefinement.service.refinement.RefinementResult;
import vip.mcsj.www.karrefinement.service.refinement.RefinementService;
import vip.mcsj.www.karrefinement.service.refinement.RefinementStrategy;

import java.util.*;

/**
 * KarRefinement 公共 API（门面模式）
 * 外部插件通过此类与 KarRefinement 交互
 */
public class KarRefinementAPI {

    private static KarRefinementAPI instance;

    // ========= 子系统注册中心 =========
    private final DefaultGuiSlotRegistry guiSlotRegistry;
    private final DefaultPlaceholderRegistry placeholderRegistry;

    // ========= 淬炼服务 =========
    private final RefinementService refinementService;

    // ========= 自定义淬炼策略 =========
    private final Map<String, RefinementStrategy> customStrategies = new HashMap<>();

    public KarRefinementAPI() {
        this.guiSlotRegistry = new DefaultGuiSlotRegistry();
        this.placeholderRegistry = new DefaultPlaceholderRegistry();
        this.refinementService = new RefinementService();
        instance = this;
    }

    // ========= 获取 API 实例 =========

    /**
     * 获取 API 实例
     */
    public static KarRefinementAPI getInstance() {
        return instance;
    }

    // ========= 子系统入口 =========

    /**
     * 获取 GUI 槽位注册中心
     */
    public static GuiSlotRegistry getGuiSlotRegistry() {
        return instance.guiSlotRegistry;
    }

    /**
     * 获取内部 GUI 槽位注册中心实现（内部使用）
     */
    public DefaultGuiSlotRegistry getInternalGuiSlotRegistry() {
        return guiSlotRegistry;
    }

    /**
     * 获取 PAPI 变量注册中心
     */
    public static PlaceholderRegistry getPlaceholderRegistry() {
        return instance.placeholderRegistry;
    }

    /**
     * 获取内部 PAPI 注册中心实现（内部使用）
     */
    public DefaultPlaceholderRegistry getInternalPlaceholderRegistry() {
        return placeholderRegistry;
    }

    // ========= 暴露 ServiceRegistry =========

    /**
     * 获取已注册的内部服务实例
     * @param type 服务类型
     * @return 服务实例，若未注册则返回 null
     */
    public static <T> T getService(Class<T> type) {
        return KarRefinement.getContext().getRegistry().get(type);
    }

    // ========= 新增查询方法 =========

    /**
     * 获取装备当前淬炼等级
     */
    public static int getEquipmentLevel(ItemStack item) {
        return EquipmentService.getLevel(item);
    }

    /**
     * 获取装备保护符等级
     */
    public static int getProtectPaperLevel(ItemStack item) {
        PaperDataManager pdm = new PaperDataManager(item);
        return pdm.getPaperLevel();
    }

    /**
     * 获取淬炼石信息
     * @return 石头信息，如果物品不是淬炼石则返回空
     */
    public static Optional<Stone> getStoneInfo(ItemStack item) {
        if (!StoneDataManager.isStoneLegal(item)) {
            return Optional.empty();
        }
        return Optional.ofNullable(StoneDataManager.getStone(item));
    }

    /**
     * 获取指定等级的配置
     * @param level 淬炼等级（从1开始）
     * @return 等级配置，如果等级不存在则返回空
     */
    public static Optional<Level> getLevelConfig(int level) {
        if (level < 1 || level > LevelDataManager.levels.size()) {
            return Optional.empty();
        }
        return Optional.ofNullable(LevelDataManager.levels.get(level - 1));
    }

    /**
     * 获取所有已注册的淬炼石名称
     */
    public static Set<String> getRegisteredStones() {
        return Collections.unmodifiableSet(StoneDataManager.stones.keySet());
    }

    /**
     * 获取所有已注册的装备类型
     */
    public static List<EquipmentMaterial> getEquipmentTypes() {
        return Collections.unmodifiableList(KarRefinement.types);
    }

    /**
     * 获取玩家统计数据
     */
    public static PlayerStatsDataManager.PlayerStats getPlayerStats(UUID uuid) {
        return PlayerStatsDataManager.getPlayerStats(uuid);
    }

    // ========= 新增操作方法 =========

    /**
     * 以编程方式执行淬炼（触发事件）
     */
    public static RefinementResult performRefinement(OfflinePlayer player, ItemStack equipment, Stone stone, double extraBonus) {
        return instance.refinementService.refine(player, equipment, stone, extraBonus);
    }

    /**
     * 以编程方式打开 GUI
     */
    public static void openGui(Player player, GuiType guiType) {
        switch (guiType) {
            case REFINEMENT:
                Inventory rInv = Bukkit.createInventory(new KarRefinementInvHolder(), 54,
                        PlaceholderAPI.setPlaceholders(player, KarRefinementGui.title));
                KarRefinementGui.setInvInitial(rInv, player);
                player.openInventory(rInv);
                break;
            case FORGE:
                Inventory fInv = Bukkit.createInventory(new KarForgeInvHolder(), 45,
                        PlaceholderAPI.setPlaceholders(player, KarForgeGui.title));
                KarForgeGui.initInv(fInv, player);
                player.openInventory(fInv);
                break;
            case COMPOUND_STONE:
                Inventory cInv = Bukkit.createInventory(new KarCompoundStoneInvHolder(), 54,
                        PlaceholderAPI.setPlaceholders(player, KarCompoundStoneGui.title));
                KarCompoundStoneGui.openGuiForPlayer(cInv, player);
                break;
            case TRANSFORM:
                Inventory tInv = Bukkit.createInventory(
                        new KarTransformStarGui.KarTransformStarGuiInvHolder(),
                        KarTransformStarGui.size,
                        PlaceholderAPI.setPlaceholders(player, KarTransformStarGui.title));
                KarTransformStarGui.initInv(tInv, player);
                player.openInventory(tInv);
                break;
            case COMPOUND_PIECE:
                Inventory cpInv = Bukkit.createInventory(
                        new KarCompoundPieceGui.KarCompoundPieceGuiInvHolder(),
                        KarCompoundPieceGui.size,
                        PlaceholderAPI.setPlaceholders(player, KarCompoundPieceGui.title));
                KarCompoundPieceGui.initInv(cpInv, player);
                player.openInventory(cpInv);
                break;
            case TAKE_ITEM:
                KarTakeItemGui.openKarTakeItemGui(player);
                break;
        }
    }

    /**
     * 注册自定义淬炼策略
     */
    public static void registerRefinementStrategy(String id, RefinementStrategy strategy) {
        instance.customStrategies.put(id, strategy);
    }

    /**
     * 获取自定义淬炼策略
     */
    public static RefinementStrategy getRefinementStrategy(String id) {
        return instance.customStrategies.get(id);
    }

    /**
     * 注册自定义装备类型（使非原版物品可淬炼）
     */
    public static void registerEquipmentType(EquipmentMaterial material) {
        KarRefinement.types.add(material);
    }

    // ========= Builder 模式物品创建 =========

    /**
     * 创建淬炼石 Builder
     * @param stoneName 淬炼石配置名
     */
    public static StoneBuilder stoneBuilder(String stoneName) {
        return new StoneBuilder(stoneName);
    }

    /**
     * 创建保护符 Builder
     * @param paperName 保护符配置名
     */
    public static PaperBuilder paperBuilder(String paperName) {
        return new PaperBuilder(paperName);
    }

    // ========= 保留原有的静态便捷方法（向后兼容）=========

    public static void setItemLevel(ItemStack item, int level) {
        if (EquipmentDataManager.isEquipmentLegal(item)) {
            new EquipmentDataManager(item).setRefinementLevel(level);
        }
    }

    public static boolean canRefinement(ItemStack item) {
        return EquipmentDataManager.isEquipmentLegal(item);
    }

    public static boolean addProtectPaper(ItemStack itemPaper, ItemStack item) {
        return PaperDataManager.protectorPaperUp(itemPaper, item);
    }

    public static Level getMinLevel(Player p) {
        return LevelDataManager.getMinLevel(p);
    }

    public static ItemStack createStone(String stoneName) {
        StoneDataManager sdm = new StoneDataManager(stoneName);
        return sdm.createStone();
    }

    public static ItemStack createPaper(String paperName) {
        PaperDataManager pdm = new PaperDataManager(paperName);
        return pdm.createProtectedPaper();
    }

    public static ItemStack createSpeStone(String speStoneName) {
        SpecialStoneDataManager ssdm = new SpecialStoneDataManager(speStoneName);
        return ssdm.createSpeStone();
    }

    public static ItemStack createSoul(String soulName) {
        InfiniteSoulManager ism = new InfiniteSoulManager(soulName);
        return ism.createInfiniteSoul();
    }

    public static ItemStack createDUPaper(String dupaperName) {
        return DUPaperDataManager.createDUPaper(dupaperName);
    }

    public static ItemStack createDetachItem() {
        return DetachDataManager.createPaperDetachItem();
    }

    public static ItemStack createDetachItemPiece(String pieceName) {
        DetachDataManager ddm = new DetachDataManager(pieceName);
        return ddm.createPaperPiece();
    }

    public static ItemStack createPotion(String potionName) {
        PotionDataManager pdm = new PotionDataManager(potionName);
        return pdm.createPotion();
    }

    public static ItemStack createAdhesive(String adhesiveName) {
        return AdhesiveDataManager.createAdhesiveItem(adhesiveName);
    }
}
