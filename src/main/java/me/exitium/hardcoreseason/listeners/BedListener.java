package me.exitium.hardcoreseason.listeners;

import me.exitium.hardcoreseason.HardcoreSeason;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
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
            player.sendMessage(Component.text("Sleeping is disabled in hardcore!", NamedTextColor.RED));

            final TextComponent textComponent = Component.text("Spawn set to x: ", NamedTextColor.GRAY)
                    .append(Component.text(bedLocation.getBlockX(), NamedTextColor.GOLD))
                    .append(Component.text(", y: ", NamedTextColor.GRAY))
                    .append(Component.text(bedLocation.getBlockY(), NamedTextColor.GOLD))
                    .append(Component.text(", z: ", NamedTextColor.GRAY))
                    .append(Component.text(bedLocation.getBlockZ(), NamedTextColor.GOLD));

            player.sendMessage(textComponent);

            String bedLoc = String.format("%s:%d:%d:%d", bedLocation.getWorld().getName(), bedLocation.getBlockX(), bedLocation.getBlockY(), bedLocation.getBlockZ());
            plugin.getOnlinePlayer(uuid).setBedLocation(bedLoc);
        }
    }
}
