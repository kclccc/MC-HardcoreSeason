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
    boolean runSetup;

    @Override
    public void onEnable() {
    if(getConfig().getInt("seasonNumber") == 0){
        //TODO: initial setup, generate worlds, setup inventory group, create tables
        getLogger().info("No data found, running initialization.");
    }


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
