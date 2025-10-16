//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package vip.mcsj.www.karrefinement.effect.particle;

import org.bukkit.Location;

public abstract class ParticleModel {
    public Location loc;
    public Location centerLoc;
    public DrawHandle handle;
    public int rotateYaw = 0;
    public int rotatePitch = 0;
    public int rotateRoll = 0;
    public double particleWidth = 0.2;
    public double particleHeight = 0.2;

    public ParticleModel(Location loc, Location centerLoc, DrawHandle handle) {
        this.loc = loc;
        this.centerLoc = centerLoc;
        this.handle = handle;
    }

    public void fill(Object obj) {
    }

    public void draw() {
    }
}
