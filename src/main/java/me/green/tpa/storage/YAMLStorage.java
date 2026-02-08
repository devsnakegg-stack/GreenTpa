package me.green.tpa.storage;

import me.green.tpa.GreenTPA;
import me.green.tpa.models.Home;
import me.green.tpa.models.Spawn;
import me.green.tpa.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class YAMLStorage implements DataStorage {

    private final GreenTPA plugin;
    private final File dataFile;
    private final File homesFile;
    private final File spawnsFile;

    public YAMLStorage(GreenTPA plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data.yml");
        this.homesFile = new File(plugin.getDataFolder(), "homes.yml");
        this.spawnsFile = new File(plugin.getDataFolder(), "spawns.yml");
    }

    @Override
    public void init() {
        try {
            if (!dataFile.exists()) dataFile.createNewFile();
            if (!homesFile.exists()) homesFile.createNewFile();
            if (!spawnsFile.exists()) spawnsFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {}

    @Override
    public void saveToggles(Set<UUID> disabled, Set<UUID> ignoreAll, Map<UUID, Set<UUID>> blocked, Map<UUID, Set<UUID>> ignored, Map<UUID, Boolean> autoAccept) {
        FileConfiguration config = new YamlConfiguration();
        config.set("tpa-disabled", disabled.stream().map(UUID::toString).toList());
        config.set("ignore-all", ignoreAll.stream().map(UUID::toString).toList());
        for (Map.Entry<UUID, Set<UUID>> entry : blocked.entrySet()) {
            config.set("blocked." + entry.getKey(), entry.getValue().stream().map(UUID::toString).toList());
        }
        for (Map.Entry<UUID, Set<UUID>> entry : ignored.entrySet()) {
            config.set("ignored." + entry.getKey(), entry.getValue().stream().map(UUID::toString).toList());
        }
        for (Map.Entry<UUID, Boolean> entry : autoAccept.entrySet()) {
            config.set("auto-accept." + entry.getKey(), entry.getValue());
        }
        try { config.save(dataFile); } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public void loadToggles(Set<UUID> disabled, Set<UUID> ignoreAll, Map<UUID, Set<UUID>> blocked, Map<UUID, Set<UUID>> ignored, Map<UUID, Boolean> autoAccept) {
        if (!dataFile.exists()) return;
        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        config.getStringList("tpa-disabled").forEach(s -> disabled.add(UUID.fromString(s)));
        config.getStringList("ignore-all").forEach(s -> ignoreAll.add(UUID.fromString(s)));

        ConfigurationSection blockedSection = config.getConfigurationSection("blocked");
        if (blockedSection != null) {
            for (String key : blockedSection.getKeys(false)) {
                blocked.put(UUID.fromString(key), config.getStringList("blocked." + key).stream().map(UUID::fromString).collect(Collectors.toSet()));
            }
        }
        ConfigurationSection ignoredSection = config.getConfigurationSection("ignored");
        if (ignoredSection != null) {
            for (String key : ignoredSection.getKeys(false)) {
                ignored.put(UUID.fromString(key), config.getStringList("ignored." + key).stream().map(UUID::fromString).collect(Collectors.toSet()));
            }
        }
        ConfigurationSection autoAcceptSection = config.getConfigurationSection("auto-accept");
        if (autoAcceptSection != null) {
            for (String key : autoAcceptSection.getKeys(false)) {
                autoAccept.put(UUID.fromString(key), config.getBoolean("auto-accept." + key));
            }
        }
    }

    @Override
    public void saveHomes(Map<UUID, Map<String, Home>> homes) {
        FileConfiguration config = new YamlConfiguration();
        for (Map.Entry<UUID, Map<String, Home>> entry : homes.entrySet()) {
            for (Map.Entry<String, Home> homeEntry : entry.getValue().entrySet()) {
                config.set(entry.getKey().toString() + "." + homeEntry.getKey(), LocationUtil.serialize(homeEntry.getValue().getLocation()));
            }
        }
        try { config.save(homesFile); } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public void loadHomes(Map<UUID, Map<String, Home>> homes) {
        if (!homesFile.exists()) return;
        FileConfiguration config = YamlConfiguration.loadConfiguration(homesFile);
        for (String uuidStr : config.getKeys(false)) {
            UUID uuid = UUID.fromString(uuidStr);
            ConfigurationSection section = config.getConfigurationSection(uuidStr);
            if (section != null) {
                Map<String, Home> playerHomeMap = new HashMap<>();
                for (String name : section.getKeys(false)) {
                    Location loc = LocationUtil.deserialize(section.getString(name));
                    if (loc != null) playerHomeMap.put(name.toLowerCase(), new Home(uuid, name, loc));
                }
                homes.put(uuid, playerHomeMap);
            }
        }
    }

    @Override
    public void saveSpawns(Map<String, Spawn> spawns) {
        FileConfiguration config = new YamlConfiguration();
        for (Map.Entry<String, Spawn> entry : spawns.entrySet()) {
            config.set(entry.getKey(), LocationUtil.serialize(entry.getValue().getLocation()));
        }
        try { config.save(spawnsFile); } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public void loadSpawns(Map<String, Spawn> spawns) {
        if (!spawnsFile.exists()) return;
        FileConfiguration config = YamlConfiguration.loadConfiguration(spawnsFile);
        for (String world : config.getKeys(false)) {
            Location loc = LocationUtil.deserialize(config.getString(world));
            if (loc != null) spawns.put(world, new Spawn(world, loc));
        }
    }
}
