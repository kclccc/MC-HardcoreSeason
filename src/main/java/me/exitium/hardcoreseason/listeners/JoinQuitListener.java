package me.exitium.hardcoreseason.listeners;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.Utils;
import me.exitium.hardcoreseason.database.DatabaseManager;
import me.exitium.hardcoreseason.player.HCPlayer;
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

public record JoinQuitListener(HardcoreSeason plugin) implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!plugin.getHcWorldManager().isHardcoreWorld(event.getPlayer().getWorld().getName())) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // Since the same world-name is used, players who logged out in one season could log back into another
        // If they aren't in the database for the current season, create them and clear inventory & exp.
        DatabaseManager db = plugin.getDb();
        if (!db.getReader().hcPlayerExists(uuid)) {
            plugin.remPermission(Bukkit.getOfflinePlayer(uuid), "hardcoreseason.hasdied");

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

            player.sendMessage(Utils.colorize("&cError: A new season has started since you left. Adding as a new player."));
            player.setExp(0);
            player.setLevel(0);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.getInventory().clear();
            player.setGameMode(GameMode.SURVIVAL);
            for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                player.removePotionEffect(potionEffect.getType());
            }
            player.teleport(Bukkit.getWorld(plugin.getHcWorldManager().getHCWorld(World.Environment.NORMAL)).getSpawnLocation());

            new BukkitRunnable() {
                @Override
                public void run() {
                    db.getWriter().addPlayer(new HCPlayer(uuid));
                }
            }.runTaskAsynchronously(plugin);
        }
        HCPlayer hcPlayer = db.getReader().getPlayer(uuid);
        if (hcPlayer == null) {
            plugin.getLogger().warning("Player is null, cannot add!");
        } else {
            hcPlayer.setTime(System.currentTimeMillis());
            plugin.addOnlinePlayer(hcPlayer);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!plugin.getHcWorldManager().isHardcoreWorld(event.getPlayer().getWorld().getName())) return;

        Player player = event.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                HCPlayer hcPlayer = plugin.getOnlinePlayer(player.getUniqueId());
                hcPlayer.updateTime();

                if (hcPlayer.getStatus() != HCPlayer.STATUS.DEAD) {
                    plugin.getDb().getWriter().updatePlayer(hcPlayer);
                }

                plugin.remOnlinePlayer(player.getUniqueId());
            }
        }.runTaskAsynchronously(plugin);
    }
}
