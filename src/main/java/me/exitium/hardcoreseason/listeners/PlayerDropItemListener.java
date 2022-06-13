package me.exitium.hardcoreseason.listeners;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public record PlayerDropItemListener(HardcoreSeason plugin) implements Listener {

    @EventHandler
    public void onDropGoldIngot(PlayerDropItemEvent event) {
        if (!plugin.getHcWorldManager().isHardcoreWorld(event.getPlayer().getWorld().getName())) return;
        if (!event.getItemDrop().getItemStack().getType().equals(Material.GOLD_INGOT)) return;

        ItemStack itemStack = event.getItemDrop().getItemStack();
        NamespacedKey key = new NamespacedKey(plugin, "barter-uuid");
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.getPersistentDataContainer().set(key, new Utils.UUIDDataType(), event.getPlayer().getUniqueId());
        itemStack.setItemMeta(itemMeta);
    }
}
