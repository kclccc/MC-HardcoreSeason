package me.exitium.hardcoreseason.database;

import com.zaxxer.hikari.HikariDataSource;
import me.exitium.hardcoreseason.HardcoreSeason;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {
    private final HardcoreSeason plugin;

    DatabaseWriter writer;
    DatabaseReader reader;
    HikariDataSource hikari;
    String databaseName;
    String storageType;

    public DatabaseManager(HardcoreSeason plugin) {
        this.plugin = plugin;

        databaseName = plugin.getConfig().getString("database.db-name");
        reader = new DatabaseReader(plugin);
        writer = new DatabaseWriter(plugin);

        setStorageType(plugin.getConfig().getString("storage-type"));
    }

    public DatabaseWriter getWriter() {
        return writer;
    }

    public DatabaseReader getReader() {
        return reader;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public void createDatabase() {
        try (PreparedStatement ps = plugin.getSqlConnection().prepareStatement(
                "CREATE DATABASE IF NOT EXISTS " + databaseName
        )) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initTable(String storageType) {
        String mysql = storageType.equals("MYSQL")
                ? "rowid INTEGER PRIMARY KEY AUTO_INCREMENT, "
                : "";
        try (PreparedStatement ps = plugin.getSqlConnection().prepareStatement(
                "CREATE TABLE IF NOT EXISTS hardcore_season (" +
                        mysql +
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
                        "potions_used TEXT, " +
                        "eyes_used TEXT, " +
                        "UNIQUE(uuid,season_number));")) {
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
