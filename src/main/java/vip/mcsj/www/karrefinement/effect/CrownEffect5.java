package vip.mcsj.www.karrefinement.effect;

import org.bukkit.Location;
import org.bukkit.Particle;
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
public class CrownEffect5 extends BukkitRunnable {

    /**
     * 玩家
     */
    private Player player;
    private double degree = 0.0;
    //标记从哪个角度开始画
    private double degree2 = 0.0;

    //画完整个圆的变量
    private double degree3 = 0.0;
    private double degree4 = -1;


    private int[][] a = new int[][]{
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1},
            {1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1},
            {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1},
            {0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0},
            {0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0},
            {0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0},
            {0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0},
            {0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0}
    };
    private BukkitTask task = null;
    public CrownEffect5(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (player == null || !player.isOnline()) {
            cancel();
        }

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
        playEffectLocation.getWorld().spawnParticle(cp.getParticles().get(0),playEffectLocation,1,0.0D,0.0D,0.0D,0.0D);
        //九星特效:degree4
        Location playEffectLocation2 = playerLocation.clone().add(0.9 * Math.cos(radians), 0.5*Math.acos(degree4), 0.9 * Math.sin(radians));
        playEffectLocation2.getWorld().spawnParticle(cp.getParticles().get(0),playEffectLocation2,1,0.0D,0.0D,0.0D,0.0D);
        //12星特效:degree2 degree3
        Location playerEffectLocation3 = playerLocation.clone().add(1.5*x2,y1,1.5*z2);
        playerEffectLocation3.getWorld().spawnParticle(cp.getParticles().get(3),playerEffectLocation3,3,0.0D,0.0D,0.0D,0.0D);

        //15星特效
        try {
            cp.addParticleFifteenth(player);
        }catch (Exception e){
            e.printStackTrace();
        }
        Location pLoc = player.getLocation();
        //18星特效
        for (int i = a.length - 1; i >= 0; i--) {
            for (int j = a[i].length - 1; j >= 0; j--) {
                if(a[i][j] != 0){
                    draw(pLoc,pLoc.clone().add(-a[i].length * 1.0 / 10 + j * 1.0 / 5 + 0.1, 0.2, 0),1.8 - i * 1.0 / 5 + 0.2,cp.getParticles().get(2), 20);
                }
            }
        }



        if (degree >= 360) {
            degree = 0;
        } else {
            degree += 20;
        }

        if(degree3 >= 360+degree2){
            degree2 += 20;
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

    public void draw(Location playerLocation,Location effectLocation,double g,Particle particle,double angle){
        Location ploc = playerLocation.clone();
        Location eloc = effectLocation.clone();
        double jdc = getAngle(ploc.getX(),ploc.getY(), eloc.getX(),eloc.getY());
        if(jdc < 90){
            jdc += angle;
        }else{
            jdc -= angle;
        }
        double jl = getDistance(ploc.getX(),ploc.getY(),eloc.getX(),eloc.getY());
        double radians = toRadians(jdc + ploc.getYaw() - 180);
        double x = Math.cos(radians) * jl;
        double y = Math.sin(radians) * jl;
        ploc.add(x,g,y);
        ploc.getWorld().spawnParticle(particle,ploc,1,0,0,0,0);
        ploc.subtract(x,g,y);
    }

    public double toRadians(double angdeg){
        return angdeg / 180.0 * Math.PI;
    }

    public double getDistance(double x1,double y1,double x2,double y2){
        double x = Math.abs(x2 - x1);
        double y = Math.abs(y2 - y1);
        return Math.sqrt(x*x + y*y);
    }

    public double getAngle(double x1,double y1,double x2,double y2){
        double x = x2 - x1;
        double y = y2 - y1;
        double hypotenuse = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
        double cos = x / hypotenuse;
        double radian = Math.acos(cos);
        double angle = 180 / (Math.PI / radian);
        if(y < 0){
            angle = -angle;
        }else if((y == 0) && (x < 0)){
            angle = 180;
        }
        return angle;
    }
}
