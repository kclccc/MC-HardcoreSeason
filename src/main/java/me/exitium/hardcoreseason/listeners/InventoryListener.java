package me.exitium.hardcoreseason.listeners;

import me.exitium.hardcoreseason.HardcoreSeason;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class InventoryListener implements Listener {
    private final HardcoreSeason plugin;

    public InventoryListener(HardcoreSeason plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().title().equals(Component.text("Hardcore Season"))) {
            if (event.getCurrentItem() == null) return;

            Inventory inventory = event.getClickedInventory();
            ItemStack item = inventory.getItem(event.getSlot());

            if (item != null) {
                if (item.getType().equals(Material.PLAYER_HEAD)) {
                    SkullMeta skullItem = (SkullMeta) item.getItemMeta();
                    OfflinePlayer destinationPlayer = skullItem.getOwningPlayer();
                    Player player = (Player) event.getWhoClicked();

                    assert destinationPlayer != null;
                    teleportPlayer(player, destinationPlayer);
                }
            }
            event.setCancelled(true);
        }
    }

    private void teleportPlayer(Player player, OfflinePlayer destinationPlayer) {
        player.teleport(destinationPlayer.getPlayer().getLocation());
    }
}