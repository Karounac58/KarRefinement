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
public class CrownEffect extends BukkitRunnable {

    /**
     * 玩家
     */
    private Player player;
    private double degree = 0;
    private double degree2 = -1;
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
        playEffectLocation.getWorld().spawnParticle(Particle.FLAME,playEffectLocation,1,0.0D,0.0D,0.0D,0.0D);


//        //第二种特效，环绕圆
//        Location playerLocation = player.getLocation().add(0,0D,0);
//        double radians = Math.toRadians(degree);
//        double x1 = Math.sin(radians);
//        double y1 = Math.sin(radians);
//        double z1 = Math.cos(radians);
//
//        Location playerEffectLocation = playerLocation.clone().add(x1,y1,z1);
//        playerEffectLocation.getWorld().spawnParticle(Particle.VILLAGER_ANGRY,playerEffectLocation,3,0.0D,0.0D,0.0D,0.0D);
//        //叠加特效，双重环绕圆
//        double x2 = Math.cos(radians);
//        double y2 = Math.sin(radians);
//        double z2 = Math.sin(radians);
//        Location playerEffectLocation2 = playerLocation.clone().add(x2,y2,z2);
//        playerEffectLocation.getWorld().spawnParticle(Particle.VILLAGER_ANGRY,playerEffectLocation2,3,0.0D,0.0D,0.0D,0.0D);


//        //第三种特效，火焰粒子从下按圆圈旋转到上
//        Location playerLocation = player.getLocation();
//        double radians = Math.toRadians(degree);
//
//        Location playEffectLocation = playerLocation.clone().add(0.5 * Math.cos(radians), 0.5*Math.acos(degree2), 0.5 * Math.sin(radians));
////        ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(Color.ORANGE), playEffectLocation, 50);
//        playEffectLocation.getWorld().spawnParticle(Particle.FLAME,playEffectLocation,5,0.0D,0.0D,0.0D,0.0D);
        if (degree >= 360) {
            degree = 0;
        } else {
            degree += 20;
        }
//
//        if(degree2 > 1){
//            degree2 = -1;
//        }else{
//            degree2 += 0.03;
//        }
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
