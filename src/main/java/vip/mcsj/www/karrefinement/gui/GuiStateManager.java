package vip.mcsj.www.karrefinement.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ConcurrentHashMap;

/**
 * GUI 状态管理器
 * 从 KarEventListener 提取，集中管理淬炼/锻造/移星 GUI 的运行状态
 */
public class GuiStateManager {

    // 淬炼面板状态
    private static final ConcurrentHashMap<Player, Integer> refinementGuiOpen = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Player, Integer> refinementRunning = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Player, Inventory> refinementInvs = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Player, ItemStack[]> refinementCloseItems = new ConcurrentHashMap<>();

    // 锻造面板状态
    private static final ConcurrentHashMap<Player, Integer> forgeGuiOpen = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Player, Integer> forgeRunning = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Player, Inventory> forgeInvs = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Player, ItemStack[]> forgeCloseItems = new ConcurrentHashMap<>();

    // 移星面板状态
    private static final ConcurrentHashMap<Player, Integer> transformGuiOpen = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Player, Integer> transformRunning = new ConcurrentHashMap<>();

    // ==================== 淬炼 ====================

    public static boolean isRefinementRunning(Player p) {
        Integer state = refinementRunning.get(p);
        return state != null && state == 1;
    }

    public static void setRefinementRunning(Player p, boolean running) {
        refinementRunning.put(p, running ? 1 : 0);
    }

    public static boolean isRefinementGuiOpen(Player p) {
        Integer state = refinementGuiOpen.get(p);
        return state != null && state == 1;
    }

    public static void setRefinementGuiOpen(Player p, boolean open) {
        refinementGuiOpen.put(p, open ? 1 : 0);
    }

    public static Inventory getRefinementInv(Player p) {
        return refinementInvs.get(p);
    }

    public static void setRefinementInv(Player p, Inventory inv) {
        refinementInvs.put(p, inv);
    }

    public static ItemStack[] getRefinementCloseItems(Player p) {
        return refinementCloseItems.get(p);
    }

    public static void setRefinementCloseItems(Player p, ItemStack[] items) {
        refinementCloseItems.put(p, items);
    }

    // ==================== 锻造 ====================

    public static boolean isForgeRunning(Player p) {
        Integer state = forgeRunning.get(p);
        return state != null && state == 1;
    }

    public static void setForgeRunning(Player p, boolean running) {
        forgeRunning.put(p, running ? 1 : 0);
    }

    public static boolean isForgeGuiOpen(Player p) {
        Integer state = forgeGuiOpen.get(p);
        return state != null && state == 1;
    }

    public static void setForgeGuiOpen(Player p, boolean open) {
        forgeGuiOpen.put(p, open ? 1 : 0);
    }

    public static Inventory getForgeInv(Player p) {
        return forgeInvs.get(p);
    }

    public static void setForgeInv(Player p, Inventory inv) {
        forgeInvs.put(p, inv);
    }

    public static ItemStack[] getForgeCloseItems(Player p) {
        return forgeCloseItems.get(p);
    }

    public static void setForgeCloseItems(Player p, ItemStack[] items) {
        forgeCloseItems.put(p, items);
    }

    // ==================== 移星 ====================

    public static boolean isTransformRunning(Player p) {
        Integer state = transformRunning.get(p);
        return state != null && state == 1;
    }

    public static void setTransformRunning(Player p, boolean running) {
        transformRunning.put(p, running ? 1 : 0);
    }

    public static boolean isTransformGuiOpen(Player p) {
        Integer state = transformGuiOpen.get(p);
        return state != null && state == 1;
    }

    public static void setTransformGuiOpen(Player p, boolean open) {
        transformGuiOpen.put(p, open ? 1 : 0);
    }

    // ==================== 生命周期 ====================

    /**
     * 玩家登录时初始化所有状态
     */
    public static void onPlayerLogin(Player p) {
        refinementRunning.put(p, 0);
        refinementGuiOpen.put(p, 0);
        refinementCloseItems.put(p, new ItemStack[]{});

        forgeRunning.put(p, 0);
        forgeGuiOpen.put(p, 0);
        forgeCloseItems.put(p, new ItemStack[]{});

        transformRunning.put(p, 0);
        transformGuiOpen.put(p, 0);
    }

    /**
     * 玩家退出时清理所有状态
     */
    public static void onPlayerQuit(Player p) {
        refinementRunning.remove(p);
        refinementGuiOpen.remove(p);
        refinementInvs.remove(p);
        refinementCloseItems.remove(p);

        forgeRunning.remove(p);
        forgeGuiOpen.remove(p);
        forgeInvs.remove(p);
        forgeCloseItems.remove(p);

        transformRunning.remove(p);
        transformGuiOpen.remove(p);
    }

    /**
     * 玩家死亡时重置运行状态
     */
    public static void onPlayerDeath(Player p) {
        refinementRunning.put(p, 0);
        refinementGuiOpen.put(p, 0);

        forgeRunning.put(p, 0);
        forgeGuiOpen.put(p, 0);

        transformRunning.put(p, 0);
        transformGuiOpen.put(p, 0);
    }
}
