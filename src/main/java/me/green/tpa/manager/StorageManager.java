package me.green.tpa.manager;

import me.green.tpa.GreenTPA;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;

public class StorageManager {

    private final GreenTPA plugin;
    private Connection connection;

    public StorageManager(GreenTPA plugin) {
        this.plugin = plugin;
    }

    public void init() {
        String type = plugin.getConfig().getString("storage.type", "sqlite").toLowerCase();
        if (type.equals("sqlite")) {
            initSQLite();
        } else if (type.equals("mysql")) {
            initMySQL();
        }
    }

    private void initSQLite() {
        try {
            File dataFolder = new File(plugin.getDataFolder(), "greentpa.db");
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            plugin.getLogger().info("Successfully connected to SQLite database.");
        } catch (Exception e) {
            plugin.getLogger().severe("Could not connect to SQLite database! Falling back to YAML.");
            e.printStackTrace();
        }
    }

    private void initMySQL() {
        // Implementation for MySQL would go here.
        // For now, we fall back to SQLite or YAML if misconfigured.
        plugin.getLogger().warning("MySQL storage not yet fully implemented, falling back to SQLite/YAML.");
        initSQLite();
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
