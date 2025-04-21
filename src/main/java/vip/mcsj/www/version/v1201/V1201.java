package vip.mcsj.www.version.v1201;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.version.CustomMaterial;
import vip.mcsj.www.version.CustomParticle;
import vip.mcsj.www.utils.KarUtils;
import vip.mcsj.www.version.CustomSounds;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class V1201 implements CustomParticle, CustomMaterial, CustomSounds {
    public List<Particle> particles = new ArrayList<>();
    public List<Sound> sounds = new ArrayList<>();
    public List<ItemStack> items = new ArrayList<>();

    public V1201() {
        initItems();
        initParticle();
        initSounds();
    }

    public void initParticle(){
        particles.add(Particle.valueOf("FLAME"));
        particles.add(Particle.valueOf("REDSTONE"));
        particles.add(Particle.valueOf("SMOKE_NORMAL"));
        particles.add(Particle.valueOf("VILLAGER_ANGRY"));
    }

    public void initSounds(){
        sounds.add(Sound.valueOf("ENTITY_FIREWORK_ROCKET_BLAST"));
        sounds.add(Sound.valueOf("ENTITY_EXPERIENCE_ORB_PICKUP"));
        sounds.add(Sound.valueOf("BLOCK_ANVIL_BREAK"));
    }

    public void initItems(){
        //红
        items.add(KarUtils.removeItemName(new ItemStack(Material.valueOf("RED_STAINED_GLASS_PANE"))));
        //绿
        items.add(KarUtils.removeItemName(new ItemStack(Material.valueOf("GREEN_STAINED_GLASS_PANE"))));
        //黑
        items.add(KarUtils.removeItemName(new ItemStack(Material.valueOf("BLACK_STAINED_GLASS_PANE"))));
        //白
        items.add(KarUtils.removeItemName(new ItemStack(Material.valueOf("WHITE_STAINED_GLASS_PANE"))));
        //灰
        items.add(KarUtils.removeItemName(new ItemStack(Material.valueOf("GRAY_STAINED_GLASS_PANE"))));

        items.add(KarUtils.removeItemName(new ItemStack(Material.valueOf("BLUE_STAINED_GLASS_PANE"))));

        items.add(KarUtils.removeItemName(new ItemStack(Material.valueOf("OAK_SIGN"))));
    }

    @Override
    public void addParticleFifteenth(Player p) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, InvocationTargetException, IllegalAccessException {
        Location loc2 = p.getLocation().clone();
        //15星特效
        loc2.setPitch(0.0F);
        Location locc1 = loc2.clone();
        Location locc2 = loc2.clone();
        Location locc3 = loc2.clone();
        loc2.setYaw(45.0F);
        loc2.add(loc2.getDirection().multiply(1));
        locc1.setYaw(-45.0F);
        locc1.add(locc1.getDirection().multiply(1));
        locc2.setYaw(135.0F);
        locc2.add(locc2.getDirection().multiply(1));
        locc3.setYaw(-135.0F);
        locc3.add(locc3.getDirection().multiply(1));
        for (int i = 0; i < 5; i++) {
            float shit = KarUtils.nextFloat(359);
            int rgb = java.awt.Color.HSBtoRGB(shit, 1.0f, 1.0f);
            Color color = Color.fromRGB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
            Class dustOptionsClass = Class.forName("org.bukkit.Particle$DustOptions");
            Object dustOptions = dustOptionsClass.getDeclaredConstructor(Color.class,float.class).newInstance(color,1.0F);
            loc2.getWorld().spawnParticle(this.particles.get(1), loc2,1,0.3,0,0.3,dustOptions);
            locc1.getWorld().spawnParticle(this.particles.get(1),locc1,1,0.3,0,0.3,dustOptions);
            locc2.getWorld().spawnParticle(this.particles.get(1),locc2,1,0.3,0,0.3,dustOptions);
            locc3.getWorld().spawnParticle(this.particles.get(1),locc3,1,0.3,0,0.3,dustOptions);
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
}
