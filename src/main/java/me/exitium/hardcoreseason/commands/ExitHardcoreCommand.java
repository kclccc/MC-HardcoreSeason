package me.exitium.hardcoreseason.commands;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.Utils;
import me.exitium.hardcoreseason.player.HCPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public record ExitHardcoreCommand(HardcoreSeason plugin) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command cannot be run from the console!");
        } else {

            if (!plugin.getHcWorldManager().isHardcoreWorld(player.getWorld().getName())) {
                player.sendMessage(Utils.colorize("&7You must be in a hardcore world to use this command."));
                return true;
            }
            player.sendMessage(Utils.colorize("&7Teleporting... Do not move for 5 seconds!"));
            UUID uuid = player.getUniqueId();
            HCPlayer hcPlayer = plugin.getOnlinePlayer(uuid);

            if (hcPlayer == null) {
                hcPlayer = plugin.getDb().getReader().getPlayer(uuid);
                if (hcPlayer == null) {
                    plugin.getLogger().severe("NULL player from database but was in HARDCORE!");
                    player.sendMessage("NULL player from database, returning to softcore world!");
                    player.teleport(plugin.getHcWorldManager().getSoftcoreWorld().getSpawnLocation());
                    return true;
                }
            }

            if (hcPlayer.getStatus().equals(HCPlayer.STATUS.DEAD)) {
                player.teleport(Utils.processLocationString(
                        plugin.getHcWorldManager().getSoftcoreWorld(), hcPlayer.getReturnLocation()));
                player.sendMessage(Component.text("Thanks for playing, better luck next time!", NamedTextColor.GRAY));
                return true;
            }

            HCPlayer finalHcPlayer = hcPlayer;
            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    // Check if player has a reward Shulker Box from killing the dragon
                    // and send it with it's contents to the Softcore World.
                    validateArtifact(uuid);

                    finalHcPlayer.updateTime();

                    String returnLocation = finalHcPlayer.getReturnLocation();

                    if (returnLocation != null) {
                        Location tempLocation = Utils.processLocationString(
                                plugin.getHcWorldManager().getSoftcoreWorld(), returnLocation);
                        player.teleport(tempLocation);
                    } else {
                        player.teleport(plugin.getHcWorldManager().getSoftcoreWorld().getSpawnLocation());
                    }

                    if (finalHcPlayer.getShulkerInventory() != null) giveCardboardBox(uuid);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            plugin.getDb().getWriter().updatePlayer(finalHcPlayer);
                        }
                    }.runTaskAsynchronously(plugin);

                    // Sync player data and remove them from any lists
                    player.sendMessage(Utils.colorize("&7You have left the &4Hardcore &7world"));
                    plugin.remTeleportingPlayer(uuid);
                    plugin.remOnlinePlayer(uuid);
                }
            }.runTaskLater(plugin, 20L * plugin.getTeleportCooldown());

            plugin.addTeleportingPlayer(uuid, task.getTaskId());
        }
        return true;
    }

    private void validateArtifact(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        Inventory pInv = player.getInventory();
        ItemStack[] pInvContents = player.getInventory().getContents();
        NamespacedKey key = new NamespacedKey(plugin, "hc-box");
        HCPlayer hcPlayer = plugin.getOnlinePlayer(uuid);

        Predicate<ItemStack> itemStackPredicate = itemStack ->
                itemStack != null
                        && itemStack.hasItemMeta()
                        && itemStack.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.INTEGER)
                        && itemStack.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER) == 69;

        List<ItemStack> shulkerList = Arrays.stream(pInvContents)
                .filter(itemStackPredicate)
                .toList();

        switch (shulkerList.size()) {
            case 0:
                break; // No reward boxes found
            case 1:
                BlockStateMeta bsm = (BlockStateMeta) shulkerList.get(0).getItemMeta();
                ShulkerBox shulker = (ShulkerBox) bsm.getBlockState();
                Inventory inv = Bukkit.createInventory(null, 27, Component.text("Shulker"));
                inv.setContents(shulker.getInventory().getContents());
                hcPlayer.setShulkerInventory(inv.getContents());
                pInv.remove(shulkerList.get(0));
                plugin.getLogger().info("Found reward box, saving inventory and removing");
                break;
            default:
                plugin.getLogger().info("More than 1 reward detected?");
                break;
        }
    }

    // Replace Shulker Box with a soggy variant after teleport -- Multi-Dimensional travel is not kind.
    private void giveCardboardBox(UUID uuid) {
        Player player = (Player) Bukkit.getOfflinePlayer(uuid);
        ItemStack is = new ItemStack(Material.BROWN_SHULKER_BOX);

        ItemStack[] shulkerInventory = plugin.getOnlinePlayer(uuid).getShulkerInventory();
        BlockStateMeta bsm = (BlockStateMeta) is.getItemMeta();
        ShulkerBox shulker = (ShulkerBox) bsm.getBlockState();
        shulker.getInventory().setContents(shulkerInventory);

        bsm.setBlockState(shulker);
        bsm.displayName().append(Component.text("Soggy cardboard box")).color(NamedTextColor.LIGHT_PURPLE);
//		bsm.setDisplayName(ChatColor.LIGHT_PURPLE + "Soggy Cardboard Box");
        bsm.lore().add(Component.text("... It smells bad"));
//		bsm.setLore(new ArrayList<>(Collections.singletonList("... It smells bad")));
        bsm.getPersistentDataContainer().set(new NamespacedKey(plugin, "hc-box"), PersistentDataType.INTEGER, 69);
        is.setItemMeta(bsm);
        shulker.update();

        player.getInventory().addItem(is);
        plugin.getOnlinePlayer(uuid).setShulkerInventory(null);
    }
}