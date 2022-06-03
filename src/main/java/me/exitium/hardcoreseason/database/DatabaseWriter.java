package me.exitium.hardcoreseason.database;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.Utils;
import me.exitium.hardcoreseason.player.HCPlayer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public record DatabaseWriter(HardcoreSeason plugin) {

    public void addPlayer(UUID uuid, int season, int time) {
        try (PreparedStatement ps = plugin.getSqlConnection().prepareStatement("" +
                "INSERT INTO hardcore_season (uuid, season, status) VALUES(?, ?, ?);")) {
            ps.setObject(1, Utils.asBytes(uuid));
            ps.setInt(2, season);
            ps.setInt(3, HCPlayer.STATUS.ALIVE.ordinal());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
