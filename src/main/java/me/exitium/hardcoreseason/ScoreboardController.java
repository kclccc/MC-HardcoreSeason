package me.exitium.hardcoreseason;

import me.exitium.hardcoreseason.player.HCPlayer;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;
import java.util.UUID;

public record ScoreboardController(HardcoreSeason plugin) {

    public void createScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        int row = 1;
        Objective o = scoreboard.registerNewObjective(
                "player",
                "dummy",
                Component.text(ChatColor.BOLD + Utils.colorize("&4Hardcore Players")));

        List<UUID> hardcorePlayers = plugin.getDb().getReader().getAllPlayers();
        if (hardcorePlayers == null) return;

        for (UUID uuid : hardcorePlayers) {
            o.setDisplaySlot(DisplaySlot.SIDEBAR);

            plugin.getLogger().warning(uuid.toString());

            HCPlayer hcPlayer = plugin.getDb().getReader().getPlayer(uuid);
            if (hcPlayer == null) {
                plugin.getLogger().warning("Player loaded from database returned NULL. Was the connection interrupted?");
                return;
            }
            String pStatus = switch (hcPlayer.getStatus()) {
                case ALIVE -> Utils.colorize("&aAlive");
                case DEAD -> Utils.colorize("&cDead");
                case VICTORY -> Utils.colorize("&dVictory");
            };

            String timeString = Utils.convertTime(hcPlayer.getTime());
            String pString = Utils.colorize(
                    StringUtils.rightPad(hcPlayer.getPlayerName(),
                            16 - hcPlayer.getPlayerName().length()) + " : " +
                            StringUtils.center(pStatus, 9) + " &f: " +
                            StringUtils.center(timeString, 6));
            Score playerRow = o.getScore(pString);
            playerRow.setScore(row);
            row++;
            player.setScoreboard(scoreboard);
        }
    }

    public void removeScoreboard(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
}
