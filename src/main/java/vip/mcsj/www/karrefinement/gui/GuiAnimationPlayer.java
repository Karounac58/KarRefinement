package vip.mcsj.www.karrefinement.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import vip.mcsj.www.karrefinement.main.KarRefinement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * GUI 动画播放器
 * 从 KarRefinementGui.playInvVideo / KarForgeGui.playInvVideo 提取通用逻辑
 */
public class GuiAnimationPlayer {

    /**
     * 动画完成后的回调
     */
    public interface AnimationCallback {
        void onComplete();
    }

    /**
     * 检查动画是否应取消的回调
     */
    public interface CancelChecker {
        boolean shouldCancel();
    }

    /**
     * 播放顺序动画（按固定顺序依次填充槽位）
     *
     * @param inv          目标容器
     * @param slots        动画槽位列表（按顺序播放）
     * @param videoItem    动画物品
     * @param player       玩家
     * @param tickInterval tick 间隔
     * @param onStart      动画开始时执行
     * @param cancelCheck  每帧检查是否取消
     * @param onComplete   动画完成时执行
     * @param onCancel     动画取消时执行
     */
    public static void playSequential(Inventory inv, List<Integer> slots, ItemStack videoItem,
                                      Player player, long tickInterval,
                                      Runnable onStart,
                                      CancelChecker cancelCheck,
                                      AnimationCallback onComplete,
                                      Runnable onCancel) {
        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                if (index == 0 && onStart != null) {
                    onStart.run();
                }
                if (cancelCheck != null && cancelCheck.shouldCancel()) {
                    if (onCancel != null) onCancel.run();
                    this.cancel();
                    return;
                }
                if (index >= slots.size()) {
                    if (onComplete != null) onComplete.onComplete();
                    this.cancel();
                    return;
                }
                inv.setItem(slots.get(index), videoItem);
                player.updateInventory();
                player.playSound(player.getLocation(), KarRefinement.cs.getSounds().get(0), 1, 1);
                index++;
            }
        }.runTaskTimer(KarRefinement.instance, 0L, tickInterval);
    }

    /**
     * 播放随机动画（随机顺序填充槽位）
     *
     * @param inv          目标容器
     * @param slots        动画槽位列表（将被随机打乱）
     * @param videoItem    动画物品
     * @param player       玩家
     * @param tickInterval tick 间隔
     * @param onStart      动画开始时执行
     * @param cancelCheck  每帧检查是否取消
     * @param onComplete   动画完成时执行
     * @param onCancel     动画取消时执行
     */
    public static void playRandom(Inventory inv, List<Integer> slots, ItemStack videoItem,
                                  Player player, long tickInterval,
                                  Runnable onStart,
                                  CancelChecker cancelCheck,
                                  AnimationCallback onComplete,
                                  Runnable onCancel) {
        List<Integer> remaining = new ArrayList<>(slots);
        int totalSize = remaining.size();

        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                if (index == 0 && onStart != null) {
                    onStart.run();
                }
                if (cancelCheck != null && cancelCheck.shouldCancel()) {
                    if (onCancel != null) onCancel.run();
                    this.cancel();
                    return;
                }
                if (index >= totalSize) {
                    if (onComplete != null) onComplete.onComplete();
                    this.cancel();
                    return;
                }
                int randIdx = ThreadLocalRandom.current().nextInt(remaining.size());
                int slot = remaining.remove(randIdx);
                inv.setItem(slot, videoItem);
                player.updateInventory();
                player.playSound(player.getLocation(), KarRefinement.cs.getSounds().get(0), 1, 1);
                index++;
            }
        }.runTaskTimer(KarRefinement.instance, 0L, tickInterval);
    }
}
