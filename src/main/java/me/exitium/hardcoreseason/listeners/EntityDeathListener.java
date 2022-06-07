package me.exitium.hardcoreseason.listeners;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.Utils;
import me.exitium.hardcoreseason.playerdata.HCPlayer;
import me.exitium.hardcoreseason.playerdata.HCPlayerController;
import me.exitium.hardcoreseason.playerdata.statistics.PlayerStatistics;
import me.exitium.hardcoreseason.playerdata.statistics.StatisticsManager;
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
        if (plugin.isHardcoreWorld(world)) {
            Player killer = event.getEntity().getKiller();
            if (killer != null) {
                PlayerStatistics playerStats = plugin.getHcPlayerService().getHardcorePlayer(killer.getUniqueId()).getStatistics();
                StatisticsManager sm = new StatisticsManager(plugin.getHcPlayerService().getHardcorePlayer(killer.getUniqueId()).getStatistics());
                sm.addMonsterKill(event.getEntity());
            }
        }

        // Track dragonkills
        if (event.getEntityType() == EntityType.ENDER_DRAGON) {
            Entity enderDragon = event.getEntity();
            if (Objects.equals(enderDragon.getLocation().getWorld().getName(), plugin.getHardcoreWorld(World.Environment.THE_END))) {
                Collection<Player> nearbyPlayers = enderDragon.getLocation().getNearbyPlayers(50,50,50);
//                nearbyPlayers.forEach(this::processKill);

                for(Player p : nearbyPlayers){
                    PlayerStatistics playerStats = plugin.getHcPlayerService().getHardcorePlayer(p.getUniqueId()).getStatistics();
                    StatisticsManager sm = new StatisticsManager(plugin.getHcPlayerService().getHardcorePlayer(p.getUniqueId()).getStatistics());
                    sm.addMonsterKill(enderDragon);
                    processKill(p);
                }
            }
        }
    }

    private void processKill(Entity entity) {
        Player player = (Player) entity;
        UUID uuid = player.getUniqueId();
        HCPlayer hcPlayer = plugin.getHcPlayerService().getHardcorePlayer(uuid);
        HCPlayerController hcPlayerController = new HCPlayerController(hcPlayer);
        hcPlayerController.processPlayerVictory();

        if(hcPlayerController.firstDragonKill()) {
//        if (hcPlayer.getDragonKill() != 1) {
            giveItem(uuid);
            itemRoll(uuid);
            player.sendMessage(Utils.chat("&6Victory!"));
        } else {
            player.sendMessage(Utils.chat("&7You've already been &6rewarded &7this season."));
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
