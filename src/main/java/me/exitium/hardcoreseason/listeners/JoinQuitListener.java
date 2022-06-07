package me.exitium.hardcoreseason.listeners;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.Utils;
import me.exitium.hardcoreseason.database.DatabaseController;
import me.exitium.hardcoreseason.playerdata.HCPlayer;
import me.exitium.hardcoreseason.playerdata.HCPlayerController;
import me.exitium.hardcoreseason.playerdata.HCPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JoinQuitListener implements Listener {
    private final HardcoreSeason plugin;

    public JoinQuitListener(HardcoreSeason plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        UUID uuid = player.getUniqueId();

        if (plugin.isHardcoreWorld(world.getName())) {
            HCPlayerManager hcPlayerManager = plugin.getHcPlayerService();
            DatabaseController databaseController = plugin.getDatabaseController();

            // Since the same world-name is used, players who logged out in one season could log back into another
            // If they aren't in the database for the current season, create them and clear inventory & exp.
            if (!hcPlayerManager.getAllHardcorePlayers().containsKey(uuid) || !databaseController.getReader().playerExists(uuid)) {

                if (plugin.hasPermission(Bukkit.getOfflinePlayer(uuid), "hardcoreseason.hasdied")) {
                    plugin.remPermission(Bukkit.getOfflinePlayer(uuid), "hardcoreseason.hasdied");
                }

                List<ItemStack> inventoryList = new ArrayList<>();
                for (ItemStack itemStack : player.getInventory()) {
                    inventoryList.add(itemStack);
                }

                File playerFile = new File(plugin.getDataFolder(), player.getName() + ".yml");
                FileConfiguration playerConfig = new YamlConfiguration();
                playerConfig.set("name", player.getName());
                playerConfig.set("exp", player.getExp());
                playerConfig.set("level", player.getLevel());
                playerConfig.set("inventory", inventoryList);
                try {
                    playerConfig.save(playerFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                player.sendMessage(Utils.chat("&cError: A new season has started since you left. Adding as a new player."));
                player.setExp(0);
                player.setLevel(0);
                player.setHealth(20);
                player.setFoodLevel(20);
                player.getInventory().clear();
                player.setGameMode(GameMode.SURVIVAL);
                for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                    player.removePotionEffect(potionEffect.getType());
                }
                player.teleport(Bukkit.getWorld(plugin.getHardcoreWorld(World.Environment.NORMAL)).getSpawnLocation());

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        HCPlayer hcPlayer = hcPlayerManager.createNewPlayer(uuid);
                        hcPlayerManager.addPlayerToList(hcPlayer);
                        databaseController.getWriter().addNewPlayer(hcPlayer);
                        hcPlayer.setOnline(true);
                        hcPlayer.setTimeCounter(System.currentTimeMillis());
                    }
                }.runTaskAsynchronously(plugin);
            } else {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        HCPlayer hcPlayer = hcPlayerManager.getHardcorePlayer(uuid);
                        hcPlayer.setOnline(true);
                        hcPlayer.setTimeCounter(System.currentTimeMillis());
                        hcPlayerManager.addPlayerToList(hcPlayer);
                    }
                }.runTaskAsynchronously(plugin);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (plugin.isHardcoreWorld(player.getWorld().getName())) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    UUID uuid = player.getUniqueId();
                    HCPlayer hcPlayer = plugin.getHcPlayerService().getHardcorePlayer(uuid);
                    new HCPlayerController(hcPlayer).updateTime();
                    if (hcPlayer.getStatus() != HCPlayer.STATUS.DEAD) {
                        plugin.getDatabaseController().getWriter().syncPlayer(hcPlayer);
                    }
                    hcPlayer.setOnline(false);
                }
            }.runTaskAsynchronously(plugin);
        }
    }
}
