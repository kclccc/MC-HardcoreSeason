package me.exitium.hardcoreseason.commands;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.Utils;
import me.exitium.hardcoreseason.player.HCPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

public record ResetPlayerCommand(HardcoreSeason plugin) implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Command cannot be run from console!");
        } else {
            if (args.length == 0) {
                player.sendMessage(Component.text("Command requires a player name to reset! \n Usage: ", NamedTextColor.GRAY)
                        .append(Component.text("/hcreset <playername>", NamedTextColor.AQUA)));
                return true;
            }

            Player resetPlayer = Bukkit.getPlayer(args[0]);
            if (resetPlayer == null) {
                player.sendMessage(Component.text("Player was NULL, Did you type the name correctly? Players must be ", NamedTextColor.GRAY)
                        .append(Component.text("online ", NamedTextColor.GREEN))
                        .append(Component.text(" to be reset!")));
                return true;
            }

            String returnLoc = plugin.getOnlinePlayer(resetPlayer.getUniqueId()).getReturnLocation();

            HCPlayer hcPlayer = new HCPlayer(resetPlayer.getUniqueId());
            plugin.getDb().getWriter().updatePlayer(hcPlayer);
            plugin.addOnlinePlayer(hcPlayer);

            if (plugin.getHcWorldManager().isHardcoreWorld(resetPlayer.getWorld().getName())) {
                World hcWorld = Bukkit.getWorld(plugin.getHcWorldManager().getHCWorld(World.Environment.NORMAL));
                if (hcWorld != null) {
                    if (resetPlayer.teleport(hcWorld.getSpawnLocation())) {
                        Utils.saveInventoryToFile(plugin, resetPlayer);
                        resetPlayer.setExp(0);
                        resetPlayer.setLevel(0);
                        resetPlayer.setHealth(20);
                        resetPlayer.setFoodLevel(20);
                        resetPlayer.getInventory().clear();
                        plugin.remPermission(resetPlayer, "hardcoreseason.hasdied");
                        resetPlayer.setGameMode(GameMode.SURVIVAL);
                        for (PotionEffect potionEffect : resetPlayer.getActivePotionEffects()) {
                            resetPlayer.removePotionEffect(potionEffect.getType());
                        }
                    }
                    if (returnLoc == null) {
                        plugin.getOnlinePlayer(resetPlayer.getUniqueId())
                                .setReturnLocation(plugin.getHcWorldManager().getSoftcoreWorld().getSpawnLocation());
                    } else {
                        plugin.getOnlinePlayer(resetPlayer.getUniqueId()).setReturnLocation(Utils.processLocationString(returnLoc));
                    }
                }
            }

            player.sendMessage(Component.text("Player ", NamedTextColor.GRAY)
                    .append(Component.text(resetPlayer.getName(), NamedTextColor.AQUA))
                    .append(Component.text(" has been reset successfully!")));
            return true;
        }

        return false;
    }
}
