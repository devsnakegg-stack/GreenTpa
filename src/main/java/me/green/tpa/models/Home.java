package me.green.tpa.models;

import org.bukkit.Location;
import java.util.UUID;

public class Home {
    private final UUID owner;
    private final String name;
    private final Location location;

    public Home(UUID owner, String name, Location location) {
        this.owner = owner;
        this.name = name;
        this.location = location;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }
}
