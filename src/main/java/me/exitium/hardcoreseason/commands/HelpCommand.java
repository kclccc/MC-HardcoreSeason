package me.exitium.hardcoreseason.commands;

import me.exitium.hardcoreseason.HardcoreSeason;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record HelpCommand(HardcoreSeason plugin) implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Command cannot be run from console!");
            return true;
        }

        player.sendMessage(Component.text("", NamedTextColor.GRAY)
                .append(Component.text("Hardcore Season", NamedTextColor.DARK_RED))
                .append(Component.text(" is active! Join any time with "))
                .append(Component.text("/hcenter", NamedTextColor.AQUA))
                .append(Component.text(".\nYou only get "))
                .append(Component.text("ONE ", NamedTextColor.GOLD))
                .append(Component.text("life so make it count!\n"))
                .append(Component.text("If you do die, You can spectate players with "))
                .append(Component.text("/hcspec\n", NamedTextColor.AQUA))
                .append(Component.text("or you can specify a player with "))
                .append(Component.text("/hcspec <name>\n", NamedTextColor.AQUA))
                .append(Component.text("You may "))
                .append(Component.text("leave ", NamedTextColor.AQUA))
                .append(Component.text("any time with "))
                .append(Component.text("/hcexit", NamedTextColor.AQUA))
        );

        return true;
    }
}
