package me.green.tpa.manager;

import me.green.tpa.GreenTPA;
import me.green.tpa.storage.*;

public class StorageManager {

    private final GreenTPA plugin;
    private DataStorage storage;

    public StorageManager(GreenTPA plugin) {
        this.plugin = plugin;
    }

    public void init() {
        String type = plugin.getConfig().getString("storage.type", "sqlite").toLowerCase();
        switch (type) {
            case "yaml" -> storage = new YAMLStorage(plugin);
            case "mysql" -> storage = new MySQLStorage(plugin);
            default -> storage = new SQLiteStorage(plugin);
        }
        storage.init();
        plugin.getLogger().info("Using storage backend: " + type.toUpperCase());
    }

    public DataStorage getStorage() {
        return storage;
    }

    public void close() {
        if (storage != null) {
            storage.close();
        }
    }
}
