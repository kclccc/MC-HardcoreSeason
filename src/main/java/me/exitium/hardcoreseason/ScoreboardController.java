package me.exitium.hardcoreseason;

import me.exitium.hardcoreseason.player.HCPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;
import java.util.UUID;

public record ScoreboardController(HardcoreSeason plugin) {

    public void createScoreboard(Player player, int seasonNumber) {
        List<UUID> hardcorePlayers = plugin.getDb().getReader().getAllPlayers(seasonNumber);
        if (hardcorePlayers == null || (long) hardcorePlayers.size() == 0) {
            player.sendMessage(Component.text("Season doesn't exist or no players this season!", NamedTextColor.RED));
            return;
        }

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective o = scoreboard.registerNewObjective(
                "player",
                "dummy",
                Component.text("Hardcore Players", NamedTextColor.DARK_RED));

        int row = 1;

        for (UUID uuid : hardcorePlayers) {
            o.setDisplaySlot(DisplaySlot.SIDEBAR);

            HCPlayer hcPlayer = plugin.getDb().getReader().getPlayer(uuid, seasonNumber);
            if (hcPlayer == null) {
                plugin.getLogger().warning("Player {" + uuid + "} returned NULL from database! Skipping...");
                break;
            }

            HCPlayer onlinePlayer = plugin.getOnlinePlayer(uuid);
            if (seasonNumber == plugin.getSeasonNumber() && onlinePlayer != null) {
                if (!onlinePlayer.getStatus().equals(HCPlayer.STATUS.DEAD)) {
                    onlinePlayer.updateTime();
                    plugin.getDb().getWriter().updatePlayer(onlinePlayer);
                }
                hcPlayer = onlinePlayer;
            }

            TextComponent pStatus = switch (hcPlayer.getStatus()) {
                case ALIVE -> Component.text("Alive", NamedTextColor.GREEN);
                case DEAD -> Component.text("Dead", NamedTextColor.DARK_RED);
                case NETHER -> Component.text("Nether", NamedTextColor.RED);
                case END -> Component.text("End", NamedTextColor.LIGHT_PURPLE);
                case VICTORY -> Component.text("Victory", NamedTextColor.GOLD);
            };

            TextComponent timeString = Utils.convertTime(hcPlayer.getTime());

            String pString =
                    StringUtils.rightPad(hcPlayer.getPlayerName(),
                            16 - hcPlayer.getPlayerName().length()) + "&7 : " +
                            StringUtils.center(LegacyComponentSerializer.legacyAmpersand().serialize(pStatus), 9) + " &7: " +
                            StringUtils.center(LegacyComponentSerializer.legacyAmpersand().serialize(timeString), 6);

            Score playerRow = o.getScore(Utils.colorize(pString));
            playerRow.setScore(row);
            row++;

            player.setScoreboard(scoreboard);
        }
    }

    public void removeScoreboard(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
}
