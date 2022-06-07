package me.exitium.hardcoreseason;

import com.onarandombox.MultiverseCore.MultiverseCore;
import me.exitium.hardcoreseason.commands.EnterHardcoreCommand;
import me.exitium.hardcoreseason.commands.ExitHardcoreCommand;
import me.exitium.hardcoreseason.database.DatabaseManager;
import me.exitium.hardcoreseason.player.HCPlayer;
import me.exitium.hardcoreseason.worldhandler.HCWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class HardcoreSeason extends JavaPlugin {
    private HardcoreSeason instance;

    @Override
    public void onLoad() {
        if (instance != null || !Bukkit.getServer().getWorlds().isEmpty() || !Bukkit.getOnlinePlayers().isEmpty()) {
            getLogger().severe("Server reloaded. This plugin may not run correctly.");
        }
        instance = this;
    }

    @Override
    public void onEnable() {
        multiverseCore = initMultiverse();
        if (multiverseCore == null) {
            getLogger().severe("Could not bind to MULTIVERSE-CORE, disabling!");
            return;
        }

        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
            saveResource("hardcore-worlds.yml", false);
        }

        registerCommands();

        seasonNumber = getConfig().getInt("season-number");
        getLogger().info("Current Hardcore Season: " + seasonNumber);

        hcWorldManager = new HCWorldManager(this);

        if (seasonNumber == 0) {
            getLogger().info("No data found, running initialization.");
            hcWorldManager.createAll();
            getConfig().set("season-number", seasonNumber + 1);
            saveConfig();
        }

        db = new DatabaseManager(this);
        sqlConnection = db.initHikari();
        if (sqlConnection == null) {
            getLogger().warning("SQL Connection is null, data cannot be saved! Please check your config options.");
        } else {
            db.initTable(getConfig().getString("storage-type"));
        }

        onlinePlayers = new HashMap<>();
    }

    private void registerCommands() {
        this.getCommand("hcenter").setExecutor(new EnterHardcoreCommand(this));
        this.getCommand("hcexit").setExecutor(new ExitHardcoreCommand(this));
    }

    Map<UUID, HCPlayer> onlinePlayers;

    public void addOnlinePlayer(HCPlayer player) {
        onlinePlayers.put(player.getUUID(), player);
    }

    public void remOnlinePlayer(UUID uuid) {
        onlinePlayers.remove(uuid);
    }

    Map<UUID, Integer> teleportingPlayers;

    public Integer getTeleportingPlayer(UUID uuid) {
        return teleportingPlayers.get(uuid);
    }

    public void addTeleportingPlayer(UUID uuid, int taskID) {
        teleportingPlayers.put(uuid, taskID);
    }

    public void remTeleportingPlayer(UUID uuid) {
        teleportingPlayers.remove(uuid);
    }

    HCWorldManager hcWorldManager;

    public HCWorldManager getHcWorldManager() {
        return hcWorldManager;
    }

    DatabaseManager db;

    public DatabaseManager getDb() {
        return db;
    }

    Connection sqlConnection;

    public Connection getSqlConnection() {
        return sqlConnection;
    }

    MultiverseCore multiverseCore;

    public MultiverseCore getMultiverseCore() {
        return multiverseCore;
    }

    public MultiverseCore initMultiverse() {
        Plugin mvPlugin = Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
        if ((mvPlugin instanceof MultiverseCore)) {
            return (MultiverseCore) mvPlugin;
        }
        return null;
    }

    private int seasonNumber;

    public int getSeasonNumber() {
        return seasonNumber;
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
