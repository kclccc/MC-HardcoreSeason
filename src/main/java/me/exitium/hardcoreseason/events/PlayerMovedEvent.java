package me.exitium.hardcoreseason.events;

import me.exitium.hardcoreseason.HardcoreSeason;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public record PlayerMovedEvent(HardcoreSeason plugin) implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        int taskID = plugin.getTeleportingPlayer(e.getPlayer().getUniqueId());

        plugin.getLogger().warning("MOVED: " + taskID);
        
        if (taskID != 0 && e.hasChangedPosition()) {
            Bukkit.getScheduler().cancelTask(taskID);
        }
    }
}
