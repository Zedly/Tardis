package zedly.tardis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class FileIO {

    public static void loadLocations() {
        File dataFile = new File(TardisPlugin.instance().getDataFolder(), "data.yml");
        FileConfiguration fc = YamlConfiguration.loadConfiguration(dataFile);
        ArrayList<Location> tardisLocations = new ArrayList<>();
        fc.getList("locations", tardisLocations).forEach((o) -> {
            if (o instanceof Location) {
                Location loc = (Location) o;
                if (loc.getBlock().getState() instanceof Sign) {

                    Sign sign = (Sign) loc.getBlock().getState();
                    if (sign.getLine(0).equals(ChatColor.DARK_BLUE + "Tardis")) {
                        Storage.tardises.add(new Tardis(loc));
                        return;
                    } else if (sign.getLine(0).startsWith(ChatColor.GREEN + "")) {
                        sign.setLine(0, ChatColor.DARK_BLUE + "Tardis");
                        Storage.tardises.add(new Tardis(loc));
                        return;
                    }
                }
            }
            System.err.println("Tardis location invalid: " + o + ". Skipping");
        });
    }

    public static void saveLocations() {
        LinkedList<Location> tardisLocations = new LinkedList<>();
        for (Tardis tardis : Storage.tardises) {
            tardisLocations.add(tardis.signLocation);
        }
        YamlConfiguration yc = new YamlConfiguration();
        yc.set("locations", tardisLocations);
        try {
            File dataFile = new File(TardisPlugin.instance().getDataFolder(), "data.yml");
            yc.save(dataFile);
        } catch (IOException ex) {
            System.err.println("Error saving Tardis locations! Data was lost.");
        }
    }
}
