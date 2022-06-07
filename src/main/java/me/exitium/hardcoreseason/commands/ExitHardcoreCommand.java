package me.exitium.hardcoreseason.commands;

import me.exitium.hardcoreseason.HardcoreSeason;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record ExitHardcoreCommand(HardcoreSeason plugin) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command cannot be run from the console!");
        } else {
            plugin.remOnlinePlayer(player.getUniqueId());
            player.teleport(Bukkit.getWorld("world").getSpawnLocation());


            // TODO: Defer teleportation to a movement check to prevent 'chicken' teleports
        }

        return false;
    }
}
