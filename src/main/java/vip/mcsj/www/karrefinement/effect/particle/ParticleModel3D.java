//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package vip.mcsj.www.karrefinement.effect.particle;

import org.bukkit.Location;

public class ParticleModel3D extends ParticleModel {
    public ParticleModel2D[] models;

    public ParticleModel3D(Location loc) {
        super(loc, loc, new DrawHandle.V19PlusDrawHandle());
    }

    public void fill(Object obj) {
        for(ParticleModel2D model : this.models) {
            model.fill(obj);
        }

    }

    public void draw() {
        for(ParticleModel2D model : this.models) {
            model.draw();
        }

    }
}
