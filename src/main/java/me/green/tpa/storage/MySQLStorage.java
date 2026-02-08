package me.green.tpa.storage;

import me.green.tpa.GreenTPA;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLStorage extends SQLStorage {

    public MySQLStorage(GreenTPA plugin) {
        super(plugin);
    }

    @Override
    protected Connection createConnection() throws SQLException {
        ConfigurationSection mysql = plugin.getConfig().getConfigurationSection("storage.mysql");
        if (mysql == null) throw new SQLException("MySQL configuration is missing!");

        String host = mysql.getString("host", "127.0.0.1");
        int port = mysql.getInt("port", 3306);
        String database = mysql.getString("database", "greentpa");
        String username = mysql.getString("username", "root");
        String password = mysql.getString("password", "password");
        boolean ssl = mysql.getBoolean("ssl", false);

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + ssl;
        return DriverManager.getConnection(url, username, password);
    }
}
