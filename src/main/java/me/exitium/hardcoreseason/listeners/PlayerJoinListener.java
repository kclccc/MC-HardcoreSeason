package me.exitium.hardcoreseason.listeners;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.Utils;
import me.exitium.hardcoreseason.database.DatabaseManager;
import me.exitium.hardcoreseason.player.HCPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public record PlayerJoinListener(HardcoreSeason plugin) implements Listener {

//    @EventHandler
//    public void onPlayerJoin(PlayerJoinEvent event) {
//        Player player = event.getPlayer();
//        if (plugin.getHcWorldManager().isHardcoreWorld(player.getWorld().getName())) {
//            plugin.getLogger().info("Player joined HC World!");
//
//            DatabaseManager db = plugin.getDb();
//            if (db.getReader().hcPlayerExists(player.getUniqueId())) {
//                plugin.getLogger().info("HC Player exists!");
//            }
//
//            HCPlayer hcPlayer = new HCPlayer(player.getUniqueId());
//            db.getWriter().updatePlayer(hcPlayer);
//            plugin.addOnlinePlayer(hcPlayer);
//        }
//    }


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

            Utils.saveInventoryToFile(plugin, player);
            player.sendMessage(Component.text("A new season has begun since you left, Adding as new player!", NamedTextColor.RED));
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

            HCPlayer hcPlayer = new HCPlayer(uuid);
            hcPlayer.updateTime();
            plugin.addOnlinePlayer(hcPlayer);

            new BukkitRunnable() {
                @Override
                public void run() {
                    db.getWriter().addPlayer(hcPlayer);
                }
            }.runTaskAsynchronously(plugin);
            return;
        }

        HCPlayer hcPlayer = db.getReader().getPlayer(uuid, plugin.getSeasonNumber());
        if (hcPlayer == null) {
            plugin.getLogger().warning("HCPLAYER returned NULL from database!");
            player.sendMessage(Component.text("ERR: HCPLAYER returned NULL from database!"));
            return;
        }

        if (hcPlayer.getStatus() == HCPlayer.STATUS.DEAD) {
            player.sendMessage(Component.text("You've already ", NamedTextColor.GRAY)
                    .append(Component.text("died ", NamedTextColor.RED))
                    .append(Component.text("this season! Switching to spectator mode.", NamedTextColor.GRAY)));
            player.setGameMode(GameMode.SPECTATOR);
        } else {
            hcPlayer.setTimeCounter(System.currentTimeMillis());
            plugin.addOnlinePlayer(hcPlayer);
        }
    }
}
