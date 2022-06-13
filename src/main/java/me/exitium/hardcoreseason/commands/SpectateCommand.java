package me.exitium.hardcoreseason.commands;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.SpectatorGUI;
import me.exitium.hardcoreseason.player.HCPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record SpectateCommand(HardcoreSeason plugin) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Command cannot be run from console!");
            return true;
        } else {
            if (args.length > 0) {
                Player specPlayer = Bukkit.getPlayer(args[0]);
                if (specPlayer == null) {
                    player.sendMessage(Component.text("Player does not exist! \nUsage: /hcspec <player-name>", NamedTextColor.RED));
                    return true;
                }

                HCPlayer hcPlayer = plugin.getOnlinePlayer(specPlayer.getUniqueId());
                if (hcPlayer == null) {
                    player.sendMessage(Component.text("You can only spectate ", NamedTextColor.GRAY)
                            .append(Component.text("alive ", NamedTextColor.GREEN))
                            .append(Component.text("players! \nUsage: "))
                            .append(Component.text("/hcspec <player-name>", NamedTextColor.AQUA)));
                    return true;
                }

                player.sendMessage(Component.text("You are now spectating ", NamedTextColor.GRAY)
                        .append(Component.text(String.valueOf(specPlayer.displayName()), NamedTextColor.AQUA)));
                player.teleport(specPlayer.getLocation());
            }

            new SpectatorGUI(plugin).openMenu(player);
            return true;
        }
    }
}
