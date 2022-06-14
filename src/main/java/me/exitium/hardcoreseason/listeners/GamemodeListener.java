package me.exitium.hardcoreseason.listeners;

import me.exitium.hardcoreseason.HardcoreSeason;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

public record GamemodeListener(HardcoreSeason plugin) implements Listener {

    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (!(plugin.getHcWorldManager().isHardcoreWorld(player.getWorld().getName()))) return;
        if (event.getNewGameMode() == GameMode.CREATIVE) return;

        if (player.getGameMode() == GameMode.SPECTATOR && player.hasPermission("hardcoreseason.hasdied")) {
            event.setCancelled(true);
        }
    }
}