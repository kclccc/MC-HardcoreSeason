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

        int difficulty = 0;
        if (args.length > 0) {
            try {
                difficulty = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                return false;
            }
        }

        if (plugin.getHcWorldManager().createAll(difficulty == 0 ? plugin.getDifficulty() : difficulty)) {
            sender.sendMessage("Hardcore Season " + plugin.getSeasonNumber() + " has started!");
            return true;
        }

        sender.sendMessage("Could not create a new Hardcore Season... Report this nonsense to exitium at once!");
        return false;
    }
}
