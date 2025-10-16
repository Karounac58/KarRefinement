//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package vip.mcsj.www.karrefinement.effect.particle;

import org.bukkit.Location;
import org.bukkit.Particle;

public class DrawHandle {
    public void onDraw(Location loc, Object data) {
    }

    public static class V19PlusDrawHandle extends DrawHandle {
        public void onDraw(Location loc, Object data) {
            if (data != null && data instanceof Particle) {
                loc.getWorld().spawnParticle((Particle)data, loc, 0);
            }

        }
    }
}
