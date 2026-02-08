package me.green.tpa.manager;

import me.green.tpa.GreenTPA;
import me.green.tpa.models.Spawn;
import me.green.tpa.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpawnManager {

    private final GreenTPA plugin;
    private final File file;
    private FileConfiguration config;
    private final Map<String, Spawn> worldSpawns = new ConcurrentHashMap<>();

    public SpawnManager(GreenTPA plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "spawns.yml");
    }

    public void load() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        worldSpawns.clear();

        for (String worldName : config.getKeys(false)) {
            Location loc = LocationUtil.deserialize(config.getString(worldName));
            if (loc != null) {
                worldSpawns.put(worldName, new Spawn(worldName, loc));
            }
        }
    }

    public void setSpawn(String worldName, Location location) {
        worldSpawns.put(worldName, new Spawn(worldName, location));
        config.set(worldName, LocationUtil.serialize(location));
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteSpawn(String worldName) {
        worldSpawns.remove(worldName);
        config.set(worldName, null);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Spawn getSpawn(String worldName) {
        return worldSpawns.get(worldName);
    }
}
