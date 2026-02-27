package vip.mcsj.www.karrefinement.version.v1122;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.version.CustomPath;
import vip.mcsj.www.karrefinement.version.CustomSounds;
import vip.mcsj.www.karrefinement.utils.KarUtils;
import vip.mcsj.www.karrefinement.version.CustomMaterial;
import vip.mcsj.www.karrefinement.version.CustomParticle;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class V1122 implements CustomParticle, CustomMaterial, CustomSounds, CustomPath {
    public String originPath = "";
    public List<Particle> particles = new ArrayList<>();
    public List<Sound> sounds = new ArrayList<>();
    public List<ItemStack> items = new ArrayList<>();

    public V1122() {
        initParticle();
        initSounds();
        initItems();
        initPath();
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

    public void initPath(){
        originPath = "v1122/";
    }

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
            loc.getWorld().spawnParticle(particles.get(1), loc,3,red*size,0,blue,1);
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

    @Override
    public String getPath() {
        return originPath;
    }
}
