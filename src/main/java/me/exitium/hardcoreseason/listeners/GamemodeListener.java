package me.exitium.hardcoreseason.listeners;

import me.exitium.hardcoreseason.HardcoreSeason;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

public class GamemodeListener implements Listener {
    private final HardcoreSeason plugin;

    public GamemodeListener(HardcoreSeason plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        GameMode playerMode = player.getGameMode();
        World world = player.getWorld();

        if (plugin.isHardcoreWorld(world.getName())) {
            if (playerMode == GameMode.SPECTATOR && player.hasPermission("hardcoreseason.hasdied")) {
                event.setCancelled(true);
            }
        }
    }


}