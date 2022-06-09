package me.exitium.hardcoreseason.commands;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.Utils;
import me.exitium.hardcoreseason.player.HCPlayer;
import org.bukkit.Bukkit;
import org.bukkit.World;
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
            if (plugin.getHcWorldManager().isHardcoreWorld(player.getWorld().getName())) {
                player.sendMessage(Utils.colorize("&7You are already in an HC world!"));
                return false;
            }

            if (plugin.getDb().getReader().hcPlayerExists(player.getUniqueId())) {
                plugin.getLogger().info("HC Player exists!");
                HCPlayer hcPlayer = plugin.getDb().getReader().getPlayer(player.getUniqueId());
                plugin.addOnlinePlayer(hcPlayer);
            } else {
                HCPlayer hcPlayer = new HCPlayer(player.getUniqueId());
                plugin.getDb().getWriter().updatePlayer(hcPlayer);
                plugin.addOnlinePlayer(hcPlayer);
            }

            World hcWorld = Bukkit.getWorld(plugin.getHcWorldManager().getHCWorld(World.Environment.NORMAL));
            if (hcWorld == null) {
                plugin.getLogger().warning("Hardcore world was NULL!");
                player.sendMessage(Utils.colorize("&cCould not teleport you to the hardcore world!"));
                return false;
            }
            player.teleport(hcWorld.getSpawnLocation());
        }
        return true;
    }
}
