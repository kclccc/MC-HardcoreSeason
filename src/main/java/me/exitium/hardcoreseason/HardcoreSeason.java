package me.exitium.hardcoreseason;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;

public final class HardcoreSeason extends JavaPlugin {
    private HardcoreSeason instance;

    public HardcoreSeason getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        if (instance != null || !Bukkit.getServer().getWorlds().isEmpty() || !Bukkit.getOnlinePlayers().isEmpty()) {
            getLogger().severe("Server reloaded. This plugin may not run correctly.");
        }
        instance = this;
    }

    HikariDataSource hikari;
    Connection sqlConnection;

    public Connection sqlConn() {
        return sqlConnection;
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
