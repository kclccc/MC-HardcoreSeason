package me.exitium.hardcoreseason.listeners;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.Utils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

import java.util.UUID;

public record BedListener(HardcoreSeason plugin) implements Listener {

    @EventHandler
    public void onSleep(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        // If we're in the hardcore overworld, disable sleeping (Phantoms cannot spawn), save an individual spawnpoint here.
        if (plugin.getHcWorldManager().isHardcoreWorld(world.getName())) {
            event.setCancelled(true);
            UUID uuid = player.getUniqueId();
            Location bedLocation = event.getBed().getLocation();
            player.sendMessage(Utils.colorize("&cSleeping is disabled in hardcore."));
            player.sendMessage(String.format(Utils.colorize("&7Spawn set to &7x:&6 %d, &7y:&6 %d, &7z:&6 %d"), bedLocation.getBlockX(), bedLocation.getBlockY(), bedLocation.getBlockZ()));
            plugin.getOnlinePlayer(uuid).setBedLocation(String.format("%d:%d:%d", bedLocation.getBlockX(), bedLocation.getBlockY(), bedLocation.getBlockZ()));
        }
    }
}
