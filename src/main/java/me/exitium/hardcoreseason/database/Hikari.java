package me.exitium.hardcoreseason.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.exitium.hardcoreseason.HardcoreSeason;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public record Hikari(HardcoreSeason plugin) {

    public Connection setupHikari(String storageType) {
        if (storageType == null) {
            storageType = "SQLITE";
            plugin.getLogger().warning("Storage type not specified, using SQLITE.");
        }

        Connection conn = null;
        int tries = 0;
        String type = storageType;

        while (conn == null && tries < 3) {
            HikariConfig config = new HikariConfig();

            switch (type) {
                case "MYSQL" -> {
                    String baseURL = "jdbc:mysql://";
                    String hostname = plugin.getConfig().getString("database.hostname");
                    int port = plugin.getConfig().getInt("database.port");
                    String username = plugin.getConfig().getString("database.username");
                    String password = plugin.getConfig().getString("database.password");
                    String finalURL = baseURL + hostname + ":" + port + "/";

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

            try (HikariDataSource hikari = new HikariDataSource(config)) {
                conn = hikari.getConnection();
                tries++;
            } catch (SQLException e) {
                if (type.equals("MYSQL")) {
                    type = "SQLITE";
                    plugin.getDb().setStorageType(type);
                }
            }
        }
        return conn;
    }
}
