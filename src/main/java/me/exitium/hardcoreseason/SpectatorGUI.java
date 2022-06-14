package me.exitium.hardcoreseason;

import me.exitium.hardcoreseason.player.HCPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public record SpectatorGUI(HardcoreSeason plugin) {

    public void openMenu(Player player) {
        Inventory invGui = Bukkit.createInventory(null, 18, Component.text("Hardcore Players"));

        for (UUID uuid : plugin.getAllOnlinePlayers()) {
            if (!plugin.getOnlinePlayer(uuid).getStatus().equals(HCPlayer.STATUS.DEAD))
                invGui.addItem(getPlayerHead(uuid));
        }

        player.openInventory(invGui);
    }

    private ItemStack getPlayerHead(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);

        if (player != null) {
            SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
            skullMeta.setOwningPlayer(player);

            playerHead.getItemMeta().displayName(Component.text(player.getName(), NamedTextColor.LIGHT_PURPLE));
            playerHead.setItemMeta(skullMeta);
        }
        return playerHead;
    }

}
