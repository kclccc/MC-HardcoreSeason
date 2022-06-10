package me.exitium.hardcoreseason.listeners;

import me.exitium.hardcoreseason.HardcoreSeason;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public record PlayerMoveListener(HardcoreSeason plugin) implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (!plugin.getHcWorldManager().isHardcoreWorld(player.getWorld().getName())) return;
        if (!plugin.isTeleportingPlayer(player.getUniqueId())) return;

        int taskID = plugin.getTeleportingPlayer(player.getUniqueId());

        if (taskID != 0 && e.hasChangedBlock()) {
            player.sendMessage(Component.text("Teleportation cancelled due to movement!", NamedTextColor.RED));
            plugin.remTeleportingPlayer(player.getUniqueId());
            Bukkit.getScheduler().cancelTask(taskID);
        }
    }
//    @EventHandler
//    public void onPlayerMove(PlayerMoveEvent event) {
//        Player player = event.getPlayer();
//        if (!(plugin.getHcWorldManager().isHardcoreWorld(player.getWorld().getName()))) return;
//
//        int taskID = plugin.getTeleportingPlayer(player.getUniqueId());
//        if (taskID != 0 && event.hasChangedPosition()) Bukkit.getScheduler().cancelTask(taskID);
//
//        plugin.getOnlinePlayer(player.getUniqueId()).setTeleportTaskID(0);
//        player.sendMessage(Utils.colorize("&cTeleporting cancelled. Please stand still to teleport."));
//    }
}
