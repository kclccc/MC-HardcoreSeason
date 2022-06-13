package me.exitium.hardcoreseason.listeners;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.player.HCPlayer;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public record PlayerTeleportListener(HardcoreSeason plugin) implements Listener {
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (!(plugin.getHcWorldManager().isHardcoreWorld(event.getFrom().getWorld().getName()))) return;
        if (plugin.getHcWorldManager().getSoftcoreWorld().equals(event.getTo().getWorld())) return;

        // Player joined the server in a hardcore world?
        if (event.getTo().getWorld().getName().equals(event.getFrom().getWorld().getName())) return;

        HCPlayer hcPlayer = plugin.getOnlinePlayer(event.getPlayer().getUniqueId());

        if (hcPlayer.getStatus().equals(HCPlayer.STATUS.DEAD)) return;

        String worldFrom = event.getFrom().getWorld().getName();
        String worldTo = event.getTo().getWorld().getName();

        String hcOverworld = plugin.getHcWorldManager().getHCWorld(World.Environment.NORMAL);
        String hcNether = plugin.getHcWorldManager().getHCWorld(World.Environment.NETHER);
        String hcEnd = plugin.getHcWorldManager().getHCWorld(World.Environment.THE_END);

        if (worldFrom.equals(hcOverworld) && worldTo.equals(hcNether)) {
            if (hcPlayer.getStatus().equals(HCPlayer.STATUS.ALIVE)) hcPlayer.setStatus(HCPlayer.STATUS.NETHER);
        }

        if (worldFrom.equals(hcOverworld) && worldTo.equals(hcEnd)) {
            if (hcPlayer.getStatus().equals(HCPlayer.STATUS.ALIVE) || hcPlayer.getStatus().equals(HCPlayer.STATUS.NETHER)) {
                hcPlayer.setStatus(HCPlayer.STATUS.END);
            }
        }
    }
}
