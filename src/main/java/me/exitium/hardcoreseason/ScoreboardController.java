package me.exitium.hardcoreseason;

import me.exitium.hardcoreseason.player.HCPlayer;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.List;
import java.util.UUID;

public class ScoreboardController {
    private final HardcoreSeason plugin;

    public ScoreboardController(HardcoreSeason plugin) {
        this.plugin = plugin;
    }

    public void createScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();

        int row = 1;
//        Objective o = scoreboard.registerNewObjective("player", "dummy", ChatColor.BOLD + Utils.chat("&4Hardcore Players"));
        Objective o = scoreboard.registerNewObjective(
                "player",
                "dummy",
                Component.text(ChatColor.BOLD + Utils.colorize("&4Hardcore Players")));

        List<UUID> hardcorePlayers = plugin.getDb().getReader().getAllPlayers();
        if(hardcorePlayers == null) return;
        for (UUID uuid : hardcorePlayers) {
            o.setDisplaySlot(DisplaySlot.SIDEBAR);

            HCPlayer hcPlayer = plugin.getOnlinePlayer(uuid);
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
        }

        player.setScoreboard(scoreboard);
    }

    public void removeScoreboard(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
}
