package me.exitium.hardcoreseason.commands;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.Utils;
import me.exitium.hardcoreseason.player.HCPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public record EnterHardcoreCommand(HardcoreSeason plugin) implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Command cannot be run from console!");
        } else {
            if (plugin.getHcWorldManager().isHardcoreWorld(player.getWorld().getName())) {
                player.sendMessage(Utils.colorize("&7You are already in an HC world!"));
                return true;
            }

            if (!plugin.getHcWorldManager().getSoftcoreWorld().getName().equals(player.getWorld().getName())) {
                player.sendMessage(Component.text("You can only enter hardcore from the main overworld!"));
                return true;
            }

            HCPlayer hcPlayer;
            boolean isNewPlayer = false;

            if (plugin.getDb().getReader().hcPlayerExists(player.getUniqueId())) {
                hcPlayer = plugin.getDb().getReader().getPlayer(player.getUniqueId(), plugin().getSeasonNumber());
                if (hcPlayer == null) {
                    player.sendMessage(Component.text("HCPLAYER was null after database call!"));
                    return false;
                }

                if (hcPlayer.getStatus() == HCPlayer.STATUS.DEAD) {
                    player.sendMessage(Component.text("You've already ", NamedTextColor.GRAY)
                            .append(Component.text("died ", NamedTextColor.RED))
                            .append(Component.text("this season! Switching to spectator mode.", NamedTextColor.GRAY)));
                    player.setGameMode(GameMode.SPECTATOR);
                } else {
                    hcPlayer.setTimeCounter(System.currentTimeMillis());
                }

                plugin.addOnlinePlayer(hcPlayer);
            } else {
                isNewPlayer = true;
                hcPlayer = new HCPlayer(player.getUniqueId());
                hcPlayer.setTimeCounter(System.currentTimeMillis());
                plugin.addOnlinePlayer(hcPlayer);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        plugin.getDb().getWriter().updatePlayer(hcPlayer);
                    }
                }.runTaskAsynchronously(plugin);
            }

            World hcWorld = Bukkit.getWorld(plugin.getHcWorldManager().getHCWorld(World.Environment.NORMAL));
            if (hcWorld == null) {
                plugin.getLogger().warning("Hardcore world was NULL!");
                player.sendMessage(Utils.colorize("&cCould not teleport you to the hardcore world!"));
                return true;
            }

            if (hcPlayer.getBedLocation() != null) {
                Location playerBed = Utils.processLocationString(hcPlayer.getBedLocation());
                hcPlayer.setReturnLocation(player.getLocation());
                player.teleport(playerBed);
                return true;
            }
            hcPlayer.setReturnLocation(player.getLocation());
            if (player.teleport(hcWorld.getSpawnLocation())) {
                if (isNewPlayer) {
                    Utils.saveInventoryToFile(plugin, player);
                    player.setExp(0);
                    player.setLevel(0);
                    player.setHealth(20);
                    player.setFoodLevel(20);
                    player.getInventory().clear();
                    player.updateInventory();
                    player.setGameMode(GameMode.SURVIVAL);
                    for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                        player.removePotionEffect(potionEffect.getType());
                    }
                }
            }
        }
        return true;
    }
}
