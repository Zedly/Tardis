package zedly.tardis;

import org.bukkit.Location;

public class Utilities {

    public static Location getCenter(Location loc) {
        double x = loc.getX();
        double z = loc.getZ();
        if (x >= 0) {
            x += .5;
        } else {
            x += .5;
        }
        if (z >= 0) {
            z += .5;
        } else {
            z += .5;
        }
        Location lo = loc.clone();
        lo.setX(x);
        lo.setY(lo.getY() + .5);
        lo.setZ(z);
        return lo;
    }
}
