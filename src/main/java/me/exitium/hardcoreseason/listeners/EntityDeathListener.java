package me.exitium.hardcoreseason.listeners;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.Utils;
import me.exitium.hardcoreseason.player.HCPlayer;
import me.exitium.hardcoreseason.statistics.GenericStat;
import me.exitium.hardcoreseason.statistics.StatisticsHandler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class EntityDeathListener implements Listener {
    private final HardcoreSeason plugin;

    public EntityDeathListener(HardcoreSeason plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        String world = event.getEntity().getWorld().getName();
        if (!(plugin.getHcWorldManager().isHardcoreWorld(world))) return;

        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            plugin.getOnlinePlayer(killer.getUniqueId()).getStatistics().addStat(new GenericStat(
                    event.getEntity().getName()), StatisticsHandler.STATTYPE.MOB_KILL);
        }

        // Track dragonkills
        if (event.getEntityType() == EntityType.ENDER_DRAGON) {
            Entity enderDragon = event.getEntity();
            if (Objects.equals(enderDragon.getLocation().getWorld().getName(), plugin.getHcWorldManager().getHCWorld(World.Environment.THE_END))) {
                Collection<Player> nearbyPlayers = enderDragon.getLocation().getNearbyPlayers(50, 50, 50);

                for (Player p : nearbyPlayers) {
                    plugin.getOnlinePlayer(p.getUniqueId()).getStatistics().addStat(
                            new GenericStat(enderDragon.getName()),
                            StatisticsHandler.STATTYPE.MOB_KILL);
                    processKill(p);
                }
            }
        }
    }

    private void processKill(Entity entity) {
        Player player = (Player) entity;
        UUID uuid = player.getUniqueId();
        HCPlayer hcPlayer = plugin.getOnlinePlayer(uuid);
        hcPlayer.processVictory();

        if (hcPlayer.getStatistics().isFirstDragonKill() == 0) {
            giveItem(uuid);
            itemRoll(uuid);
            player.sendMessage(Utils.colorize("&6Victory!"));
        } else {
            player.sendMessage(Utils.colorize("&7You've already been &6rewarded &7this season."));
        }
        System.out.printf("Player %s credited with dragon kill.%n", player.getName());
    }

    private void giveItem(UUID uuid) {
        Player player = (Player) Bukkit.getOfflinePlayer(uuid);
        ItemStack artifact = ItemStack.deserialize(plugin.getRewardsConfig().getConfigurationSection("dragon_artifact").getValues(true));
        player.getInventory().addItem(artifact);
    }
    
    private void itemRoll(UUID uuid) {
        Player player = (Player) Bukkit.getOfflinePlayer(uuid);
        FileConfiguration rewards = plugin.getRewardsConfig();
        ItemStack artifact = ItemStack.deserialize(rewards.getConfigurationSection("dragontooth_pickaxe").getValues(true));
        player.getInventory().addItem(artifact);
    }

}
