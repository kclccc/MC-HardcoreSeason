package me.exitium.hardcoreseason.listeners;

import me.exitium.hardcoreseason.HardcoreSeason;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public record PlayerQuitListener(HardcoreSeason plugin) implements Listener {

    @EventHandler
    public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (plugin.getHcWorldManager().isHardcoreWorld(player.getWorld().getName())) {
            plugin.remOnlinePlayer(player.getUniqueId());
        }
    }
}
