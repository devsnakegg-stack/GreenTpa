package me.green.tpa.manager;

import me.green.tpa.GreenTPA;
import me.green.tpa.utils.SafetyUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class RTPManager {

    private final GreenTPA plugin;
    private final Random random = new Random();

    public RTPManager(GreenTPA plugin) {
        this.plugin = plugin;
    }

    public CompletableFuture<Location> findSafeLocation(World world) {
        return CompletableFuture.supplyAsync(() -> {
            int minX = plugin.getConfig().getInt("rtp.regions." + world.getName() + ".min-x", -5000);
            int maxX = plugin.getConfig().getInt("rtp.regions." + world.getName() + ".max-x", 5000);
            int minZ = plugin.getConfig().getInt("rtp.regions." + world.getName() + ".min-z", -5000);
            int maxZ = plugin.getConfig().getInt("rtp.regions." + world.getName() + ".max-z", 5000);

            int attempts = 0;
            while (attempts < 50) {
                int x = random.nextInt(maxX - minX + 1) + minX;
                int z = random.nextInt(maxZ - minZ + 1) + minZ;

                // Need to be on main thread to get highest block or just use world.getHighestBlockAt
                // Actually paper allows world.getHighestBlockAt async in some versions, but better safe.

                Location loc = new Location(world, x, 0, z);

                Location safeLoc = null;
                try {
                    safeLoc = Bukkit.getScheduler().callSyncMethod(plugin, () -> {
                        int y = world.getHighestBlockYAt(x, z);
                        Location l = new Location(world, x + 0.5, y + 1, z + 0.5);
                        if (SafetyUtil.isSafe(l)) return l;
                        return null;
                    }).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (safeLoc != null) return safeLoc;
                attempts++;
            }
            return null;
        });
    }
}
