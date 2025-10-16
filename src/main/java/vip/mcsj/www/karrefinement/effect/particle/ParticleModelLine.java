//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package vip.mcsj.www.karrefinement.effect.particle;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class ParticleModelLine extends ParticleModel {
    public int length;
    public Object[] data;

    public ParticleModelLine(Location loc, int length) {
        this(loc, loc, new DrawHandle.V19PlusDrawHandle(), length);
    }

    public ParticleModelLine(Location loc, Location centerLoc, int length) {
        this(loc, centerLoc, new DrawHandle.V19PlusDrawHandle(), length);
    }

    public ParticleModelLine(Location loc, Location centerLoc, DrawHandle handle, int length) {
        super(loc, centerLoc, handle);
        this.data = new Object[length];
    }

    public void fill(Object obj) {
        for(int i = 0; i < this.length; ++i) {
            this.data[i] = obj;
        }

    }

    public void draw() {
        for(int i = 0; i < this.length; ++i) {
            Location loc2D = this.loc.clone().add((double)i * this.particleWidth, (double)0.0F, (double)0.0F);
            Location rotatePitchLoc = ParticleUtil.rotatePitchLocationWithPoint(loc2D, (double)this.rotatePitch, this.centerLoc);
            Location rotateYawLoc = ParticleUtil.rotateYawLocationWithPoint(rotatePitchLoc, (double)this.rotateYaw, this.centerLoc);
            Location rotateRollLoc = ParticleUtil.rotateRollLocationWithPoint(rotateYawLoc, (double)this.rotateRoll, this.centerLoc);
            this.handle.onDraw(rotateRollLoc, this.data[i]);
        }

    }

    public static class BetweenPoints extends ParticleModelLine {
        public Object realData;
        public Location beginLoc;
        public Location endLoc;

        public BetweenPoints(Location beginLoc, Location endLoc, Object realData) {
            super(beginLoc, 0);
            this.realData = realData;
            this.beginLoc = beginLoc;
            this.endLoc = endLoc;
        }

        public void draw() {
            Vector vectorAB = this.endLoc.clone().subtract(this.beginLoc).toVector();
            double vectorLength = vectorAB.length();
            vectorAB.normalize();

            for(double i = (double)0.0F; i < vectorLength; i += this.particleWidth) {
                Vector vector = vectorAB.clone().multiply(i);
                this.beginLoc.add(vector);
                this.handle.onDraw(this.beginLoc, this.realData);
                this.beginLoc.subtract(vector);
            }

        }
    }
}
