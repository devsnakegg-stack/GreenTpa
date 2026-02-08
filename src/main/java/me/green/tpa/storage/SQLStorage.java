package me.green.tpa.storage;

import me.green.tpa.GreenTPA;
import me.green.tpa.models.Home;
import me.green.tpa.models.Spawn;
import me.green.tpa.utils.LocationUtil;
import org.bukkit.Location;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public abstract class SQLStorage implements DataStorage {

    protected final GreenTPA plugin;
    protected Connection connection;

    public SQLStorage(GreenTPA plugin) {
        this.plugin = plugin;
    }

    protected abstract Connection createConnection() throws SQLException;

    @Override
    public void init() {
        try {
            connection = createConnection();
            try (Statement s = connection.createStatement()) {
                s.execute("CREATE TABLE IF NOT EXISTS gtp_toggles (uuid VARCHAR(36) PRIMARY KEY, disabled BOOLEAN, ignore_all BOOLEAN, auto_accept BOOLEAN)");
                s.execute("CREATE TABLE IF NOT EXISTS gtp_relations (uuid VARCHAR(36), target_uuid VARCHAR(36), type VARCHAR(10), PRIMARY KEY (uuid, target_uuid, type))");
                s.execute("CREATE TABLE IF NOT EXISTS gtp_homes (uuid VARCHAR(36), name VARCHAR(50), location TEXT, PRIMARY KEY (uuid, name))");
                s.execute("CREATE TABLE IF NOT EXISTS gtp_spawns (world VARCHAR(100) PRIMARY KEY, location TEXT)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void saveToggles(Set<UUID> disabled, Set<UUID> ignoreAll, Map<UUID, Set<UUID>> blocked, Map<UUID, Set<UUID>> ignored, Map<UUID, Boolean> autoAccept) {
        try {
            connection.setAutoCommit(false);
            // We'll use a simpler approach: clear and re-insert for the relations, update/insert for toggles
            // A more professional way would be diffing, but this is reliable.

            Set<UUID> allUuids = new HashSet<>(disabled);
            allUuids.addAll(ignoreAll);
            allUuids.addAll(autoAccept.keySet());

            try (PreparedStatement ps = connection.prepareStatement("REPLACE INTO gtp_toggles (uuid, disabled, ignore_all, auto_accept) VALUES (?, ?, ?, ?)")) {
                for (UUID uuid : allUuids) {
                    ps.setString(1, uuid.toString());
                    ps.setBoolean(2, disabled.contains(uuid));
                    ps.setBoolean(3, ignoreAll.contains(uuid));
                    ps.setBoolean(4, autoAccept.getOrDefault(uuid, false));
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            try (Statement s = connection.createStatement()) { s.execute("DELETE FROM gtp_relations"); }
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO gtp_relations (uuid, target_uuid, type) VALUES (?, ?, ?)")) {
                for (Map.Entry<UUID, Set<UUID>> entry : blocked.entrySet()) {
                    for (UUID target : entry.getValue()) {
                        ps.setString(1, entry.getKey().toString()); ps.setString(2, target.toString()); ps.setString(3, "BLOCK"); ps.addBatch();
                    }
                }
                for (Map.Entry<UUID, Set<UUID>> entry : ignored.entrySet()) {
                    for (UUID target : entry.getValue()) {
                        ps.setString(1, entry.getKey().toString()); ps.setString(2, target.toString()); ps.setString(3, "IGNORE"); ps.addBatch();
                    }
                }
                ps.executeBatch();
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void loadToggles(Set<UUID> disabled, Set<UUID> ignoreAll, Map<UUID, Set<UUID>> blocked, Map<UUID, Set<UUID>> ignored, Map<UUID, Boolean> autoAccept) {
        try (Statement s = connection.createStatement()) {
            ResultSet rs = s.executeQuery("SELECT * FROM gtp_toggles");
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                if (rs.getBoolean("disabled")) disabled.add(uuid);
                if (rs.getBoolean("ignore_all")) ignoreAll.add(uuid);
                autoAccept.put(uuid, rs.getBoolean("auto_accept"));
            }
            rs = s.executeQuery("SELECT * FROM gtp_relations");
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                UUID target = UUID.fromString(rs.getString("target_uuid"));
                String type = rs.getString("type");
                if (type.equals("BLOCK")) blocked.computeIfAbsent(uuid, k -> new HashSet<>()).add(target);
                else ignored.computeIfAbsent(uuid, k -> new HashSet<>()).add(target);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void saveHomes(Map<UUID, Map<String, Home>> homes) {
        try {
            connection.setAutoCommit(false);
            try (Statement s = connection.createStatement()) { s.execute("DELETE FROM gtp_homes"); }
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO gtp_homes (uuid, name, location) VALUES (?, ?, ?)")) {
                for (Map.Entry<UUID, Map<String, Home>> entry : homes.entrySet()) {
                    for (Home home : entry.getValue().values()) {
                        ps.setString(1, entry.getKey().toString());
                        ps.setString(2, home.getName());
                        ps.setString(3, LocationUtil.serialize(home.getLocation()));
                        ps.addBatch();
                    }
                }
                ps.executeBatch();
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void loadHomes(Map<UUID, Map<String, Home>> homes) {
        try (Statement s = connection.createStatement(); ResultSet rs = s.executeQuery("SELECT * FROM gtp_homes")) {
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                String name = rs.getString("name");
                Location loc = LocationUtil.deserialize(rs.getString("location"));
                if (loc != null) {
                    homes.computeIfAbsent(uuid, k -> new HashMap<>()).put(name.toLowerCase(), new Home(uuid, name, loc));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void saveSpawns(Map<String, Spawn> spawns) {
        try {
            connection.setAutoCommit(false);
            try (Statement s = connection.createStatement()) { s.execute("DELETE FROM gtp_spawns"); }
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO gtp_spawns (world, location) VALUES (?, ?)")) {
                for (Map.Entry<String, Spawn> entry : spawns.entrySet()) {
                    ps.setString(1, entry.getKey());
                    ps.setString(2, LocationUtil.serialize(entry.getValue().getLocation()));
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void loadSpawns(Map<String, Spawn> spawns) {
        try (Statement s = connection.createStatement(); ResultSet rs = s.executeQuery("SELECT * FROM gtp_spawns")) {
            while (rs.next()) {
                String world = rs.getString("world");
                Location loc = LocationUtil.deserialize(rs.getString("location"));
                if (loc != null) spawns.put(world, new Spawn(world, loc));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
