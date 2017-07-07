package zedly.tardis;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Watcher implements Listener {

    //Tardises
    @EventHandler(priority = EventPriority.HIGH)
    public boolean onTicketOrSign(PlayerInteractEvent evt) throws FileNotFoundException, IOException {
        final Player player = evt.getPlayer();
        Block block = evt.getClickedBlock();
        if (evt.getAction().equals(RIGHT_CLICK_BLOCK)) {
            if (block.getType() == Material.WALL_SIGN) {
                for (Tardis tardis : Storage.tardises) {
                    if (tardis.signLocation.equals(block.getLocation())) {
                        World world = tardis.signLocation.getWorld();
                        Location loc = Tardis.randomLocation(world, 10);
                        if (loc != null) {
                            tardis.startTeleport(loc);
                        } else {
                            player.sendMessage(Storage.logo + "Cannot find a safe destination! Check the config.yml");
                        }
                        return true;
                    }
                }
            }
        }
        ItemStack is = player.getInventory().getItemInMainHand();
        if (is != null && is.getType() == Material.PAPER && is.hasItemMeta() && is.getItemMeta().hasDisplayName()
                && (is.getItemMeta().getDisplayName().equals(ChatColor.LIGHT_PURPLE + "Apparator Ticket")
                || is.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Tardis Key"))) {
            if (evt.getAction() == Action.RIGHT_CLICK_BLOCK) {
                player.sendMessage(ChatColor.DARK_PURPLE + "Click into the air to summon a Tardis!");
                return true;
            } else if (evt.getAction() == Action.RIGHT_CLICK_AIR) {

                if (!Tardis.isLocationSafe(player.getLocation())) {
                    player.sendMessage(ChatColor.DARK_PURPLE + "Make sure there is at least 3x3x5 of air around you!");
                    return true;
                }

                Location targetLoc = player.getLocation().getBlock().getRelative(0, 2, 0).getLocation();

                for (Tardis tardis : Storage.tardises) {
                    if (!tardis.isTeleporting && tardis.signLocation.getWorld().equals(player.getWorld())) {
                        tardis.startTeleport(targetLoc);
                        if (is.getAmount() <= 1) {
                            is = null;
                        } else {
                            is.setAmount(is.getAmount() - 1);
                        }
                        player.getInventory().setItemInMainHand(is);
                        player.updateInventory();
                        for (int i = 0; i < 90; i += 5) {
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(TardisPlugin.instance(), () -> {
                                player.getWorld().spigot().playEffect(targetLoc, Effect.PORTAL, 0, 1, 3f, 3f, 3f, 2f, 400, 32);
                            }, i);
                        }
                        break;
                    }
                }
                return true;
            }
        }
        return true;
    }

    @EventHandler
    public boolean onAttemptBreak(BlockBreakEvent evt) {
        Location l = evt.getBlock().getLocation();
        for (Tardis app : Storage.tardises) {
            Location loc = app.signLocation;
            int x = l.getBlockX() - loc.getBlockX();
            int y = l.getBlockY() - loc.getBlockY();
            int z = l.getBlockZ() - loc.getBlockZ();
            if (x > -2 && x < 2 && y > -3 && y < 3 && z > -2 && z < 2 && l.getWorld().getUID().equals(loc.getWorld().getUID())) {
                evt.setCancelled(true);
                return true;
            }
        }
        return true;
    }

    @EventHandler
    public boolean onAttemptPlace(BlockPlaceEvent evt) {
        Location l = evt.getBlock().getLocation();
        for (Tardis app : Storage.tardises) {
            Location loc = app.signLocation;
            int x = l.getBlockX() - loc.getBlockX();
            int y = l.getBlockY() - loc.getBlockY();
            int z = l.getBlockZ() - loc.getBlockZ();
            if (x > -2 && x < 2 && y > -3 && y < 3 && z > -2 && z < 2 && l.getWorld().getUID().equals(loc.getWorld().getUID())) {
                evt.setCancelled(true);
                return true;
            }
        }
        return true;
    }

    @EventHandler
    public boolean onTicketGen(EntityDeathEvent evt) {
        if (evt.getEntity().getKiller() instanceof Player) {
            if (evt.getEntity().getType() == EntityType.ENDERMAN && Storage.ticketDropWorlds.contains(evt.getEntity().getWorld())) {
                if (Storage.rnd.nextInt(100) + 1 <= Storage.dropChance) {
                    ItemStack is = new ItemStack(Material.PAPER);
                    ItemMeta meta = is.getItemMeta();
                    meta.setDisplayName(ChatColor.AQUA + "Tardis Key");
                    is.setItemMeta(meta);
                    evt.getEntity().getWorld().dropItem(evt.getEntity().getLocation(), is);
                }
            }
        }
        return true;
    }
}
