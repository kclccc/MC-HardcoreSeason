package me.exitium.hardcoreseason.database;

import com.zaxxer.hikari.HikariDataSource;
import me.exitium.hardcoreseason.HardcoreSeason;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {
    private final HardcoreSeason plugin;


    DatabaseWriter writer;
    DatabaseReader reader;

    public DatabaseManager(HardcoreSeason plugin) {
        this.plugin = plugin;

        reader = new DatabaseReader(plugin);
        writer = new DatabaseWriter(plugin);
    }

    HikariDataSource hikari;

    public Connection initHikari() {
        String storageType = plugin.getConfig().getString("storage-type");
        hikari = new Hikari(plugin).setupHikari(storageType);
        try {
            Connection connection = hikari.getConnection();
            plugin.getLogger().info("Attempting to connect to SQL...");
            if (!connection.isValid(10)) {
                plugin.getLogger().warning("SQL could not connect, falling back to SQLITE.");
                hikari = new Hikari(plugin).setupHikari("SQLITE");
            } else {
                plugin.getLogger().info("SQL Connection successful!");
                return connection;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void initTable() {
        try (PreparedStatement ps = plugin.getSqlConnection().prepareStatement(
                "CREATE TABLE IF NOT EXISTS hardcore_season (" +
                        "rowid INTEGER NOT NULL AUTO_INCREMENT, " +
                        "uuid BINARY(16) NOT NULL, " +
                        "season_number INT NOT NULL, " +
                        "status INT NOT NULL, " +
                        "time INT, " +
                        "spawn_point TEXT, " +
                        "death_type TEXT, " +
                        "inventory TEXT, " +
                        "return_location TEXT, " +
                        "monster_kills TEXT, " +
                        "damage_taken TEXT, " +
                        "damage_dealt TEXT, " +
                        "items_crafted TEXT, " +
                        "trades_made TEXT, " +
                        "food_eaten TEXT, " +
                        "potions_used TEXT " +
                        "PRIMARY KEY (rowid));")) {
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
