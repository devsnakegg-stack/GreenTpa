package me.green.tpa.storage;

import me.green.tpa.GreenTPA;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteStorage extends SQLStorage {

    public SQLiteStorage(GreenTPA plugin) {
        super(plugin);
    }

    @Override
    protected Connection createConnection() throws SQLException {
        File dataFolder = new File(plugin.getDataFolder(), "greentpa.db");
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
    }
}
