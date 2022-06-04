package me.exitium.hardcoreseason.commands;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.database.DatabaseManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record EnterHardcoreCommand(HardcoreSeason plugin) implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)){
            sender.sendMessage("Command cannot be run from console!");
        } else {
            // TODO: check if they're using this command from an HC world already

            DatabaseManager db = plugin.getDb();
            if(db.getReader().hcPlayerExists(player.getUniqueId())) {
                // TODO: They exist, pull from SQL insert into OnlinePlayers map

                return true;
            }

            // TODO: They don't exist yet, create a new player.


        }
        return false;
    }
}
