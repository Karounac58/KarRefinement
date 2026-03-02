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
public class CrownEffect2 extends BukkitRunnable {

    /**
     * 玩家
     */
    private Player player;
    private double degree = 0;
    private double degree2 = -1;
    private BukkitTask task = null;
    public CrownEffect2(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (player == null || !player.isOnline()) {
            cancel();
        }

        Location playerLocation = player.getLocation();
        double radians = Math.toRadians(degree);
        //六星特效
        Location playEffectLocation = playerLocation.clone().add(0.5 * Math.cos(radians), 2D, 0.5 * Math.sin(radians));
        playEffectLocation.getWorld().spawnParticle(cp.getParticles().get(0),playEffectLocation,1,0.0D,0.0D,0.0D,0.0D);
        //九星特效
        Location playEffectLocation2 = playerLocation.clone().add(0.9 * Math.cos(radians), 0.5*Math.acos(degree2), 0.9 * Math.sin(radians));
        playEffectLocation2.getWorld().spawnParticle(cp.getParticles().get(0),playEffectLocation2,1,0.0D,0.0D,0.0D,0.0D);



        if (degree >= 360) {
            degree = 0;
        } else {
            degree += 20;
        }

        if(degree2 > 1){
            degree2 = -1;
        }else{
            degree2 += 0.03;
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
