package me.exitium.hardcoreseason.listeners;

import me.exitium.hardcoreseason.HardcoreSeason;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public record BlockListener(HardcoreSeason plugin) implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack placedItem = event.getItemInHand();
        Block placedBlock = event.getBlockPlaced();

        NamespacedKey key = new NamespacedKey(plugin, "hc-box");
        ItemMeta itemMeta = placedItem.getItemMeta();
        Location blockLoc = placedBlock.getLocation();

        // If the block they placed has our persistent data, save that location
        if (itemMeta.getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) {
            if (itemMeta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER) == 69) {
                plugin.getOnlinePlayer(event.getPlayer().getUniqueId()).setArtifactLocation(blockLoc);
            }
        }
    }

    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent event) {
        if (!event.getItems().isEmpty()) {
            ItemStack droppedItem = event.getItems().get(0).getItemStack();

            if (event.getBlockState() instanceof ShulkerBox) {
                Block brokenBlock = event.getBlock();
                NamespacedKey key = new NamespacedKey(plugin, "hc-box");
                Location blockLoc = brokenBlock.getLocation();
                UUID uuid = event.getPlayer().getUniqueId();

                // If they broke an artifact at a saved location, re-add the persistent data since it's lost on placement.
                if (plugin.getOnlinePlayer(uuid).getArtifactLocation().equals(blockLoc)) {
                    ItemStack artifact = ItemStack.deserialize(plugin.getRewardsConfig().getConfigurationSection("dragon_artifact").getValues(true));
                    ItemMeta im = droppedItem.getItemMeta();
                    im.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 69);
                    im.setLore(artifact.getItemMeta().getLore());
                    droppedItem.setItemMeta(im);

                    plugin.getOnlinePlayer(uuid).setArtifactLocation(null);
                }
            }
        }
    }
}