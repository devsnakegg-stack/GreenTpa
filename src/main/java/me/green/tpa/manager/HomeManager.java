package me.green.tpa.manager;

import me.green.tpa.GreenTPA;
import me.green.tpa.models.Home;
import org.bukkit.Location;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HomeManager {

    private final GreenTPA plugin;
    private final Map<UUID, Map<String, Home>> playerHomes = new ConcurrentHashMap<>();

    public HomeManager(GreenTPA plugin) {
        this.plugin = plugin;
    }

    public void setHome(UUID uuid, String name, Location location) {
        playerHomes.computeIfAbsent(uuid, k -> new HashMap<>()).put(name.toLowerCase(), new Home(uuid, name, location));
    }

    public void deleteHome(UUID uuid, String name) {
        if (playerHomes.containsKey(uuid)) {
            playerHomes.get(uuid).remove(name.toLowerCase());
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

    public Map<UUID, Map<String, Home>> getAllHomes() {
        return playerHomes;
    }
}
