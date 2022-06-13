package me.exitium.hardcoreseason.listeners;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.player.HCPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public record PlayerQuitListener(HardcoreSeason plugin) implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!plugin.getHcWorldManager().isHardcoreWorld(event.getPlayer().getWorld().getName())) return;

        Player player = event.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                HCPlayer hcPlayer = plugin.getOnlinePlayer(player.getUniqueId());

                if (hcPlayer.getStatus() != HCPlayer.STATUS.DEAD) {
                    hcPlayer.updateTime();
                    plugin.getDb().getWriter().updatePlayer(hcPlayer);
                }
                plugin.remOnlinePlayer(player.getUniqueId());
            }
        }.runTaskAsynchronously(plugin);
    }
}
