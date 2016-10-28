package zedly.fireworkeffects;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Bukkit-compatible fallback code if NMS detection fails
 * @author Dennis
 */
public class CustomEntityFirework_Unknown implements CustomEntityFirework {
    
    private final Location loc;
    private final FireworkEffect effect;
    private final JavaPlugin plugin;
    
    protected CustomEntityFirework_Unknown(JavaPlugin plugin, Location loc, FireworkEffect effect) {
        this.plugin = plugin;
        this.loc = loc;
        this.effect = effect;
    }
    
    @Override
    public boolean perform() {
        if(plugin == null) {
            return false;
        }
        final Firework f = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta d = (FireworkMeta) f.getFireworkMeta();
        d.setPower(1);
        d.addEffect(effect);
        f.setFireworkMeta(d);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                f.detonate();
            }
        }, 1);
        return true;
    }
}
