package me.exitium.hardcoreseason.commands;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.ScoreboardController;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class ShowScoreboardCommand implements CommandExecutor {
    private final HardcoreSeason plugin;

    public ShowScoreboardCommand(HardcoreSeason plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Command cannot be run from console.");
            return false;
        } else {
            ScoreboardController sb = new ScoreboardController(plugin);
            sb.createScoreboard(player);

            new BukkitRunnable() {
                @Override
                public void run() {
                    sb.removeScoreboard(player);
                }
            }.runTaskLater(plugin, 10 * 20);
            return true;
        }
    }
}
