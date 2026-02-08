package me.green.tpa.manager;

import me.green.tpa.GreenTPA;
import me.green.tpa.models.Home;
import me.green.tpa.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HomeManager {

    private final GreenTPA plugin;
    private final File file;
    private FileConfiguration config;
    private final Map<UUID, Map<String, Home>> playerHomes = new ConcurrentHashMap<>();

    public HomeManager(GreenTPA plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "homes.yml");
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
        playerHomes.clear();

        for (String uuidString : config.getKeys(false)) {
            UUID uuid = UUID.fromString(uuidString);
            ConfigurationSection section = config.getConfigurationSection(uuidString);
            if (section != null) {
                Map<String, Home> homes = new HashMap<>();
                for (String name : section.getKeys(false)) {
                    Location loc = LocationUtil.deserialize(section.getString(name));
                    if (loc != null) {
                        homes.put(name.toLowerCase(), new Home(uuid, name, loc));
                    }
                }
                playerHomes.put(uuid, homes);
            }
        }
    }

    public void save() {
        for (Map.Entry<UUID, Map<String, Home>> entry : playerHomes.entrySet()) {
            String uuid = entry.getKey().toString();
            for (Map.Entry<String, Home> homeEntry : entry.getValue().entrySet()) {
                config.set(uuid + "." + homeEntry.getKey(), LocationUtil.serialize(homeEntry.getValue().getLocation()));
            }
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setHome(UUID uuid, String name, Location location) {
        playerHomes.computeIfAbsent(uuid, k -> new HashMap<>()).put(name.toLowerCase(), new Home(uuid, name, location));
        config.set(uuid.toString() + "." + name.toLowerCase(), LocationUtil.serialize(location));
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteHome(UUID uuid, String name) {
        if (playerHomes.containsKey(uuid)) {
            playerHomes.get(uuid).remove(name.toLowerCase());
            config.set(uuid.toString() + "." + name.toLowerCase(), null);
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Home getHome(UUID uuid, String name) {
        if (playerHomes.containsKey(uuid)) {
            return playerHomes.get(uuid).get(name.toLowerCase());
        }
        return null;
    }

    public Map<String, Home> getHomes(UUID uuid) {
        return playerHomes.getOrDefault(uuid, Collections.emptyMap());
    }

    public int getHomeCount(UUID uuid) {
        return getHomes(uuid).size();
    }
}
