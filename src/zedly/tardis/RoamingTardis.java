package zedly.tardis;

import org.bukkit.Location;
import org.bukkit.World;

public class RoamingTardis implements Runnable {

    private int nextTardisId = 0;

    @Override
    public void run() {
        if (!Storage.tardises.isEmpty()) {
            nextTardisId = (nextTardisId + 1) % Storage.tardises.size();
            Tardis tardis = Storage.tardises.get(nextTardisId);
            World world = tardis.signLocation.getWorld();
            Location loc = Tardis.randomLocation(world, 10);
            if (loc != null) {
                tardis.startTeleport(loc);
            } else {
                System.err.println("Cannot find a safe destination! Check the config.yml");
            }
        }
    }
}
