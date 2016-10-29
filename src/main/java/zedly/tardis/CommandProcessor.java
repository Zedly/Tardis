package zedly.tardis;

import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandProcessor {

    public static boolean onCommand(CommandSender sender, Command command, String commandlabel, String[] args) {
        switch (args.length) {
            case 1:
                singleArgCommands(sender, command, commandlabel, args);
                break;
            case 2:
                twoArgCommands(sender, command, commandlabel, args);
                break;
            default:
                noArgsCommand(sender, command, commandlabel, args);
                break;
        }
        return true;
    }

    private static boolean noArgsCommand(CommandSender sender, Command command, String commandlabel, String[] args) {
        listTardises(sender);
        return true;
    }

    private static boolean singleArgCommands(CommandSender sender, Command command, String commandlabel, String[] args) {
        if (args[0].matches("^\\d+$")) {
            int tardisId = Integer.parseInt(args[0]);
            if (tardisId < 1 || tardisId > Storage.tardises.size()) {
                sender.sendMessage(Storage.logo + " That's not a real Tardis id!");
                return true;
            }
            Location l = Storage.tardises.get(tardisId - 1).signLocation.clone();
            if (sender instanceof Player) {
                Player p = (Player) sender;
                l.setX(l.getX() + .5);
                l.setZ(l.getZ() + .5);
                l.setY(l.getY() - 1);
                p.teleport(l);
            } else {
                sender.sendMessage(Storage.logo + " Tardis " + tardisId + " is in " + l.getWorld().getName() + " at " + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ());
            }
        } else {
            switch (args[0]) {
                case "spawn":
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Storage.logo + " This can only be done ingame!");
                        return true;
                    }
                    if (!sender.hasPermission("tardis.spawn")) {
                        sender.sendMessage(Storage.logo + " You don't have permission to do this.");
                        return true;
                    }
                    Block b = ((Player) sender).getTargetBlock((Set<Material>) null, 40);
                    if (b == null) {
                        sender.sendMessage(Storage.logo + " Please look at the spot where you want to spawn a Tardis");
                        return true;
                    }
                    Location spawnLoc = Tardis.safeLocation(b.getLocation());
                    if (spawnLoc != null) {
                        Tardis.spawn(spawnLoc);
                        sender.sendMessage(Storage.logo + " A new Tardis has been spawned");
                    } else {
                        sender.sendMessage(Storage.logo + " A tardis cannot be spawned at that location");
                    }
                    break;
                case "key":
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Storage.logo + " This can only be done ingame!");
                        return true;
                    }
                    if (!sender.hasPermission("tardis.key")) {
                        sender.sendMessage(Storage.logo + " You don't have permission to do this.");
                        return true;
                    }
                    ItemStack is = new ItemStack(Material.PAPER, 1);
                    ItemMeta meta = is.getItemMeta();
                    meta.setDisplayName(ChatColor.AQUA + "Tardis Key");
                    is.setItemMeta(meta);
                    Player p = (Player) sender;
                    p.getWorld().dropItem(p.getLocation(), is);
                    p.sendMessage(Storage.logo + "Created a Tardis key");
                    break;
                case "help":
                    sender.sendMessage(Storage.logo + "\n- /tardis: Lists Tardises.\n- /tardis <ID>: Teleports you to this Tardis."
                            + "\n- /tardis spawn: Spawns a Tardis.\n- /tardis del <ID>: Deletes a Tardis.");
                    break;
            }
        }
        return true;
    }

    private static boolean twoArgCommands(CommandSender sender, Command command, String commandlabel, String[] args) {
        int tardisId = 0;
        if (args[1].matches("^\\d+$")) {
            try {
                tardisId = Integer.parseInt(args[1]);
                if (tardisId < 1 || tardisId > Storage.tardises.size()) {
                    sender.sendMessage(Storage.logo + " That's not a real Tardis id!");
                    return true;
                }
            } catch (NumberFormatException ex) {
                sender.sendMessage(Storage.logo + " That's not a real Tardis id!");
                return true;
            }
        }
        switch (args[0].toLowerCase()) {
            case "del":
                if (!sender.hasPermission("tardis.delete")) {
                    sender.sendMessage(Storage.logo + " You don't have permission to do this.");
                    return true;
                }
                Storage.tardises.get(tardisId - 1).delete();
                Storage.tardises.remove(tardisId - 1);
                sender.sendMessage(Storage.logo + " Tardis " + tardisId + " has been deleted.");
                break;
            case "summon":
                if (!sender.hasPermission("tardis.summon")) {
                    sender.sendMessage(Storage.logo + " You don't have permission to do this.");
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Storage.logo + " This only works ingame!");
                    return true;
                }
                Player player = (Player) sender;
                if (!Tardis.isLocationSafe(player.getLocation())) {
                    player.sendMessage(ChatColor.DARK_PURPLE + "Make sure there is at least 3x3x5 of air around you!");
                    return true;
                }
                
                Tardis tardis = Storage.tardises.get(tardisId - 1);
                Location targetLoc = player.getLocation().getBlock().getRelative(0, 2, 0).getLocation();
                Location destEntLoc = targetLoc.clone().add(0.5, -0.9, 0.5);
                tardis.move(targetLoc);
                player.teleport(destEntLoc);
                player.sendMessage(Storage.logo + " Tardis " + tardisId + " has been summoned.");
                break;
        }
        return true;
    }

    private static void listTardises(CommandSender sender) {
        sender.sendMessage(Storage.logo + " There's " + Storage.tardises.size() + " active Tardis(es):");
        int i = 0;
        for (Tardis app : Storage.tardises) {
            Location l = app.signLocation.clone();
            sender.sendMessage(ChatColor.LIGHT_PURPLE + " - " + ++i + ": " + l.getWorld().getName() + " at " + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ());
        }
        if (sender instanceof Player) {
            sender.sendMessage(Storage.logo + " Use /tardis <number> to teleport to a Tardis!");
        }
    }
}
