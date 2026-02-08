package me.green.tpa.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class SafetyUtil {

    public static boolean isSafe(Location location) {
        Block feet = location.getBlock();
        Block head = location.clone().add(0, 1, 0).getBlock();
        Block ground = location.clone().add(0, -1, 0).getBlock();

        // Check if feet and head are in air or non-solid blocks
        if (!isPassable(feet.getType()) || !isPassable(head.getType())) {
            return false;
        }

        // Check if ground is solid and not dangerous
        if (ground.getType().isAir() || isDangerous(ground.getType())) {
            return false;
        }

        return true;
    }

    private static boolean isPassable(Material material) {
        return material.isAir() || material == Material.SHORT_GRASS || material == Material.TALL_GRASS ||
               material == Material.SNOW || material == Material.FERN || material == Material.LARGE_FERN;
    }

    private static boolean isDangerous(Material material) {
        return material == Material.LAVA || material == Material.MAGMA_BLOCK ||
               material == Material.FIRE || material == Material.CACTUS ||
               material == Material.VOID_AIR;
    }
}
