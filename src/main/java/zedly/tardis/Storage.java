package zedly.tardis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.World;
import zedly.fireworkeffects.FireworkEffectPlayer;

public class Storage {
    public static ArrayList<Tardis> tardises = new ArrayList<>();
    public static FireworkEffectPlayer fep;
    public static final Random rnd = new Random();
    public static final HashSet<World> ticketDropWorlds = new HashSet<>();
    public static int dropChance;
    public static int x;
    public static int z;
    public static int r;
    public static final String logo = ChatColor.RED + "[" + ChatColor.DARK_PURPLE + "Tardis" + ChatColor.RED + "]" + ChatColor.LIGHT_PURPLE + "";
}
