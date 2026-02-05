package me.green.tpa.manager;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ToggleManager {

    private final Set<UUID> tpaDisabled = new HashSet<>();
    private final Map<UUID, Set<UUID>> blockedPlayers = new HashMap<>();
    private final Map<UUID, Set<UUID>> ignoredPlayers = new HashMap<>();
    private final Set<UUID> ignoreAll = new HashSet<>();
    private final Map<UUID, Boolean> autoAccept = new HashMap<>();
    private boolean defaultAutoAccept = false;

    public void setDefaultAutoAccept(boolean val) {
        this.defaultAutoAccept = val;
    }

    public void load(File file) {
        if (!file.exists()) return;
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        List<String> disabled = config.getStringList("tpa-disabled");
        disabled.forEach(s -> tpaDisabled.add(UUID.fromString(s)));

        List<String> ignoringAll = config.getStringList("ignore-all");
        ignoringAll.forEach(s -> ignoreAll.add(UUID.fromString(s)));

        ConfigurationSection blockedSection = config.getConfigurationSection("blocked");
        if (blockedSection != null) {
            for (String key : blockedSection.getKeys(false)) {
                Set<UUID> blocked = config.getStringList("blocked." + key).stream()
                        .map(UUID::fromString).collect(Collectors.toSet());
                blockedPlayers.put(UUID.fromString(key), blocked);
            }
        }

        ConfigurationSection ignoredSection = config.getConfigurationSection("ignored");
        if (ignoredSection != null) {
            for (String key : ignoredSection.getKeys(false)) {
                Set<UUID> ignored = config.getStringList("ignored." + key).stream()
                        .map(UUID::fromString).collect(Collectors.toSet());
                ignoredPlayers.put(UUID.fromString(key), ignored);
            }
        }

        ConfigurationSection autoAcceptSection = config.getConfigurationSection("auto-accept");
        if (autoAcceptSection != null) {
            for (String key : autoAcceptSection.getKeys(false)) {
                autoAccept.put(UUID.fromString(key), config.getBoolean("auto-accept." + key));
            }
        }
    }

    public void save(File file) {
        FileConfiguration config = new YamlConfiguration();
        config.set("tpa-disabled", tpaDisabled.stream().map(UUID::toString).toList());
        config.set("ignore-all", ignoreAll.stream().map(UUID::toString).toList());

        for (Map.Entry<UUID, Set<UUID>> entry : blockedPlayers.entrySet()) {
            config.set("blocked." + entry.getKey(), entry.getValue().stream().map(UUID::toString).toList());
        }

        for (Map.Entry<UUID, Set<UUID>> entry : ignoredPlayers.entrySet()) {
            config.set("ignored." + entry.getKey(), entry.getValue().stream().map(UUID::toString).toList());
        }

        for (Map.Entry<UUID, Boolean> entry : autoAccept.entrySet()) {
            config.set("auto-accept." + entry.getKey(), entry.getValue());
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toggleTpa(UUID uuid) {
        if (tpaDisabled.contains(uuid)) {
            tpaDisabled.remove(uuid);
        } else {
            tpaDisabled.add(uuid);
        }
    }

    public boolean isTpaDisabled(UUID uuid) {
        return tpaDisabled.contains(uuid);
    }

    public void blockPlayer(UUID uuid, UUID target) {
        blockedPlayers.computeIfAbsent(uuid, k -> new HashSet<>()).add(target);
    }

    public void unblockPlayer(UUID uuid, UUID target) {
        if (blockedPlayers.containsKey(uuid)) {
            blockedPlayers.get(uuid).remove(target);
        }
    }

    public boolean isBlocked(UUID uuid, UUID target) {
        return blockedPlayers.getOrDefault(uuid, Collections.emptySet()).contains(target);
    }

    public void ignorePlayer(UUID uuid, UUID target) {
        ignoredPlayers.computeIfAbsent(uuid, k -> new HashSet<>()).add(target);
    }

    public void unignorePlayer(UUID uuid, UUID target) {
        if (ignoredPlayers.containsKey(uuid)) {
            ignoredPlayers.get(uuid).remove(target);
        }
    }

    public boolean isIgnoring(UUID uuid, UUID target) {
        return ignoredPlayers.getOrDefault(uuid, Collections.emptySet()).contains(target) || ignoreAll.contains(uuid);
    }

    public void toggleIgnoreAll(UUID uuid) {
        if (ignoreAll.contains(uuid)) {
            ignoreAll.remove(uuid);
        } else {
            ignoreAll.add(uuid);
        }
    }

    public boolean isIgnoringAll(UUID uuid) {
        return ignoreAll.contains(uuid);
    }

    public void toggleAutoAccept(UUID uuid) {
        autoAccept.put(uuid, !isAutoAccept(uuid));
    }

    public boolean isAutoAccept(UUID uuid) {
        return autoAccept.getOrDefault(uuid, defaultAutoAccept);
    }
}
