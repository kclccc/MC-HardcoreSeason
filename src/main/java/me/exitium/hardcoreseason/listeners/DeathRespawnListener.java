package me.exitium.hardcoreseason.listeners;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.Utils;
import me.exitium.hardcoreseason.player.HCPlayer;
import me.exitium.hardcoreseason.playerdata.HCPlayerController;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class DeathRespawnListener implements Listener {
    private final HardcoreSeason plugin;

    public DeathRespawnListener(HardcoreSeason plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        World world = event.getEntity().getWorld();

        // If player dies in the HC world, log some info about it and set hasdied permission
        if (plugin.getHcWorldManager().isHardcoreWorld(world.getName())) {
            Player player = event.getEntity();
            ItemStack[] inventory = player.getInventory().getContents();

            HashMap<String, Integer> playerInventory = new HashMap<>();
            for (ItemStack item : inventory) {
                if (item != null) playerInventory.put(item.getType().toString(), item.getAmount());
            }
            plugin.setPermission(player, "hardcoreseason.hasdied");

            HCPlayer hcPlayer = plugin.getOnlinePlayer(player.getUniqueId());
            hcPlayer.killPlayer(String.valueOf(event.deathMessage()), player.getLocation());

            new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getDb().getWriter().updatePlayer(hcPlayer);
                }
            }.runTaskAsynchronously(plugin);

//            new BukkitRunnable() {
//                @Override
//                public void run() {
//                    player.spigot().respawn();
//                }
//            }.runTaskLater(plugin, 1);
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
//        System.out.println("TELEPORT EVENT: " + event.getCause());
        plugin.getLogger().info("TeleportEvent: " + event.getCause());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (plugin.isHardcoreWorld(player.getWorld().getName())) {
            UUID uuid = player.getUniqueId();
            HCPlayer hcPlayer = plugin.getHcPlayerService().getHardcorePlayer(uuid);

//            System.out.println("FLAGS: " + event.getRespawnFlags());
            plugin.getLogger().info("RespawnFlags: " + event.getRespawnFlags());
            if (event.getRespawnFlags().containsAll(Arrays.asList(PlayerRespawnEvent.RespawnFlag.END_PORTAL, PlayerRespawnEvent.RespawnFlag.BED_SPAWN))) {
                String spawnLocation = hcPlayer.getBedLocation();
                Location respawnLoc = Bukkit.getWorld(plugin.getHardcoreWorld(World.Environment.NORMAL)).getSpawnLocation();

                if (spawnLocation != null) {
                    respawnLoc = Utils.processLocationString(Bukkit.getWorld(plugin.getHardcoreWorld(World.Environment.NORMAL)), spawnLocation);
                }

                event.setRespawnLocation(respawnLoc);
                player.sendMessage(Utils.chat("&7Congrats!"));
            }

            if (hcPlayer.getStatus() == HCPlayer.STATUS.DEAD) {
                event.setRespawnLocation(hcPlayer.getLastLocation());
                player.sendMessage(Utils.chat("&7Better luck next time!"));

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.setGameMode(GameMode.SPECTATOR);
                    }
                }.runTaskLater(plugin, 1);
            }
        }
    }

    // Add players to a temporary list when they touch end-portal blocks from the end
    // onRespawn doesn't give enough information to detect this otherwise.
    @EventHandler
    public void onEndPortal(EntityPortalEnterEvent event) {
        if (event.getEntity() instanceof Player && event.getLocation().getBlock().getType() == Material.END_PORTAL && event.getLocation().getWorld().getName().equals(plugin.getHardcoreWorld(World.Environment.THE_END))) {
            UUID uuid = event.getEntity().getUniqueId();
            HCPlayerController hcPlayerController = new HCPlayerController(plugin.getHcPlayerService().getHardcorePlayer(uuid));
            hcPlayerController.processPlayerVictory();
        }
    }
}