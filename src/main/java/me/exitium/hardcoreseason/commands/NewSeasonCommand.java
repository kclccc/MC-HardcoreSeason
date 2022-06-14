package me.exitium.hardcoreseason.commands;

import me.exitium.hardcoreseason.HardcoreSeason;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public record NewSeasonCommand(HardcoreSeason plugin) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage("Creating new Hardcore Season! Please wait while worlds are processed...");
        if (plugin.getHcWorldManager().createAll()) {
            plugin.incrementSeasonNumber();
            sender.sendMessage("Hardcore Season " + plugin.getSeasonNumber() + " has started!");
            return true;
        }

        sender.sendMessage("Could not create a new Hardcore Season... Report this nonsense to exitium at once!");
        return false;
    }
}
