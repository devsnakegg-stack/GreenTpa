package me.green.tpa.manager;

import me.green.tpa.GreenTPA;
import me.green.tpa.models.Spawn;
import org.bukkit.Location;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpawnManager {

    private final GreenTPA plugin;
    private final Map<String, Spawn> worldSpawns = new ConcurrentHashMap<>();

    public SpawnManager(GreenTPA plugin) {
        this.plugin = plugin;
    }

    public void setSpawn(String worldName, Location location) {
        worldSpawns.put(worldName, new Spawn(worldName, location));
    }

    public void deleteSpawn(String worldName) {
        worldSpawns.remove(worldName);
    }

    public Spawn getSpawn(String worldName) {
        return worldSpawns.get(worldName);
    }

    public Map<String, Spawn> getAllSpawns() {
        return worldSpawns;
    }
}
