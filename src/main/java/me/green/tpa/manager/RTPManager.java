package me.green.tpa.manager;

import me.green.tpa.GreenTPA;
import me.green.tpa.utils.SafetyUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;

import java.util.List;
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
            String path = "rtp.regions." + world.getName();

            List<Integer> xRange = plugin.getConfig().getIntegerList(path + ".x");
            List<Integer> zRange = plugin.getConfig().getIntegerList(path + ".z");

            if (xRange.size() < 2 || zRange.size() < 2) {
                // Fallback to default world if current world not configured
                xRange = plugin.getConfig().getIntegerList("rtp.regions.world.x");
                zRange = plugin.getConfig().getIntegerList("rtp.regions.world.z");

                if (xRange.size() < 2 || zRange.size() < 2) {
                    return null; // Totally unconfigured
                }
            }

            int minX = xRange.get(0);
            int maxX = xRange.get(1);
            int minZ = zRange.get(0);
            int maxZ = zRange.get(1);
            int minY = plugin.getConfig().getInt(path + ".y.min", 60);
            int maxY = plugin.getConfig().getInt(path + ".y.max", 320);

            int maxAttempts = plugin.getConfig().getInt("rtp.attempts", 25);
            List<String> avoidBlocks = plugin.getConfig().getStringList("rtp.avoid-blocks");
            List<String> avoidBiomes = plugin.getConfig().getStringList("rtp.avoid-biomes");

            int attempts = 0;
            while (attempts < maxAttempts) {
                int x = random.nextInt(maxX - minX + 1) + minX;
                int z = random.nextInt(maxZ - minZ + 1) + minZ;

                Location safeLoc = null;
                try {
                    safeLoc = Bukkit.getScheduler().callSyncMethod(plugin, () -> {
                        int y = world.getHighestBlockYAt(x, z);
                        if (y < minY || y > maxY) return null;

                        Location l = new Location(world, x + 0.5, y + 1, z + 0.5);

                        // Check biome
                        Biome biome = l.getBlock().getBiome();
                        if (avoidBiomes.contains(biome.name())) return null;

                        // Check block
                        Material mat = l.clone().add(0, -1, 0).getBlock().getType();
                        if (avoidBlocks.contains(mat.name())) return null;

                        if (SafetyUtil.isSafe(l)) return l;
                        return null;
                    }).get();
                } catch (Exception e) {
                    // Silently ignore sync task failures during search
                }

                if (safeLoc != null) return safeLoc;
                attempts++;
            }
            return null;
        });
    }
}
