package me.exitium.hardcoreseason.database;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.Utils;
import me.exitium.hardcoreseason.player.HCPlayer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public record DatabaseWriter(HardcoreSeason plugin) {

    public void addPlayer(HCPlayer player) {
        try (PreparedStatement ps = plugin.getSqlConnection().prepareStatement("" +
                "INSERT INTO hardcore_season (uuid, season_number, status) VALUES(?, ?, ?);")) {
            ps.setObject(1, Utils.asBytes(player.getUUID()));
            ps.setInt(2, plugin.getSeasonNumber());
            ps.setInt(3, player.getStatus().ordinal());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
