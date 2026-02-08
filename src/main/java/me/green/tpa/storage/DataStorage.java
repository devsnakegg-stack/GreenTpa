package me.green.tpa.storage;

import me.green.tpa.models.Home;
import me.green.tpa.models.Spawn;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface DataStorage {

    void init();
    void close();

    // Toggle Manager Data
    void saveToggles(Set<UUID> disabled, Set<UUID> ignoreAll, Map<UUID, Set<UUID>> blocked, Map<UUID, Set<UUID>> ignored, Map<UUID, Boolean> autoAccept);
    void loadToggles(Set<UUID> disabled, Set<UUID> ignoreAll, Map<UUID, Set<UUID>> blocked, Map<UUID, Set<UUID>> ignored, Map<UUID, Boolean> autoAccept);

    // Home Manager Data
    void saveHomes(Map<UUID, Map<String, Home>> homes);
    void loadHomes(Map<UUID, Map<String, Home>> homes);

    // Spawn Manager Data
    void saveSpawns(Map<String, Spawn> spawns);
    void loadSpawns(Map<String, Spawn> spawns);
}
