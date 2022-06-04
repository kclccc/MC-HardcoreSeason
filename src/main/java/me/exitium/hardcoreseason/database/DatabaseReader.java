package me.exitium.hardcoreseason.database;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.Utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public record DatabaseReader(HardcoreSeason plugin) {

    public boolean hcPlayerExists(UUID uuid) {
        try (PreparedStatement ps = plugin.getSqlConnection().prepareStatement("" +
                "SELECT TOP 1 FROM hardcore_season WHERE uuid=? AND season_number=?")) {
            ps.setObject(1, Utils.asBytes(uuid));
            ps.setInt(2, plugin.getSeasonNumber());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
