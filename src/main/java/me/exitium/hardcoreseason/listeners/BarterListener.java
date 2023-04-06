package me.exitium.hardcoreseason.listeners;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.Utils;
import me.exitium.hardcoreseason.statistics.GenericStat;
import me.exitium.hardcoreseason.statistics.StatisticsHandler;
import org.apache.commons.text.WordUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.List;
import java.util.UUID;

public record BarterListener(HardcoreSeason plugin) implements Listener {
    @EventHandler
    public void onBarter(PiglinBarterEvent event) {
        if (!(plugin.getHcWorldManager().isHardcoreWorld(event.getEntity().getWorld().getName()))) return;
        ItemStack input = event.getInput();
        List<ItemStack> output = event.getOutcome();

        NamespacedKey key = new NamespacedKey(plugin, "barter-uuid");
        ItemMeta itemMeta = input.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        if (!container.has(key, new Utils.UUIDDataType())) return;
        UUID uuid = container.get(key, new Utils.UUIDDataType());

        plugin.getOnlinePlayer(uuid).getStatistics().addStat(
                new GenericStat("Gold Ingot", 1),
                StatisticsHandler.STATTYPE.TRADES_MADE
        );

        for (ItemStack item : output) {
            String itemString = item.getType().toString().replace('_', ' ');

            plugin.getOnlinePlayer(uuid).getStatistics().addStat(
                    new GenericStat(WordUtils.capitalizeFully(itemString), item.getAmount()),
                    StatisticsHandler.STATTYPE.TRADES_MADE
            );
        }
    }
}
