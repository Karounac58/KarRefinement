package vip.mcsj.www.effect;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import vip.mcsj.www.datamanager.EffectDataManager;
import vip.mcsj.www.main.KarRefinement;

import java.util.Arrays;
import java.util.List;

/**
 * 王冠特效
 *
 * @author Zoyn
 */
public class CrownEffect3 extends BukkitRunnable {

    /**
     * 玩家
     */
    private Player player;
    private double degree = 0;
    private double degree2 = 0;

    private double degree3 = 0;

    private double degree4 = -1;
    private BukkitTask task = null;
    public CrownEffect3(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (player == null || !player.isOnline()) {
            cancel();
        }

//        //第一种特效，圆在头上画圈圈
//        Location playerLocation = player.getLocation();
//        double radians = Math.toRadians(degree);
//
//        Location playEffectLocation = playerLocation.clone().add(0.3 * Math.cos(radians), 2D, 0.3 * Math.sin(radians));
////        ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(Color.ORANGE), playEffectLocation, 50);
//        playEffectLocation.getWorld().spawnParticle(Particle.WHITE_ASH,playEffectLocation,50);



        Location playerLocation = player.getLocation().add(0,0D,0);
        double radians = Math.toRadians(degree);
        double x1 = Math.sin(radians);
        double y1 = Math.sin(radians);
        double z1 = Math.cos(radians);

        double radians2 = Math.toRadians(degree3);
        double x2 = Math.sin(radians2);
        double z2 = Math.cos(radians2);

        //六星特效:degree1
        Location playEffectLocation = playerLocation.clone().add(0.5 * Math.cos(radians), 2D, 0.5 * Math.sin(radians));
        playEffectLocation.getWorld().spawnParticle(Particle.FLAME,playEffectLocation,1,0.0D,0.0D,0.0D,0.0D);
        //九星特效:degree4
        Location playEffectLocation2 = playerLocation.clone().add(0.9 * Math.cos(radians), 0.5*Math.acos(degree4), 0.9 * Math.sin(radians));
        playEffectLocation2.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME,playEffectLocation2,1,0.0D,0.0D,0.0D,0.0D);

        //十二星特效:degree1 degree2 degree3
        Location playerEffectLocation3 = playerLocation.clone().add(1.5*x2,y1,1.5*z2);
        playerEffectLocation3.getWorld().spawnParticle(Particle.VILLAGER_ANGRY,playerEffectLocation3,3,0.0D,0.0D,0.0D,0.0D);
        if (degree >= 360) {
            degree = 0;
        } else {
            degree += 20;
        }

        if(degree3 >= 360+degree2){
            degree2 += 40;
            if(degree2 >= 360){
                degree2 = 0;
            }
            degree3 = degree2;
        }else{
            degree3 += 20;
        }

        //九星特效
        if(degree4 > 1){
            degree4 = -1;
        }else{
            degree4 += 0.03;
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
