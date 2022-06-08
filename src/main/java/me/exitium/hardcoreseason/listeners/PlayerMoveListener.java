package me.exitium.hardcoreseason.listeners;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {
    private final HardcoreSeason plugin;

    public PlayerMoveListener(HardcoreSeason plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!(plugin.getHcWorldManager().isHardcoreWorld(player.getWorld().getName()))) return;

        int taskID = plugin.getTeleportingPlayer(player.getUniqueId());
        if (taskID != 0 && event.hasChangedPosition()) Bukkit.getScheduler().cancelTask(taskID);

        plugin.getOnlinePlayer(player.getUniqueId()).setTeleportTaskID(0);
        player.sendMessage(Utils.colorize("&cTeleporting cancelled. Please stand still to teleport."));
    }
}
