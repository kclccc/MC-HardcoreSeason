package me.exitium.hardcoreseason.listeners;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.database.DatabaseManager;
import me.exitium.hardcoreseason.player.HCPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public record PlayerJoinListener(HardcoreSeason plugin) implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (plugin.getHcWorldManager().isHardcoreWorld(player.getWorld().getName())) {
            plugin.getLogger().info("Player joined HC World!");

            DatabaseManager db = plugin.getDb();
            if (db.getReader().hcPlayerExists(player.getUniqueId())) {
                plugin.getLogger().info("HC Player exists!");
            }

            HCPlayer hcPlayer = new HCPlayer(player.getUniqueId());
            db.getWriter().updatePlayer(hcPlayer);
            plugin.addOnlinePlayer(hcPlayer);
        }
    }
}
