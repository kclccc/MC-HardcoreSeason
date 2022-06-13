package me.exitium.hardcoreseason.listeners;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public record PlayerPickupItemListener(HardcoreSeason plugin) implements Listener {

    @EventHandler
    public void onPlayerPickupGoldIngot(PlayerAttemptPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();
        if (!item.getType().equals(Material.GOLD_INGOT)) return;

        NamespacedKey key = new NamespacedKey(plugin, "barter-uuid");
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        if (container.has(key, new Utils.UUIDDataType())) {
            container.remove(key);
            item.setItemMeta(itemMeta);
        }
    }
}
