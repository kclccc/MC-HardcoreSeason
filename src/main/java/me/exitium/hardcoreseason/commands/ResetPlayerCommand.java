package me.exitium.hardcoreseason.commands;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.player.HCPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record ResetPlayerCommand(HardcoreSeason plugin) implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage("Command cannot be run from console!");
        } else {
            if(args.length == 0) {
                player.sendMessage(Component.text("Command requires a player name to reset! \n Usage: ", NamedTextColor.GRAY)
                        .append(Component.text("/hcreset <playername>", NamedTextColor.AQUA)));
                return true;
            }

            Player resetPlayer = Bukkit.getPlayer(args[0]);
            if(resetPlayer == null) {
                player.sendMessage(Component.text("Player was NULL, Did you type the name correctly? Players must be ", NamedTextColor.GRAY)
                        .append(Component.text("online ", NamedTextColor.GREEN))
                        .append(Component.text(" to be reset!")));
                return true;
            }

            plugin.getDb().getWriter().updatePlayer(new HCPlayer(resetPlayer.getUniqueId()));
            plugin.remOnlinePlayer(resetPlayer.getUniqueId());

        }

        return false;
    }
}
