package me.exitium.hardcoreseason.listeners;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.playerdata.HCPlayer;
import me.exitium.hardcoreseason.playerdata.HCPlayerController;
import me.exitium.hardcoreseason.playerdata.HCPlayerManager;
import me.exitium.hardcoreseason.playerdata.statistics.StatisticsManager;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EntityDamageListener implements Listener {
    private final HardcoreSeason plugin;

    public EntityDamageListener(HardcoreSeason plugin) {
        this.plugin = plugin;
    }

    // -- Incomplete --
    // Track damage to see who helped during the fight
//    @EventHandler
//    private void onEntityDamage(EntityDamageEvent event){
//        Entity damagedEntity = event.getEntity();
//
//        String damageCause = String.valueOf(event.getCause());
//
//        double damageAmount = event.getDamage();
//        double finalDamage = event.getFinalDamage();
//
//        if(damagedEntity instanceof EnderDragon) {
//            System.out.printf("Amount: %f  -  Final: %f  -  Cause: %s", damageAmount, finalDamage, damageCause);
//            System.out.printf("Health: %f", ((EnderDragon) damagedEntity).getHealth());
//        }
//    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (plugin.isHardcoreWorld(event.getEntity().getWorld().getName())) {
            Entity damageSender = event.getDamager();
            Entity damageReceiver = event.getEntity();
            double damageAmount = event.getFinalDamage();
            boolean isPlayerShooter = damageSender instanceof Projectile && ((Projectile) damageSender).getShooter() instanceof Player;

            HCPlayerManager hcPlayerManager = plugin.getHcPlayerService();

            if (damageSender instanceof Player && !(damageReceiver instanceof Player)) {
                UUID damageSenderUUID = damageSender.getUniqueId();
                Material weapon = ((Player) damageSender).getInventory().getItemInMainHand().getType();
                new StatisticsManager(hcPlayerManager.getHardcorePlayer(damageSenderUUID).getStatistics()).updateDamageDealt(weapon, (int) damageAmount);
//                new HCPlayerController(hcPlayerManager.getHardcorePlayer(damageSenderUUID)).updateDamageDealt(weapon, (int) damageAmount);
            }
            if (damageReceiver instanceof Player && !(damageSender instanceof Player)) {
                UUID damageReceiverUUID = damageReceiver.getUniqueId();
                HCPlayerController hcPlayerController = new HCPlayerController(hcPlayerManager.getHardcorePlayer(damageReceiverUUID));
                StatisticsManager sm = new StatisticsManager(hcPlayerManager.getHardcorePlayer(damageReceiverUUID).getStatistics());

                if(damageSender instanceof Projectile) {
                    sm.updateDamageTaken(((Projectile) damageSender).getShooter().toString().replace("Craft", ""), (int) damageAmount);
//                    hcPlayerController.updateDamageTaken(((Projectile) damageSender).getShooter().toString().replace("Craft", ""), (int) damageAmount);
                } else {
                    sm.updateDamageTaken(damageSender.getName(), (int) damageAmount);
//                    hcPlayerController.updateDamageTaken(damageSender.getName(), (int) damageAmount);
                }
            }
            if (isPlayerShooter && !(damageReceiver instanceof Player)) {
                UUID playerShooterUUID = ((Player) ((Projectile) damageSender).getShooter()).getUniqueId();
                Material weapon = ((Player) ((Projectile) damageSender).getShooter()).getInventory().getItemInMainHand().getType();
                new StatisticsManager(hcPlayerManager.getHardcorePlayer(playerShooterUUID).getStatistics()).updateDamageDealt(weapon, (int) damageAmount);
//                new HCPlayerController(hcPlayerManager.getHardcorePlayer(playerShooterUUID)).updateDamageDealt(weapon, (int) damageAmount);
            }
            if(damageSender instanceof Player && damageReceiver instanceof Player){
                Material weapon = ((Player) damageSender).getInventory().getItemInMainHand().getType();
                new StatisticsManager(hcPlayerManager.getHardcorePlayer(damageSender.getUniqueId()).getStatistics()).updateDamageDealt(weapon, (int) damageAmount);
                new StatisticsManager(hcPlayerManager.getHardcorePlayer(damageReceiver.getUniqueId()).getStatistics()).updateDamageTaken("Friendly Fire", (int) damageAmount);
//                new HCPlayerController(hcPlayerManager.getHardcorePlayer(damageSender.getUniqueId())).updateDamageDealt(weapon, (int) damageAmount);
//                new HCPlayerController(hcPlayerManager.getHardcorePlayer(damageReceiver.getUniqueId())).updateDamageTaken("Friendly Fire", (int) damageAmount);
            }
        }
    }

    private final ImmutableSet<EntityDamageEvent.DamageCause> ENTITY_DAMAGE_CAUSES = Sets.immutableEnumSet(
            EntityDamageEvent.DamageCause.SUICIDE,
            EntityDamageEvent.DamageCause.ENTITY_ATTACK,
            EntityDamageEvent.DamageCause.ENTITY_EXPLOSION,
            EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK,
            EntityDamageEvent.DamageCause.PROJECTILE,
            EntityDamageEvent.DamageCause.MAGIC
    );

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        String worldName = entity.getWorld().getName();

        if (plugin.isHardcoreWorld(worldName)) {
            if (entity instanceof Player) {
                if (!ENTITY_DAMAGE_CAUSES.contains(event.getCause())) {
                    UUID uuid = event.getEntity().getUniqueId();
                    double finalDamage = event.getFinalDamage();

                    new StatisticsManager(plugin.getHcPlayerService().getHardcorePlayer(uuid).getStatistics()).updateDamageTaken(event.getCause().toString(), (int) finalDamage);
//                    new HCPlayerController(plugin.getHcPlayerService().getHardcorePlayer(uuid)).updateDamageTaken(event.getCause().toString(), (int) finalDamage);
                }
            }

            if ((worldName.equals(plugin.getHardcoreWorld(World.Environment.NETHER)) || worldName.equals(plugin.getHardcoreWorld(World.Environment.THE_END)))
                    && event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
                double finalDamage = event.getFinalDamage();

                List<Entity> entities = new ArrayList<>();
                entity.getNearbyEntities(7, 7, 7).stream().filter(e -> e instanceof Player).forEach(entities::add);

                for (Entity e : entities) {
                    processNetherBed(e, finalDamage);
                }
            }
        }
    }

    private void processNetherBed(Entity e, double finalDamage) {
        UUID uuid = e.getUniqueId();
        HCPlayerManager hcPlayerManager = plugin.getHcPlayerService();
        HCPlayer hcPlayer = hcPlayerManager.getHardcorePlayer(uuid);

        if (hcPlayerManager.getNetherBedUses(uuid) != null) {
            int possibleEntityCount = hcPlayerManager.getNetherBedUses(uuid).getSecond();

            if(possibleEntityCount > 0) {
//                plugin.getLogger().info("Processing possible damaged entity with damage: " + finalDamage + ", remaining count: " + possibleEntityCount);
                new StatisticsManager(hcPlayer.getStatistics()).updateDamageDealt(Material.BLACK_BED, (int) finalDamage);
//                new HCPlayerController(hcPlayer).updateDamageDealt(Material.BLACK_BED, (int) finalDamage);
                hcPlayerManager.getNetherBedUses(uuid).setSecond(possibleEntityCount - 1);
            } else if(possibleEntityCount == 0) {
                hcPlayer.setNetherBedLocation(null);
            } else {
                plugin.getLogger().info("Entity count less than 0.");
            }
        }
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player && plugin.isHardcoreWorld(event.getEntity().getWorld().getName())) {
            UUID uuid = player.getUniqueId();
            if (plugin.getHcPlayerService().getHardcorePlayer(uuid).getStatus() == HCPlayer.STATUS.DEAD) {
                event.setCancelled(true);
            }
        }
    }
}
