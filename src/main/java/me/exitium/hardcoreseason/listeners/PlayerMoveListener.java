package me.exitium.hardcoreseason.listeners;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.Utils;
import me.exitium.hardcoreseason.playerdata.HCPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class PlayerMoveListener implements Listener {
    private final HardcoreSeason plugin;

    public PlayerMoveListener(HardcoreSeason plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (event.hasChangedBlock() && plugin.isHardcoreWorld(player.getWorld().getName())) {
            HCPlayer hcPlayer = plugin.getHcPlayerService().getHardcorePlayer(uuid);
            if (hcPlayer != null && hcPlayer.getTeleportTaskID() != 0) {
                int taskID = hcPlayer.getTeleportTaskID();
                Bukkit.getScheduler().cancelTask(taskID);
                hcPlayer.setTeleportTaskID(0);
                player.sendMessage(Utils.chat("&cTeleporting cancelled. Please stand still to teleport."));
            }
        }
    }
}
