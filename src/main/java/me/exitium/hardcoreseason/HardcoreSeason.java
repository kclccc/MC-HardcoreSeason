package me.exitium.hardcoreseason;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.multiverseinventories.MultiverseInventories;
import me.exitium.hardcoreseason.commands.EnterHardcoreCommand;
import me.exitium.hardcoreseason.commands.ExitHardcoreCommand;
import me.exitium.hardcoreseason.database.DatabaseManager;
import me.exitium.hardcoreseason.player.HCPlayer;
import me.exitium.hardcoreseason.worldhandler.HCWorldManager;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
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
            String storageType = getConfig().getString("storage-type");
            if (storageType == null || storageType.equals("")) {
                getLogger().info("Could not get storage type from config file. Defaulting to SQLITE.");
                storageType = "SQLITE";
            }
            db.initTable(storageType);
        }

        onlinePlayers = new HashMap<>();
    }

    private void registerCommands() {
        PluginCommand enterCommand = this.getCommand("hcenter");
        PluginCommand exitCommand = this.getCommand("hcexit");

        if (enterCommand == null || exitCommand == null) {
            getLogger().warning("Failed to find one or more commands, Plugin may not work properly!");
            return;
        }

        enterCommand.setExecutor(new EnterHardcoreCommand(this));
        exitCommand.setExecutor(new ExitHardcoreCommand(this));
    }

    Map<UUID, HCPlayer> onlinePlayers;

    public HCPlayer getOnlinePlayer(UUID uuid) {
        return onlinePlayers.get(uuid);
    }

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

    private YamlConfiguration rewardsConfig;

    public YamlConfiguration getRewardsConfig() {
        return rewardsConfig;
    }

    private void createRewardsConfig() {
        File rewardsConfigFile = new File(getDataFolder(), "rewards.yml");
        if (!rewardsConfigFile.exists()) {
            rewardsConfigFile.getParentFile().mkdirs();
            saveResource("rewards.yml", false);
        }

        rewardsConfig = new YamlConfiguration();
        try {
            rewardsConfig.load(rewardsConfigFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getTeleportCooldown() {
        int teleportCD = getConfig().getInt("teleport-cooldown");
        if (teleportCD == 0) {
            getLogger().info("No teleport-cooldown found or it was 0. Check config if this is incorrect!");
        }
        return teleportCD;
    }

    public MultiverseCore initMultiverse() {
        Plugin mvPlugin = Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
        if ((mvPlugin instanceof MultiverseCore)) {
            return (MultiverseCore) mvPlugin;
        }
        return null;
    }

    public MultiverseInventories getMultiverseInventories() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Inventories");

        if (plugin instanceof MultiverseInventories) {
            return (MultiverseInventories) plugin;
        }
        throw new RuntimeException("Multiverse-Inventories not found!");
    }

//    private void setupMVInventoryGroups() {
//        WorldGroupManager groupManager = getMultiverseInventories().getGroupManager();
//        if (groupManager.getGroup("Hardcore") == null) {
//            WorldGroup worldGroup = groupManager.newEmptyGroup("Hardcore");
//            getHardcoreWorlds().forEach((k, v) -> worldGroup.addWorld(v));
//            worldGroup.getShares().addAll(Sharables.all());
//            groupManager.updateGroup(worldGroup);
//        }
//    }

    private int seasonNumber;

    public int getSeasonNumber() {
        return seasonNumber;
    }

    private static Permission perms = null;

    private void setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        assert rsp != null;
        perms = rsp.getProvider();
    }

    public void setPermission(OfflinePlayer player, String perm) {
        perms.playerAdd(null, player, perm);
    }

    public void remPermission(OfflinePlayer player, String perm) {
        perms.playerRemove(null, player, perm);
    }

    public boolean hasPermission(OfflinePlayer player, String perm) {
        return perms.playerHas(null, player, perm);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
