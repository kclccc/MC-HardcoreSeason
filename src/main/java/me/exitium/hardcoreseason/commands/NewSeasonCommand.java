package me.exitium.hardcoreseason.commands;

import me.exitium.hardcoreseason.HardcoreSeason;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record NewSeasonCommand(HardcoreSeason plugin) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        plugin.getHcWorldManager().createAll();
        return true;
    }
}
