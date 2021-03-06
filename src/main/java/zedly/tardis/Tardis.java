package zedly.tardis;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import static org.bukkit.Material.*;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Orientable;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.Stairs.Shape;
import org.bukkit.entity.Entity;

public class Tardis {

    private static final int GLASS_PANE_COLOR = 11;

    private static final Material[] TARDIS_MATERIALS = new Material[]{QUARTZ_STAIRS, BLUE_STAINED_GLASS_PANE, BLUE_STAINED_GLASS_PANE, QUARTZ_STAIRS,
        AIR, QUARTZ_STAIRS, BLUE_STAINED_GLASS_PANE, BLUE_STAINED_GLASS_PANE, QUARTZ_STAIRS, AIR, QUARTZ_STAIRS,
        BLUE_STAINED_GLASS_PANE, BLUE_STAINED_GLASS_PANE, QUARTZ_STAIRS, AIR, QUARTZ_STAIRS, BLUE_STAINED_GLASS_PANE,
        BLUE_STAINED_GLASS_PANE, QUARTZ_STAIRS, AIR, QUARTZ_BLOCK, AIR, OAK_WALL_SIGN, GLOWSTONE, QUARTZ_SLAB, QUARTZ_STAIRS,
        AIR, AIR, QUARTZ_STAIRS, AIR, QUARTZ_STAIRS, BLUE_STAINED_GLASS_PANE,
        BLUE_STAINED_GLASS_PANE, QUARTZ_STAIRS, AIR, QUARTZ_STAIRS, BLUE_STAINED_GLASS_PANE, BLUE_STAINED_GLASS_PANE, QUARTZ_STAIRS,
        AIR, QUARTZ_STAIRS, BLUE_STAINED_GLASS_PANE, BLUE_STAINED_GLASS_PANE, QUARTZ_STAIRS, AIR};

    private static final Shape[][] STAIR_SHAPES = {{Shape.OUTER_LEFT, Shape.STRAIGHT, Shape.OUTER_LEFT}, {Shape.STRAIGHT, null, Shape.STRAIGHT}, {Shape.OUTER_LEFT, Shape.STRAIGHT, Shape.OUTER_RIGHT}};
    private static final BlockFace[][] STAIR_BLOCK_FACES = {{BlockFace.SOUTH, BlockFace.SOUTH, BlockFace.WEST}, {BlockFace.EAST, null, BlockFace.WEST}, {BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST}};

    public Location signLocation;
    public Boolean isTeleporting;

    public Tardis(Location signLocation) {
        this.signLocation = signLocation;
        isTeleporting = false;
    }

    public static Tardis spawn(Location location) {
        Tardis tardis = new Tardis(location);
        Storage.tardises.add(tardis);
        generate(location);
        return tardis;
    }

    public void startTeleport(Location location) {
        if (!isTeleporting) {
            isTeleporting = true;
            showSign(location);
            Bukkit.getScheduler().scheduleSyncDelayedTask(TardisPlugin.instance(), () -> {
                close();
            }, 100);
            Bukkit.getScheduler().scheduleSyncDelayedTask(TardisPlugin.instance(), () -> {
                move(location);
            }, 120);
        }
    }

    public void delete() {
        delete(signLocation);
    }

    private void delete(Location location) {
        location.getBlock().getRelative(0, 0, 0).setType(AIR);
        location.getBlock().getRelative(0, 2, 0).setType(AIR);
        for (int x = 1; x > -2; x--) {
            for (int z = 1; z > -2; z--) {
                for (int y = 1; y > -3; y--) {
                    location.getBlock().getRelative(x, y, z).setType(AIR);
                }
            }
        }
        Block maybeLadder = location.getBlock().getRelative(0, -2, 2);
        if (maybeLadder.getType() == Material.LADDER) {
            maybeLadder.setType(Material.AIR);
        }
    }

    public void move(final Location destination) {
        Location startEntLoc = signLocation.clone().add(0.5, -1, 0.5);
        Location destEntLoc = destination.clone().add(0.5, -0.9, 0.5);
        generate(destination);
        for (Entity e : startEntLoc.getWorld().getNearbyEntities(startEntLoc, 3, 3, 3)) {
            if (e.getLocation().distance(startEntLoc) < 1.2) {
                e.teleport(destEntLoc);
            }
        }
        generate(destination);
        delete();
        explode();
        isTeleporting = false;
        signLocation = destination.clone();
        FileIO.saveLocations();
    }

    private void close() {
        signLocation.getBlock().getRelative(0, 0, 1).setType(BLUE_STAINED_GLASS_PANE);
        signLocation.getBlock().getRelative(0, -1, 1).setType(BLUE_STAINED_GLASS_PANE);
    }

    private void showSign(final Location location) {
        if (this.signLocation.getBlock().getType().equals(OAK_WALL_SIGN)) {
            final Sign sign = (Sign) this.signLocation.getBlock().getState();
            for (int i = 0; i < 4; i++) {
                final int line = i;
                Bukkit.getScheduler().scheduleSyncDelayedTask(TardisPlugin.instance(), new Runnable() {
                    @Override
                    public void run() {
                        switch (line) {
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
                }, (i + 1) * 20);
            }
        }
    }

    private void explode() {
        FireworkEffect.Builder bl = FireworkEffect.builder();
        bl = bl.withColor(Color.fromRGB(0x330000)).withFade(Color.fromRGB(0x33FF00));
        bl = bl.with(FireworkEffect.Type.BALL_LARGE);
        for (int i = 0; i < 4; i++) {
            FireworkEffectPlayer.playFirework(signLocation.add(1, 0, 1), bl.build());
            FireworkEffectPlayer.playFirework(signLocation.add(1, 0, -1), bl.build());
            FireworkEffectPlayer.playFirework(signLocation.add(-1, 0, 1), bl.build());
            FireworkEffectPlayer.playFirework(signLocation.add(-1, 0, -1), bl.build());
        }
    }

    public static Location randomLocation(World world) {
        double th = Storage.rnd.nextDouble() * Math.PI * 2;
        double u = Storage.rnd.nextDouble() + Storage.rnd.nextDouble();
        if (u > 1) {
            u = 2 - u;
        }
        u *= Storage.r;
        int targetX = (int) (u * Math.sin(th));
        int targetZ = (int) (u * Math.cos(th));
        targetX += Storage.x;
        targetZ += Storage.z;
        Location testLoc = new Location(world, targetX, 1, targetZ);
        return safeLocation(testLoc);
    }

    public static Location randomLocation(World world, int attempts) {
        Location loc;
        int attemptCounter = 0;
        do {
            loc = Tardis.randomLocation(world);
        } while (loc == null && ++attemptCounter <= attempts);
        return loc;
    }

    public static boolean isLocationSafe(Location loc) {
        Block block = loc.getBlock();
        for (int y = 0; y < 5; y++) {
            if (block.getY() + y > 255 || !isSectionSafe(block.getWorld(), block.getX(), block.getY() + y, block.getZ())) {
                return false;
            }
        }
        return true;
    }

    public static Location safeLocation(Location location) {
        World world = location.getWorld();
        int testX = location.getBlockX();
        int testY = location.getBlockY();
        int testZ = location.getBlockZ();
        int freeSpaceHeight = 0;
        for (int y = testY; y < 255; y++) {
            if (isSectionSafe(world, testX, y, testZ)) {
                freeSpaceHeight++;
            } else {
                freeSpaceHeight = 0;
            }
            if (freeSpaceHeight >= 5) {
                return new Location(world, testX, y - 2, testZ);
            }
        }
        return null;
    }

    private static void generate(Location location) {

        BlockFace[][] STAIR_BLOCK_FACES = {
            {BlockFace.EAST, BlockFace.EAST, BlockFace.EAST},
            {BlockFace.SOUTH, null, BlockFace.NORTH},
            {BlockFace.WEST, BlockFace.WEST, BlockFace.WEST}};

        Shape[][] STAIR_SHAPES = {
            {Shape.OUTER_RIGHT, Shape.STRAIGHT, Shape.OUTER_LEFT},
            {Shape.STRAIGHT, null, Shape.STRAIGHT},
            {Shape.OUTER_LEFT, Shape.STRAIGHT, Shape.OUTER_RIGHT}};

        int counter = 0;
        for (int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {
                for (int y = -2; y < 3; y++) {
                    location.getBlock().getRelative(x, y, z).setType(TARDIS_MATERIALS[counter]);
                    if (null != TARDIS_MATERIALS[counter]) //location.getBlock().getRelative(x, y, z).setData(tardisBlockData[counter]);
                    {
                        switch (TARDIS_MATERIALS[counter]) {
                            case GLOWSTONE:
                                location.getBlock().getRelative(x, y, z).getState().update();
                                break;
                            case OAK_WALL_SIGN:
                                Sign sig = (Sign) location.getBlock().getRelative(x, y, z).getState();
                                Directional sd = (Directional) sig.getBlockData();
                                sd.setFacing(BlockFace.SOUTH);
                                sig.setLine(0, ChatColor.DARK_BLUE + "Tardis");
                                sig.setLine(1, ChatColor.BLACK + "x: " + ChatColor.MAGIC + "12345");
                                sig.setLine(2, ChatColor.BLACK + "y: " + ChatColor.MAGIC + "12345");
                                sig.setLine(3, ChatColor.BLACK + "z: " + ChatColor.MAGIC + "12345");
                                //location.getBlock().getRelative(x, y, z).setData((byte) 3);
                                sig.setBlockData(sd);
                                sig.update(true);
                                break;
                            case QUARTZ_STAIRS:
                                Block block = location.getBlock().getRelative(x, y, z);
                                Stairs stairs = (Stairs) block.getBlockData();
                                stairs.setFacing(STAIR_BLOCK_FACES[x + 1][z + 1]); //STAIR_BLOCK_FACES[x + 1][z + 1]
                                stairs.setShape(STAIR_SHAPES[x + 1][z + 1]);
                                block.setBlockData(stairs, true);
                                break;
                            default:
                                break;
                        }
                    }
                    counter++;
                }
            }
        }

        Block maybeLadder = location.getBlock().getRelative(0, -2, 2);
        if (maybeLadder.getType() == Material.AIR) {
            maybeLadder.setType(Material.LADDER);
            Directional ld = (Directional) maybeLadder.getBlockData();
            ld.setFacing(BlockFace.SOUTH);
            maybeLadder.setBlockData(ld);
        }

    }

    private static boolean isSectionSafe(World world, int testX, int testY, int testZ) {
        for (int x = testX - 1; x < testX + 2; x++) {
            for (int z = testZ - 1; z < testZ + 2; z++) {
                if (world.getBlockAt(x, testY, z).getType() != Material.AIR) {
                    return false;
                }
            }
        }
        return true;
    }
}
