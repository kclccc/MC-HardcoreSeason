package me.exitium.hardcoreseason.listeners;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.Utils;
import me.exitium.hardcoreseason.player.HCPlayer;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record DeathRespawnListener(HardcoreSeason plugin) implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        World world = event.getEntity().getWorld();

        // If player dies in the HC world, log some info about it and set hasdied permission
        if (plugin.getHcWorldManager().isHardcoreWorld(world.getName())) {
            Player player = event.getEntity();

            plugin.setPermission(player, "hardcoreseason.hasdied");
            HCPlayer hcPlayer = plugin.getOnlinePlayer(player.getUniqueId());
            hcPlayer.updateTime();

            String deathMessage = processDeathMessage((TranslatableComponent) event.deathMessage(), player.getName());
            hcPlayer.killPlayer(deathMessage, player.getLocation());

            new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getDb().getWriter().updatePlayer(hcPlayer);
                }
            }.runTaskAsynchronously(plugin);

            new BukkitRunnable() {
                @Override
                public void run() {
                    player.spigot().respawn();
                }
            }.runTaskLater(plugin, 1);
        }
    }

    private String processDeathMessage(TranslatableComponent deathMsg, String playerName) {
        List<String> keys = new ArrayList<>();
        deathMsg.args().stream().forEach(arg -> {
            if (!(arg instanceof TranslatableComponent)) return;
            String keyString = ((TranslatableComponent) arg).key();
            plugin.getLogger().severe(keyString);
            keys.add(plugin.getEntityNames().getName(keyString));
        });
        return plugin.getDeathMessages().interpolate(deathMsg.key(), playerName, keys);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getHcWorldManager().isHardcoreWorld(player.getWorld().getName())) return;
        HCPlayer hcPlayer = plugin.getOnlinePlayer(player.getUniqueId());

        if (event.getRespawnFlags().containsAll(
                Arrays.asList(PlayerRespawnEvent.RespawnFlag.END_PORTAL, PlayerRespawnEvent.RespawnFlag.BED_SPAWN))) {
            String spawnLocation = hcPlayer.getBedLocation();
            World hcWorld = Bukkit.getWorld(plugin.getHcWorldManager().getHCWorld(World.Environment.NORMAL));
            Location respawnLoc = null;
            if (hcWorld != null) {
                respawnLoc = hcWorld.getSpawnLocation();
                if (spawnLocation != null) {
                    respawnLoc = Utils.processLocationString(spawnLocation);
                }

                event.setRespawnLocation(respawnLoc);
                player.sendMessage(Utils.colorize("&7Congrats!"));
            } else {
                plugin.getLogger().warning("Getting HCWORLD for environment NORMAL failed!");
            }
        }

        if (hcPlayer.getStatus() == HCPlayer.STATUS.DEAD) {
            event.setRespawnLocation(hcPlayer.getLastLocation());
            player.sendMessage(Utils.colorize("&7Better luck next time!"));

            new BukkitRunnable() {
                @Override
                public void run() {
                    player.setGameMode(GameMode.SPECTATOR);
                }
            }.runTaskLater(plugin, 1L);
        }
    }

    // Add players to a temporary list when they touch end-portal blocks from the end
    // onRespawn doesn't give enough information to detect this otherwise.
    @EventHandler
    public void onEndPortal(EntityPortalEnterEvent event) {
        if (event.getEntity() instanceof Player
                && event.getLocation().getBlock().getType() == Material.END_PORTAL
                && event.getLocation().getWorld().getName().equals(plugin.getHcWorldManager().getHCWorld(World.Environment.THE_END))) {
            plugin.getOnlinePlayer(event.getEntity().getUniqueId()).processVictory();
        }
    }
}