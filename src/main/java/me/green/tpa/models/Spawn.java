package me.green.tpa.models;

import org.bukkit.Location;

public class Spawn {
    private final String worldName;
    private final Location location;

    public Spawn(String worldName, Location location) {
        this.worldName = worldName;
        this.location = location;
    }

    public String getWorldName() {
        return worldName;
    }

    public Location getLocation() {
        return location;
    }
}
