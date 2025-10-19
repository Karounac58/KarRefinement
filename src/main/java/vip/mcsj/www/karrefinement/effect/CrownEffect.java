package vip.mcsj.www.karrefinement.effect;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import vip.mcsj.www.karrefinement.datamanager.EffectDataManager;
import vip.mcsj.www.karrefinement.main.KarRefinement;

import static vip.mcsj.www.karrefinement.main.KarRefinement.cp;

/**
 * 王冠特效
 *
 * @author Zoyn
 */
public class CrownEffect extends BukkitRunnable {

    /**
     * 玩家
     */
    private Player player;
    private double degree = 0;
    private BukkitTask task = null;

    public CrownEffect(Player player) {
        this.player = player;

    }

    @Override
    public void run() {
        if (player == null || !player.isOnline()) {
            cancel();
        }

        //第一种特效，圆在头上画圈圈
        Location playerLocation = player.getLocation();
        double radians = Math.toRadians(degree);

        Location playEffectLocation = playerLocation.clone().add(0.5 * Math.cos(radians), 2D, 0.5 * Math.sin(radians));
        playEffectLocation.getWorld().spawnParticle(cp.getParticles().get(0),playEffectLocation,1,0.0D,0.0D,0.0D,0.0D);

        if (degree >= 360) {
            degree = 0;
        } else {
            degree += 20;
        }
    }

    /**
     * 开启特效的方法
     */
    public void startEffect() {
        String name = player.getName();
        task = runTaskTimer(KarRefinement.instance, 0L, 1L);
        EffectDataManager.addTaskToMap(name, task);
    }

    /**
     * 关闭特效的方法
     */
    public void stopEffect() {
        String name = player.getName();
        EffectDataManager.getTask(name).cancel();
        EffectDataManager.removeTaskFromMap(name);
    }
}
