package zedly.fireworkeffects;

import net.minecraft.server.v1_10_R1.EntityFireworks;
import net.minecraft.server.v1_10_R1.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_10_R1.World;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

//CustomEntityFirework class by recon88: https://github.com/recon88/Instant-Fireworks/blob/master/src/CustomEntityFirework.java
public class CustomEntityFirework_1_10_R1 extends EntityFireworks implements CustomEntityFirework {

    Player[] players = new Player[]{};
    Location location;
    FireworkEffect effect;

    protected CustomEntityFirework_1_10_R1(World world, Player... p) {
        super(world);
        players = p;
        this.a(0.25F, 0.25F);
    }

    protected CustomEntityFirework_1_10_R1(World world) {
        super(world);
        Bukkit.getOnlinePlayers().toArray(players);
        this.a(0.25F, 0.25F);
    }

    protected CustomEntityFirework_1_10_R1(Location loc, FireworkEffect effect) {
        super(((CraftWorld) loc.getWorld()).getHandle());
        Bukkit.getOnlinePlayers().toArray(players);
        this.a(0.25F, 0.25F);
        this.location = loc;
        this.effect = effect;
    }

    boolean gone = false;

    @Override
    public boolean perform() {
        try {
            FireworkMeta meta = ((Firework) getBukkitEntity()).getFireworkMeta();
            meta.addEffect(effect);
            ((Firework) getBukkitEntity()).setFireworkMeta(meta);
            setPosition(location.getX(), location.getY(), location.getZ());
            if ((((CraftWorld) location.getWorld()).getHandle()).addEntity(this)) {
                setInvisible(true);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void m() {
        if (gone) {
            return;
        }
        gone = true;
        if (players != null) {
            if (players.length > 0) {
                for (Player player : players) {
                    (((CraftPlayer) player).getHandle()).playerConnection.sendPacket(new PacketPlayOutEntityStatus(this, (byte) 17));
                }
            } else {
                world.broadcastEntityEffect(this, (byte) 17);
            }
        }
        this.die();
    }
}
