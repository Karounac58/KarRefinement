package vip.mcsj.www.version;

import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public interface CustomParticle {
    List<Particle> particles = new ArrayList<>();
    void addParticleFifteenth(Player p) throws InvocationTargetException, IllegalAccessException, ClassNotFoundException,NoSuchMethodException, InstantiationException ;

    List<Particle> getParticles();
}
