package me.exitium.hardcoreseason.commands;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.Utils;
import me.exitium.hardcoreseason.database.DatabaseManager;
import me.exitium.hardcoreseason.player.HCPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record EnterHardcoreCommand(HardcoreSeason plugin) implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Command cannot be run from console!");
        } else {
            if (!plugin.getHcWorldManager().isHardcoreWorld(player.getWorld().getName())) {
                player.sendMessage(Utils.colorize("&7You are already in an HC world!"));
            }

            DatabaseManager db = plugin.getDb();
            if (db.getReader().hcPlayerExists(player.getUniqueId())) {
                // TODO: They exist, pull from SQL insert into OnlinePlayers map
                plugin.getLogger().info("HC Player exists!");
                return true;
            }

            plugin.getLogger().info("New player");

            HCPlayer hcPlayer = new HCPlayer(player.getUniqueId());
            db.getWriter().addPlayer(hcPlayer);
            plugin.addOnlinePlayer(hcPlayer);

            player.teleport(Bukkit.getWorld("hardcore").getSpawnLocation());
        }
        return false;
    }
}
