package me.exitium.hardcoreseason.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.exitium.hardcoreseason.HardcoreSeason;

import java.io.File;
import java.io.IOException;

public record Hikari(HardcoreSeason plugin) {

    public HikariDataSource setupHikari(String storageType) {
        if (storageType == null) {
            storageType = "SQLITE";
            plugin.getLogger().warning("Storage type not specified, using SQLITE.");
        }

        HikariConfig config = new HikariConfig();

        switch (storageType) {
            case "MYSQL" -> {
                String baseURL = "jdbc:mysql://";
                String hostname = plugin.getConfig().getString("database.hostname");
                int port = plugin.getConfig().getInt("database.port");
                String database = plugin.getConfig().getString("database.db-name");
                String username = plugin.getConfig().getString("database.username");
                String password = plugin.getConfig().getString("database.password");
                String finalURL = baseURL + hostname + ":" + port + "/" + database;

                plugin.getLogger().warning(finalURL);

                config.setDriverClassName("com.mysql.jdbc.Driver");
                config.setJdbcUrl(finalURL);
                config.setUsername(username);
                config.setPassword(password);
            }

            case "SQLITE" -> {
                String filename = plugin.getConfig().getString("database.filename");
                if (filename == null || filename.equals("")) {
                    filename = "hardcoreseason.db";
                    plugin.getConfig().set("database.filename", filename);
                    plugin.getLogger().warning("No filename found for SQLITE, setting default: hardcoreseason.db");
                }

                if (!filename.endsWith(".db")) {
                    filename += ".db";
                    plugin.getConfig().set("database.filename", filename);
                }

                plugin.saveConfig();

                File sqlite = new File(plugin.getDataFolder(), filename);
                if (!sqlite.exists()) {
                    try {
                        if (sqlite.createNewFile()) {
                            // TODO: generate default table
                            plugin.getLogger().info("Created new database file: " + filename);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                config.setDriverClassName("org.sqlite.JDBC");
                config.setJdbcUrl("jdbc:sqlite:plugins/HardcoreSeason/" + filename);
            }
        }

        return new HikariDataSource(config);
    }
}
