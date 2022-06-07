package me.exitium.hardcoreseason.listeners;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.player.HCPlayer;
import me.exitium.hardcoreseason.statistics.GenericStat;
import me.exitium.hardcoreseason.statistics.StatisticsHandler;
import org.apache.commons.lang.WordUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;

public record ConsumeItemListener(HardcoreSeason plugin) implements Listener {

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!plugin.getHcWorldManager().isHardcoreWorld(player.getWorld().getName())) return;
        if (event.getItem() != null) {
            HCPlayer hcPlayer = plugin.getOnlinePlayer(player.getUniqueId());
            String food = event.getItem().getType().toString().replace('_', ' ');
            hcPlayer.getStatistics().addStat(new GenericStat(WordUtils.capitalizeFully(food)), StatisticsHandler.STATTYPE.CONSUME_FOOD);
        }
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!plugin.getHcWorldManager().isHardcoreWorld(player.getWorld().getName())) return;
        if (event.getCause() == EntityPotionEffectEvent.Cause.POTION_DRINK) {
            HCPlayer hcPlayer = plugin.getOnlinePlayer(player.getUniqueId());
            String potion = event.getModifiedType().getName().replace('_', ' ');
            hcPlayer.getStatistics().addStat(new GenericStat(WordUtils.capitalizeFully(potion)), StatisticsHandler.STATTYPE.DRINK_POTION);
        }
    }

    @EventHandler
    public void onSplashPotionEffect(PotionSplashEvent event) {
        if (!(event.getPotion().getShooter() instanceof Player player)) return;
        if (!plugin.getHcWorldManager().isHardcoreWorld(player.getWorld().getName())) return;

        for (LivingEntity entity : event.getAffectedEntities()) {
            if (!(entity instanceof Player)) break;
            for (PotionEffect effect : event.getPotion().getEffects()) {
                HCPlayer hcPlayer = plugin.getOnlinePlayer(player.getUniqueId());
                String potion = effect.getType().getName().replace('_', ' ');
                hcPlayer.getStatistics().addStat(new GenericStat(WordUtils.capitalizeFully(potion)), StatisticsHandler.STATTYPE.DRINK_POTION);
            }
        }
    }
}
