package zedly.tardis;

/*

 */
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class TardisPlugin extends JavaPlugin {
    
    private static TardisPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new Watcher(), this);
        FileIO.loadLocations();
        //Set the chance of an enderman dropping a ticket
        try {
            Storage.dropChance = Integer.parseInt("" + getConfig().getList("ticketDropChance").get(0));
        } catch (NumberFormatException e) {
            Storage.dropChance = 0;
        }
        //Define the radius that the Tardis can teleport
        try {
            Storage.r = Integer.parseInt("" + getConfig().getList("teleportRadius").get(0));
        } catch (NumberFormatException e) {
            Storage.r = 9000;
        }
        //See if Tardiss can teleport randomly
        boolean teleportRandom;
        try {
            teleportRandom = Boolean.parseBoolean("" + getConfig().getList("teleportRandom").get(0));
        } catch (NumberFormatException e) {
            teleportRandom = true;
        }
        //Set the frequency of Tardis teleportation
        int teleportFrequency;
        try {
            teleportFrequency = (Integer.parseInt("" + getConfig().getList("teleportFrequency").get(0)) * 1200);
        } catch (NumberFormatException e) {
            teleportFrequency = 6000;
        }
        if (teleportRandom) {
            getServer().getScheduler().scheduleSyncRepeatingTask(this, new RoamingTardis(), teleportFrequency, teleportFrequency);
        }
        //Set the center of the map
        try {
            String[] spawnCoords = ("" + getConfig().getList("spawnCoordinates").get(0)).split(",");
            Storage.x = Integer.parseInt(spawnCoords[0]);
            Storage.z = Integer.parseInt(spawnCoords[1]);
        } catch (NumberFormatException e) {
            Storage.x = 0;
            Storage.z = 0;
        }
        //Set what worlds enderman can drop tickets
        for (Object o : getConfig().getList("ticketDropWorlds")) {
            String s = (String) o;
            World world = Bukkit.getWorld(s);
            if (world != null) {
                Storage.ticketDropWorlds.add(world);
            }
        }
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandlabel, String[] args) {
        return CommandProcessor.onCommand(sender, command, commandlabel, args);
    }

    /**
     * @return the instance
     */
    public static TardisPlugin instance() {
        return instance;
    }
}
