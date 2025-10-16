//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package vip.mcsj.www.karrefinement.effect.particle;

import org.bukkit.Location;

public class ParticleModel2D extends ParticleModel {
    public int width;
    public int height;
    public Object[][] data;

    public ParticleModel2D(Location loc, int width, int height) {
        this(loc, loc, new DrawHandle.V19PlusDrawHandle(), width, height);
    }

    public ParticleModel2D(Location loc, Location centerLoc, int width, int height) {
        this(loc, centerLoc, new DrawHandle.V19PlusDrawHandle(), width, height);
    }

    public ParticleModel2D(Location loc, Location centerLoc, DrawHandle handle, int width, int height) {
        super(loc, centerLoc, handle);
        this.width = width;
        this.height = height;
        this.data = new Object[height][width];
    }

    public void fill(Object obj) {
        for(int i = 0; i < this.height; ++i) {
            for(int j = 0; j < this.width; ++j) {
                this.data[i][j] = obj;
            }
        }

    }

    public void fillColumn(Object obj, int val) {
        for(int i = 0; i < this.height; ++i) {
            this.data[i][val] = obj;
        }

    }

    public void fillLine(Object obj, int val) {
        for(int i = 0; i < this.width; ++i) {
            this.data[val][i] = obj;
        }

    }

    public void draw() {
        for(int i = this.height - 1; i >= 0; --i) {
            for(int j = 0; j < this.width; ++j) {
                Location loc2D = this.loc.clone().add((double)j * this.particleWidth, (double)i * this.particleHeight, (double)0.0F);
                loc2D = ParticleUtil.rotateYawLocationWithPoint(loc2D, (double)this.rotateYaw, this.centerLoc);
                loc2D = ParticleUtil.rotateRollLocationWithPoint(loc2D, (double)this.rotateRoll, this.centerLoc);
                loc2D = ParticleUtil.rotatePitchLocationWithPoint(loc2D, (double)this.rotatePitch, this.centerLoc);
                this.handle.onDraw(loc2D, this.data[i][j]);
            }
        }

    }
}
