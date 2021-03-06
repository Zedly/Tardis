package zedly.tardis;

import org.bukkit.Location;
import org.bukkit.Material;

public class CloseTardis implements Runnable {

    Location loc;

    public CloseTardis(Location loc) {
        this.loc = loc;
    }

    @Override
    public void run() {
        loc.getBlock().getRelative(0, 0, 1).setType(Material.BLUE_STAINED_GLASS_PANE);
        loc.getBlock().getRelative(0, -1, 1).setType(Material.BLUE_STAINED_GLASS_PANE);
    }
}
