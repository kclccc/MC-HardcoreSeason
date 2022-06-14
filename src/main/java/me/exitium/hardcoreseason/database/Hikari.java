package me.exitium.hardcoreseason.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.exitium.hardcoreseason.HardcoreSeason;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public record Hikari(HardcoreSeason plugin) {

    public HikariDataSource getHikariSource(String storageType) {

        HikariConfig config = hikariConfig(storageType);

        try {
            return new HikariDataSource(config);
        } catch (Exception e) {
//            throw new RuntimeException(e);
        }

        return new HikariDataSource(hikariConfig("SQLITE"));
    }

    public HikariConfig hikariConfig(String storageType) {
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
                String databaseName = plugin.getConfig().getString("database.db-name");
                String username = plugin.getConfig().getString("database.username");
                String password = plugin.getConfig().getString("database.password");
                String finalURL = baseURL + hostname + ":" + port + "/" + databaseName;

//                dataSource.setDriverClassName("com.mysql.jdbc.Driver");
                config.setJdbcUrl(finalURL);
                config.setUsername(username);
                config.setPassword(password);
                config.setConnectionTestQuery("SELECT 1");
                config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(3));
                config.setValidationTimeout(TimeUnit.SECONDS.toMillis(1));
                config.setInitializationFailTimeout(0);
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
                            plugin.getLogger().info("Created new database file: " + filename);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

//                dataSource.setDriverClassName("org.sqlite.JDBC");
                config.setJdbcUrl("jdbc:sqlite:plugins/HardcoreSeason/" + filename);
            }
        }
        return config;
    }
}
