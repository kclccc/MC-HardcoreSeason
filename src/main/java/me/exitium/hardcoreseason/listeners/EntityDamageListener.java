package me.exitium.hardcoreseason.listeners;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.player.HCPlayer;
import me.exitium.hardcoreseason.statistics.GenericStat;
import me.exitium.hardcoreseason.statistics.StatisticsHandler;
import org.apache.commons.text.WordUtils;
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

    private final ImmutableSet<Material> WEAPON_TYPES = Sets.immutableEnumSet(
            Material.WOODEN_SWORD,
            Material.WOODEN_AXE,
            Material.WOODEN_HOE,
            Material.WOODEN_PICKAXE,
            Material.WOODEN_SHOVEL,
            Material.STONE_SWORD,
            Material.STONE_AXE,
            Material.STONE_HOE,
            Material.STONE_PICKAXE,
            Material.STONE_SHOVEL,
            Material.IRON_SWORD,
            Material.IRON_AXE,
            Material.IRON_HOE,
            Material.IRON_PICKAXE,
            Material.IRON_SHOVEL,
            Material.GOLDEN_SWORD,
            Material.GOLDEN_AXE,
            Material.GOLDEN_HOE,
            Material.GOLDEN_SHOVEL,
            Material.GOLDEN_PICKAXE,
            Material.DIAMOND_SWORD,
            Material.DIAMOND_AXE,
            Material.DIAMOND_HOE,
            Material.DIAMOND_SHOVEL,
            Material.DIAMOND_PICKAXE,
            Material.NETHERITE_SWORD,
            Material.NETHERITE_AXE,
            Material.NETHERITE_HOE,
            Material.NETHERITE_SHOVEL,
            Material.NETHERITE_PICKAXE,
            Material.BOW,
            Material.CROSSBOW,
            Material.SNOWBALL,
            Material.SPLASH_POTION,
            Material.BLACK_BED
    );

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
        if (!(plugin.getHcWorldManager().isHardcoreWorld(event.getEntity().getWorld().getName()))) return;
        Entity damageSender = event.getDamager();
        Entity damageReceiver = event.getEntity();
        double damageAmount = event.getFinalDamage();
        boolean isPlayerShooter = damageSender instanceof Projectile && ((Projectile) damageSender).getShooter() instanceof Player;

        // Player damages non-player
        if (damageSender instanceof Player && !(damageReceiver instanceof Player)) {
            Material weapon = ((Player) damageSender).getInventory().getItemInMainHand().getType();
            String weaponName = weapon.name().replace('_', ' ');

            if (!WEAPON_TYPES.contains(weapon)) weaponName = "Fist";
            plugin.getOnlinePlayer(damageSender.getUniqueId()).getStatistics().addStat(
                    new GenericStat(WordUtils.capitalizeFully(weaponName), (int) damageAmount),
                    StatisticsHandler.STATTYPE.DAMAGE_DEALT);
        }

        // Player receives damage from non-player
        if (damageReceiver instanceof Player && !(damageSender instanceof Player)) {
            HCPlayer hcPlayer = plugin.getOnlinePlayer(damageReceiver.getUniqueId());

            if (damageSender instanceof Projectile && ((Projectile) damageSender).getShooter() != null) {
                hcPlayer.getStatistics().addStat(new GenericStat(
                                ((Projectile) damageSender).getShooter().toString().replace("Craft", ""), (int) damageAmount),
                        StatisticsHandler.STATTYPE.DAMAGE_TAKEN);
            } else {
                hcPlayer.getStatistics().addStat(new GenericStat(
                                damageSender.getName(), (int) damageAmount),
                        StatisticsHandler.STATTYPE.DAMAGE_TAKEN);
            }
        }

        // Player shoots a projectile at a non-player
        if (isPlayerShooter && !(damageReceiver instanceof Player)) {
            Material weapon = ((Player) ((Projectile) damageSender).getShooter()).getInventory().getItemInMainHand().getType();
            String weaponName = weapon.name().replace('_', ' ');

            if (!WEAPON_TYPES.contains(weapon)) weaponName = "Fist";
            plugin.getOnlinePlayer(((Player) ((Projectile) damageSender).getShooter()).getUniqueId()).getStatistics().addStat(
                    new GenericStat(WordUtils.capitalizeFully(weaponName), (int) damageAmount),
                    StatisticsHandler.STATTYPE.DAMAGE_DEALT);
        }

        // Player damages another player
        if (damageSender instanceof Player && damageReceiver instanceof Player) {
            Material weapon = ((Player) damageSender).getInventory().getItemInMainHand().getType();
            String weaponName = weapon.name().replace('_', ' ');

            if (!WEAPON_TYPES.contains(weapon)) weaponName = "Fist";
            HCPlayer attackingPlayer = plugin.getOnlinePlayer(damageSender.getUniqueId());
            HCPlayer receivingPlayer = plugin.getOnlinePlayer(damageReceiver.getUniqueId());

            attackingPlayer.getStatistics().addStat(new GenericStat(
                            WordUtils.capitalizeFully(weaponName), (int) damageAmount),
                    StatisticsHandler.STATTYPE.DAMAGE_DEALT);

            receivingPlayer.getStatistics().addStat(new GenericStat(
                            "Friendly Fire", (int) damageAmount),
                    StatisticsHandler.STATTYPE.DAMAGE_TAKEN);
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

        if (!(plugin.getHcWorldManager().isHardcoreWorld(worldName))) return;
        if (!(entity instanceof Player)) return;
        if (!ENTITY_DAMAGE_CAUSES.contains(event.getCause())) {
            double finalDamage = event.getFinalDamage();

            plugin.getOnlinePlayer(event.getEntity().getUniqueId()).getStatistics().addStat(
                    new GenericStat(event.getCause().toString(), (int) event.getFinalDamage()),
                    StatisticsHandler.STATTYPE.DAMAGE_TAKEN);
        }

        if ((worldName.equals(plugin.getHcWorldManager().getHCWorld(World.Environment.NETHER)) || worldName.equals(plugin.getHcWorldManager().getHCWorld(World.Environment.THE_END)))
                && event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
            List<Entity> entities = new ArrayList<>();
            entity.getNearbyEntities(7, 7, 7).stream().filter(e -> e instanceof Player).forEach(entities::add);

            for (Entity e : entities) {
                processNetherBed(e, event.getFinalDamage());
            }
        }

    }

    private void processNetherBed(Entity e, double finalDamage) {
        UUID uuid = e.getUniqueId();
        HCPlayer hcPlayer = plugin.getOnlinePlayer(uuid);

        if (hcPlayer.getNetherBedLocation() != null) {
            int possibleEntityCount = hcPlayer.getNetherBedLocation().getSecond();

            if (possibleEntityCount > 0) {
                hcPlayer.getStatistics().addStat(new GenericStat(
                                "Bed", (int) finalDamage),
                        StatisticsHandler.STATTYPE.DAMAGE_DEALT);

                hcPlayer.getNetherBedLocation().setSecond(possibleEntityCount - 1);
            } else if (possibleEntityCount == 0) {
                hcPlayer.setNetherBedLocation(null);
            } else {
                plugin.getLogger().warning("Entity count less than 0.");
            }
        }
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!(plugin.getHcWorldManager().isHardcoreWorld(event.getEntity().getWorld().getName()))) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if (plugin.getOnlinePlayer(player.getUniqueId()).getStatus() == HCPlayer.STATUS.DEAD) {
            event.setCancelled(true);
        }
    }
}
