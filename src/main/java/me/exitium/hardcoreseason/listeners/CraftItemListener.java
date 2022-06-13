package me.exitium.hardcoreseason.listeners;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.statistics.GenericStat;
import me.exitium.hardcoreseason.statistics.StatisticsHandler;
import org.apache.commons.text.WordUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public record CraftItemListener(HardcoreSeason plugin) implements Listener {

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if ((event.getWhoClicked() instanceof Player player)) {
            if (!(plugin.getHcWorldManager().isHardcoreWorld(player.getWorld().getName()))) return;

            ItemStack craftedItem = event.getRecipe().getResult();
            String itemString = craftedItem.getType().toString().replace('_', ' ');

            if (!event.getClick().isShiftClick()) {
                plugin.getOnlinePlayer(player.getUniqueId()).getStatistics().addStat(
                        new GenericStat(
                                WordUtils.capitalizeFully(itemString),
                                craftedItem.getAmount()), StatisticsHandler.STATTYPE.ITEM_CRAFTED);
            } else {
                HashMap<Integer, ItemStack> heldItems = new HashMap<>(player.getInventory().all(craftedItem.getType()));
                int heldCount = 0;

                for (Map.Entry<Integer, ItemStack> entry : heldItems.entrySet()) {
                    heldCount += entry.getValue().getAmount();
                }
                plugin.getServer().getScheduler().runTaskLater(plugin, new CraftTaskUpdate(heldCount, player, craftedItem.getType()), 1L);
            }
        }
    }

    private class CraftTaskUpdate implements Runnable {
        Player player;
        int before;
        Material craftedItem;

        CraftTaskUpdate(int currentAmount, Player player, Material craftedItem) {
            this.craftedItem = craftedItem;
            before = currentAmount;
            this.player = player;
        }

        public void run() {
            HashMap<Integer, ItemStack> heldItems = new HashMap<>(player.getInventory().all(craftedItem));
            int heldCount = 0;

            for (Map.Entry<Integer, ItemStack> entry : heldItems.entrySet()) {
                heldCount += entry.getValue().getAmount();
            }

            int newCount = heldCount - before;
            String itemString = craftedItem.toString().replace('_', ' ');

            plugin.getOnlinePlayer(player.getUniqueId()).getStatistics().addStat(
                    new GenericStat(
                            WordUtils.capitalizeFully(itemString),
                            newCount), StatisticsHandler.STATTYPE.ITEM_CRAFTED);
        }
    }
}
