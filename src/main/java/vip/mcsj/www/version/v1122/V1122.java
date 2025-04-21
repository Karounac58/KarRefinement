package vip.mcsj.www.version.v1122;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.utils.KarUtils;
import vip.mcsj.www.version.CustomMaterial;
import vip.mcsj.www.version.CustomParticle;
import vip.mcsj.www.version.CustomSounds;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class V1122 implements CustomParticle, CustomMaterial, CustomSounds {
    public List<Particle> particles = new ArrayList<>();
    public List<Sound> sounds = new ArrayList<>();
    public List<ItemStack> items = new ArrayList<>();

    public V1122() {
        initParticle();
        initSounds();
        initItems();
    }

    public void initParticle(){
        particles.add(Particle.valueOf("FLAME"));
        particles.add(Particle.valueOf("REDSTONE"));
        particles.add(Particle.valueOf("SMOKE_NORMAL"));
        particles.add(Particle.valueOf("VILLAGER_ANGRY"));
    }

    public void initSounds(){
        sounds.add(Sound.valueOf("ENTITY_FIREWORK_BLAST"));
        sounds.add(Sound.valueOf("ENTITY_EXPERIENCE_ORB_PICKUP"));
        sounds.add(Sound.valueOf("BLOCK_ANVIL_BREAK"));
    }

    public void initItems(){
        //红
        items.add(KarUtils.removeItemName(KarUtils.createColorPane(14)));
        //绿
        items.add(KarUtils.removeItemName(KarUtils.createColorPane(13)));
        //黑
        items.add(KarUtils.removeItemName(KarUtils.createColorPane(15)));
        //白
        items.add(KarUtils.removeItemName(KarUtils.createColorPane(0)));
        //灰
        items.add(KarUtils.removeItemName(KarUtils.createColorPane(7)));
        //蓝
        items.add(KarUtils.removeItemName(KarUtils.createColorPane(11)));
        items.add(new ItemStack(Material.valueOf("SIGN")));
    }
    //    private Method spawnParticleMethod;
//    private Player p;
//    public double degree = 0.0;
//    //标记从哪个角度开始画
//    public double degree2 = 0.0;
//
//    //画完整个圆的变量
//    public double degree3 = 0.0;
//    public double degree4 = -1;
//
//    private int[][] a = new int[][]{
//            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2},
//            {1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2},
//            {1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2},
//            {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2},
//            {0, 1, 1, 1, 0, 0, 0, 0, 0, 2, 2, 2, 0},
//            {0, 1, 1, 1, 1, 0, 0, 0, 2, 2, 2, 2, 0},
//            {0, 0, 1, 1, 1, 0, 0, 0, 2, 2, 2, 0, 0},
//            {0, 0, 1, 1, 1, 0, 0, 0, 2, 2, 2, 0, 0},
//            {0, 0, 0, 1, 1, 1, 0, 2, 2, 2, 0, 0, 0},
//            {0, 0, 0, 0, 0, 1, 0, 2, 0, 0, 0, 0, 0}
//    };
//    public ParticleV1122(Player p) {
//        this.p = p;
//        this.spawnParticleMethod = getSpigotMethod("org.bukkit.World", "spawnParticle",
//                Particle.class,
//                Location.class,
//                int.class,
//                double.class,
//                double.class,
//                double.class,
//                double.class
//        );
//    }
//
//    public void addParticleSixteen() throws InvocationTargetException, IllegalAccessException {
//        double radians = Math.toRadians(degree);
//        Location loc = p.getLocation().clone().add(0.5 * Math.cos(radians), 2D, 0.5 * Math.sin(radians));
//        Particle flame = Particle.valueOf("Flame");
//        spawnParticleMethod.invoke(p.getWorld(),flame,loc,1,0,0D,0.0D,0.0D,0.0D);
//    }
//
//    public void addParticleNineteen() throws InvocationTargetException, IllegalAccessException {
//        double radians = Math.toRadians(degree);
//        Location loc = p.getLocation().clone().add(0.9 * Math.cos(radians), 0.5*Math.acos(degree2), 0.9 * Math.sin(radians));
//        Particle flame = Particle.valueOf("Flame");
//        spawnParticleMethod.invoke(p.getWorld(),flame,loc,1,0,0D,0.0D,0.0D,0.0D);
//    }
//
//    public void addParticleTwelfth() throws InvocationTargetException, IllegalAccessException {
//        double radians = Math.toRadians(degree);
//        double x1 = Math.sin(radians);
//        double y1 = Math.sin(radians);
//        double z1 = Math.cos(radians);
//
//        double radians2 = Math.toRadians(degree3);
//        double x2 = Math.sin(radians2);
//        double z2 = Math.cos(radians2);
//        Location loc = p.getLocation().clone().add(1.5*x2,y1,1.5*z2);
//        Particle flame = Particle.valueOf("Flame");
////        playerEffectLocation3.getWorld().spawnParticle(flame,playerEffectLocation3,3,0.0D,0.0D,0.0D,0.0D);
//        spawnParticleMethod.invoke(p.getWorld(),flame,3,0.0D,0.0D,0.0D,0.0D);
//    }
//
    public void addParticleFifteenth(Player p) throws InvocationTargetException, IllegalAccessException {
        Location loc = p.getLocation().clone();
        loc.setPitch(0.0F);
        Location loc1 = loc.clone();
        Location loc2 = loc.clone();
        Location loc3 = loc.clone();
        loc.setYaw(45.0F);
        loc.add(loc.getDirection().multiply(1));
        loc1.setYaw(-45.0F);
        loc1.add(loc1.getDirection().multiply(1));
        loc2.setYaw(135.0F);
        loc2.add(loc2.getDirection().multiply(1));
        loc3.setYaw(-135.0F);
        loc3.add(loc3.getDirection().multiply(1));
        Random rand = new Random();
        float size = 1.1f;
        Particle redStone = Particle.valueOf("REDSTONE");
        for (int i = 0; i < 5; i++) {
            float red = rand.nextFloat();
            float green = rand.nextFloat();
            float blue = rand.nextFloat();
//            float shit = KarUtils.nextFloat(359);
//            int rgb = java.awt.Color.HSBtoRGB(shit, 1.0f, 1.0f);
//            Color color = Color.fromRGB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
//            Particle.DustOptions options = new Particle.DustOptions(color,1.0F);
//            loc.getWorld().spawnParticle(KarRefinement.particles[1], loc,3,red*size,0,blue,1);
//            locc1.getWorld().spawnParticle(KarRefinement.particles[1],locc1,3,red*size,0,blue,1);
//            locc2.getWorld().spawnParticle(KarRefinement.particles[1],locc2,3,red*size,0,blue,1);
//            locc3.getWorld().spawnParticle(KarRefinement.particles[1],locc3,3,red*size,0,blue,1);

            loc2.getWorld().spawnParticle(particles.get(1), loc2,3,red*size,0,blue,1);
            loc1.getWorld().spawnParticle(particles.get(1),loc1,3,red*size,0,blue,1);
            loc2.getWorld().spawnParticle(particles.get(1),loc2,3,red*size,0,blue,1);
            loc3.getWorld().spawnParticle(particles.get(1),loc3,3,red*size,0,blue,1);
        }
    }

    @Override
    public List<Particle> getParticles() {
        return particles;
    }

    @Override
    public List<ItemStack> getItems() {
        return items;
    }

    @Override
    public List<Sound> getSounds() {
        return sounds;
    }
//
//    public void addParticleEighteenth(){
//        Location pLoc = p.getLocation();
//        for (int i = a.length - 1; i >= 0; i--) {
//            for (int j = a[i].length - 1; j >= 0; j--) {
//                if(a[i][j] != 0){
//                    draw(pLoc,pLoc.clone().add(-a[i].length * 1.0 / 10 + j * 1.0 / 5 + 0.1, 0.2, 0),1.8 - i * 1.0 / 5 + 0.2,Particle.valueOf("SMOKE_NORMAL"), 20);
//                }
//            }
//        }
//    }
//
//    public void changeDegree(){
//        if (degree >= 360) {
//            degree = 0;
//        } else {
//            degree += 20;
//        }
//
//        if(degree3 >= 360+degree2){
//            degree2 += 20;
//            if(degree2 >= 360){
//                degree2 = 0;
//            }
//            degree3 = degree2;
//        }else{
//            degree3 += 20;
//        }
//
//        //九星特效
//        if(degree4 > 1){
//            degree4 = -1;
//        }else{
//            degree4 += 0.03;
//        }
//    }
//
//    private void draw(Location playerLocation,Location effectLocation,double g,Particle particle,double angle){
//        Location ploc = playerLocation.clone();
//        Location eloc = effectLocation.clone();
//        double jdc = getAngle(ploc.getX(),ploc.getY(), eloc.getX(),eloc.getY());
//        if(jdc < 90){
//            jdc += angle;
//        }else{
//            jdc -= angle;
//        }
//        double jl = getDistance(ploc.getX(),ploc.getY(),eloc.getX(),eloc.getY());
//        double radians = toRadians(jdc + ploc.getYaw() - 180);
//        double x = Math.cos(radians) * jl;
//        double y = Math.sin(radians) * jl;
//        ploc.add(x,g,y);
//        ploc.getWorld().spawnParticle(particle,ploc,1,0,0,0,0);
//        ploc.subtract(x,g,y);
//    }
//
//    private double toRadians(double angdeg){
//        return angdeg / 180.0 * Math.PI;
//    }
//
//    private double getDistance(double x1,double y1,double x2,double y2){
//        double x = Math.abs(x2 - x1);
//        double y = Math.abs(y2 - y1);
//        return Math.sqrt(x*x + y*y);
//    }
//
//    private double getAngle(double x1,double y1,double x2,double y2){
//        double x = x2 - x1;
//        double y = y2 - y1;
//        double hypotenuse = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
//        double cos = x / hypotenuse;
//        double radian = Math.acos(cos);
//        double angle = 180 / (Math.PI / radian);
//        if(y < 0){
//            angle = -angle;
//        }else if((y == 0) && (x < 0)){
//            angle = 180;
//        }
//        return angle;
//    }
}
