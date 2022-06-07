package me.exitium.hardcoreseason.commands;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public record ExitHardcoreCommand(HardcoreSeason plugin) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command cannot be run from the console!");
        } else {

            if (!plugin.getHcWorldManager().isHardcoreWorld(player.getWorld().getName())) {
                player.sendMessage(Utils.colorize("&7You must be in a hardcore world to use this command."));
                return false;
            }
            player.sendMessage(Utils.colorize("&7Teleporting... Do not move for 5 seconds!"));

            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {

                }
            }.runTaskLater(plugin, 20L * plugin.getTeleportCooldown());
            plugin.remOnlinePlayer(player.getUniqueId());
            player.teleport(Bukkit.getWorld("world").getSpawnLocation());
        }
        return false;
    }
}
