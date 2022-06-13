package me.exitium.hardcoreseason;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.multiverseinventories.MultiverseInventories;
import com.onarandombox.multiverseinventories.WorldGroup;
import com.onarandombox.multiverseinventories.profile.WorldGroupManager;
import com.onarandombox.multiverseinventories.share.Sharables;
import me.exitium.hardcoreseason.commands.*;
import me.exitium.hardcoreseason.database.DatabaseManager;
import me.exitium.hardcoreseason.database.Hikari;
import me.exitium.hardcoreseason.listeners.*;
import me.exitium.hardcoreseason.player.HCPlayer;
import me.exitium.hardcoreseason.worldhandler.HCWorldManager;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class HardcoreSeason extends JavaPlugin {
    private static Permission perms = null;
    Map<UUID, HCPlayer> onlinePlayers;
    Map<UUID, Integer> teleportingPlayers;
    HCWorldManager hcWorldManager;
    DatabaseManager db;
    Connection sqlConnection;
    MultiverseCore multiverseCore;
    private HardcoreSeason instance;
    private YamlConfiguration rewardsConfig;
    private int seasonNumber;

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
        registerEvents();

        seasonNumber = getConfig().getInt("season-number");
        getLogger().info("Current Hardcore Season: " + seasonNumber);

        hcWorldManager = new HCWorldManager(this);
        hcWorldManager.loadWorldsFromConfig();

        if (seasonNumber == 0) {
            getLogger().info("No data found, running initialization.");
            hcWorldManager.createAll();
            getConfig().set("season-number", seasonNumber + 1);
            saveConfig();
        }

        db = new DatabaseManager(this);
        sqlConnection = new Hikari(this).setupHikari(getDb().getStorageType());

        if (sqlConnection == null) {
            getLogger().warning("SQL Connection is null, data cannot be saved! Please check your config options.");
        } else {
            String storageType = db.getStorageType();
            if (storageType == null || storageType.equals("")) {
                getLogger().info("Could not get storage type from config file. Defaulting to SQLITE.");
                storageType = "SQLITE";
            }
            if (storageType.equals("MYSQL")) db.createDatabase();
            db.initTable(storageType);
        }

        setupPermissions();
        setupMVInventoryGroups();

        onlinePlayers = new HashMap<>();
        teleportingPlayers = new HashMap<>();
    }

    private void registerCommands() {
        Map<String, PluginCommand> commandList = new HashMap<>() {{
            put("helpCommand", getCommand("hchelp"));
            put("enterCommand", getCommand("hcenter"));
            put("exitCommand", getCommand("hcexit"));
            put("statsCommand", getCommand("hcstats"));
            put("scoreboardCommand", getCommand("hclist"));
            put("spectateCommand", getCommand("hcspectate"));
        }};

        for (Map.Entry<String, PluginCommand> entry : commandList.entrySet()) {
            if (entry.getValue() == null) {
                getLogger().warning("Failed to register command: " + entry.getKey());
                commandList.remove(entry.getKey());
                break;
            }

            switch (entry.getKey()) {
                case "helpCommand" -> entry.getValue().setExecutor(new HelpCommand(this));
                case "enterCommand" -> entry.getValue().setExecutor(new EnterHardcoreCommand(this));
                case "exitCommand" -> entry.getValue().setExecutor(new ExitHardcoreCommand(this));
                case "statsCommand" -> entry.getValue().setExecutor(new GetStatbookCommand(this));
                case "scoreboardCommand" -> entry.getValue().setExecutor(new ShowScoreboardCommand(this));
                case "spectateCommand" -> entry.getValue().setExecutor(new SpectateCommand(this));
            }
        }
    }

    private void registerEvents() {
        final PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerJoinListener(this), this);
        pluginManager.registerEvents(new PlayerQuitListener(this), this);
        pluginManager.registerEvents(new BedListener(this), this);
        pluginManager.registerEvents(new BlockListener(this), this);
        pluginManager.registerEvents(new ConsumeItemListener(this), this);
        pluginManager.registerEvents(new DeathRespawnListener(this), this);
        pluginManager.registerEvents(new EntityDamageListener(this), this);
        pluginManager.registerEvents(new EntityDeathListener(this), this);
        pluginManager.registerEvents(new GamemodeListener(this), this);
        pluginManager.registerEvents(new InventoryListener(this), this);
        pluginManager.registerEvents(new PlayerTeleportListener(this), this);
        pluginManager.registerEvents(new PlayerInteractListener(this), this);
        pluginManager.registerEvents(new PlayerMoveListener(this), this);
        pluginManager.registerEvents(new CraftItemListener(this), this);
        pluginManager.registerEvents(new BarterListener(this), this);
        pluginManager.registerEvents(new PlayerDropItemListener(this), this);
        pluginManager.registerEvents(new PlayerPickupItemListener(this), this);
    }

    public List<UUID> getAllOnlinePlayers() {
        return onlinePlayers.keySet().stream().toList();
    }

    public HCPlayer getOnlinePlayer(UUID uuid) {
        return onlinePlayers.get(uuid);
    }

    public void addOnlinePlayer(HCPlayer hcPlayer) {
        onlinePlayers.put(hcPlayer.getUUID(), hcPlayer);
    }

    public void remOnlinePlayer(UUID uuid) {
        onlinePlayers.remove(uuid);
    }

    public Integer getTeleportingPlayer(UUID uuid) {
        return teleportingPlayers.get(uuid);
    }

    public boolean isTeleportingPlayer(UUID uuid) {
        return teleportingPlayers.containsKey(uuid);
    }

    public void addTeleportingPlayer(UUID uuid, int taskID) {
        teleportingPlayers.put(uuid, taskID);
    }

    public void remTeleportingPlayer(UUID uuid) {
        getOnlinePlayer(uuid).setTeleportTaskID(0);
        teleportingPlayers.remove(uuid);
    }

    public HCWorldManager getHcWorldManager() {
        return hcWorldManager;
    }

    public DatabaseManager getDb() {
        return db;
    }

    public Connection getSqlConnection() {
        return sqlConnection;
    }

    public MultiverseCore getMultiverseCore() {
        return multiverseCore;
    }

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

    private void setupMVInventoryGroups() {
        WorldGroupManager groupManager = getMultiverseInventories().getGroupManager();
        if (groupManager.getGroup("Hardcore") == null) {
            getLogger().info("Multiverse-Inventories HARDCORE group not found, creating!");
            WorldGroup worldGroup = groupManager.newEmptyGroup("Hardcore");
            getHcWorldManager().getHardcoreWorlds().forEach(
                    (k, v) -> worldGroup.addWorld(v));
            worldGroup.getShares().addAll(Sharables.all());
            groupManager.updateGroup(worldGroup);
        }
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

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
    }
}
