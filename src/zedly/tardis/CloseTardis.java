package zedly.tardis;

import org.bukkit.Location;
import static org.bukkit.Material.STAINED_GLASS_PANE;

public class CloseTardis implements Runnable {

    Location loc;

    public CloseTardis(Location loc) {
        this.loc = loc;
    }

    @Override
    public void run() {
        loc.getBlock().getRelative(0, 0, 1).setType(STAINED_GLASS_PANE);
        loc.getBlock().getRelative(0, 0, 1).setData((byte) 15);
        loc.getBlock().getRelative(0, -1, 1).setType(STAINED_GLASS_PANE);
        loc.getBlock().getRelative(0, -1, 1).setData((byte) 15);
    }
}
