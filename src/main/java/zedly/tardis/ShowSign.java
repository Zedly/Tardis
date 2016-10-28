package zedly.tardis;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;

public class ShowSign implements Runnable {

    int index;
    Sign sign;
    Location location;

    public ShowSign(Sign sign, int index, Location location) {
        this.sign = sign;
        this.index = index;
        this.location = location;
    }

    @Override
    public void run() {
        switch (index) {
            case 0:
                sign.setLine(0, ChatColor.GREEN + "" + location.getBlock().getBiome().toString());
                break;
            case 1:
                sign.setLine(1, ChatColor.BLACK + "x: " + location.getBlockX());
                break;
            case 2:
                sign.setLine(2, ChatColor.BLACK + "y: " + location.getBlockY());
                break;
            case 3:
                sign.setLine(3, ChatColor.BLACK + "z: " + location.getBlockZ());
                break;
        }
        sign.update(true);
    }
}
