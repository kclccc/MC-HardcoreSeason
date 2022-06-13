package me.exitium.hardcoreseason.commands;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.ScoreboardController;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public record ShowScoreboardCommand(HardcoreSeason plugin) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Command cannot be run from console.");
            return false;
        } else {
            int seasonNumber = plugin.getSeasonNumber();
            if (args.length > 0) {
                try {
                    seasonNumber = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    player.sendMessage(Component.text("Usage: /hclist <season-number>"));
                    return true;
                }
            }

            ScoreboardController sb = new ScoreboardController(plugin);
            sb.createScoreboard(player, seasonNumber);

            new BukkitRunnable() {
                @Override
                public void run() {
                    sb.removeScoreboard(player);
                }
            }.runTaskLater(plugin, 20L * 10);
            return true;
        }
    }
}
