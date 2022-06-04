package me.exitium.hardcoreseason.database;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.Utils;
import me.exitium.hardcoreseason.player.HCPlayer;
import me.exitium.hardcoreseason.statistics.StatisticsHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public record DatabaseReader(HardcoreSeason plugin) {

    public boolean hcPlayerExists(UUID uuid) {
        try (PreparedStatement ps = plugin.getSqlConnection().prepareStatement(
                "SELECT TOP 1 FROM hardcore_season WHERE uuid=? AND season_number=?")) {
            ps.setObject(1, Utils.asBytes(uuid));
            ps.setInt(2, plugin.getSeasonNumber());
        } catch (SQLException e) {
            // TODO: actual error handling
            e.printStackTrace();
        }

        return false;
    }

    public HCPlayer getPlayer(UUID uuid) {
        try (PreparedStatement ps = plugin.getSqlConnection().prepareStatement(
                "SELECT * FROM hardcore_season WHERE uuid=? AND season_number=?")){
            ps.setObject(1, Utils.asBytes(uuid));
            ps.setInt(2, plugin.getSeasonNumber());

            ResultSet result = ps.executeQuery();
            if(result.next()){
                return new HCPlayer(
                        uuid,
                        HCPlayer.STATUS.values()[result.getInt("status")],

                )
            }
        } catch(SQLException e){
            // TODO: Actual error handling
            e.printStackTrace();
        }
    }
}
