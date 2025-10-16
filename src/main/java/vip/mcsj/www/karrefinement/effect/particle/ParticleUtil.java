//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package vip.mcsj.www.karrefinement.effect.particle;

import org.bukkit.Location;

public class ParticleUtil {
    public static Location rotatePitchLocationWithPoint(Location location, double angle, Location point) {
        double radians = Math.toRadians(angle);
        double dx = location.getX() - point.getX();
        double dz = location.getZ() - point.getZ();
        double newX = dx * Math.cos(radians) - dz * Math.sin(radians) + point.getX();
        double newZ = dz * Math.cos(radians) + dx * Math.sin(radians) + point.getZ();
        return new Location(location.getWorld(), newX, location.getY(), newZ);
    }

    public static Location rotateYawLocationWithPoint(Location location, double angle, Location point) {
        double radians = Math.toRadians(angle);
        double dx = location.getX() - point.getX();
        double dy = location.getY() - point.getY();
        double newX = dx * Math.cos(radians) - dy * Math.sin(radians) + point.getX();
        double newY = dy * Math.cos(radians) + dx * Math.sin(radians) + point.getY();
        return new Location(location.getWorld(), newX, newY, location.getZ());
    }

    public static Location rotateRollLocationWithPoint(Location location, double angle, Location point) {
        double radians = Math.toRadians(angle);
        double dz = location.getZ() - point.getZ();
        double dy = location.getY() - point.getY();
        double newZ = dz * Math.cos(radians) - dy * Math.sin(radians) + point.getZ();
        double newY = dy * Math.cos(radians) + dz * Math.sin(radians) + point.getY();
        return new Location(location.getWorld(), location.getX(), newY, newZ);
    }
}
