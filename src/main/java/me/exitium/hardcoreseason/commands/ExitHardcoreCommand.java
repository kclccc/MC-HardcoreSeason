package me.exitium.hardcoreseason.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ExitHardcoreCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage("This command cannot be run from the console!");
        } else {
            // TODO: Check if they're in a hardcore world

            // TODO: Defer teleportation to a movement check to prevent 'chicken' teleports
        }

        return false;
    }
}
