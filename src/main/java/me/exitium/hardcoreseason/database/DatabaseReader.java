package me.exitium.hardcoreseason.database;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.Utils;
import me.exitium.hardcoreseason.player.HCPlayer;
import me.exitium.hardcoreseason.statistics.StatisticsHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record DatabaseReader(HardcoreSeason plugin) {

    public boolean hcPlayerExists(UUID uuid) {
        try (PreparedStatement ps = plugin.getSqlConnection().prepareStatement(
                "SELECT 1 FROM hardcore_season WHERE uuid=? AND season_number=?")) {
            ps.setObject(1, Utils.asBytes(uuid));
            ps.setInt(2, plugin.getSeasonNumber());

            ResultSet result = ps.executeQuery();
            return result.next();
        } catch (SQLException e) {
            // TODO: actual error handling
            e.printStackTrace();
        }
        plugin.getLogger().info("Why did this trigger?");
        return true;
    }

    public HCPlayer getPlayer(UUID uuid) {
        try (PreparedStatement ps = plugin.getSqlConnection().prepareStatement(
                "SELECT 1 FROM hardcore_season WHERE uuid=? AND season_number=?")) {
            ps.setObject(1, Utils.asBytes(uuid));
            ps.setInt(2, plugin.getSeasonNumber());

            ResultSet result = ps.executeQuery();

            if (result.next()) {
                return new HCPlayer(
                        uuid,
                        HCPlayer.STATUS.values()[result.getInt("status")],
                        new StatisticsHandler(
                                Utils.jsonToMap(result.getString("monster_kills")),
                                Utils.jsonToMap(result.getString("potions_used")),
                                Utils.jsonToMap(result.getString("food_eaten")),
                                Utils.jsonToMap(result.getString("damage_taken")),
                                Utils.jsonToMap(result.getString("damage_dealt")),
                                Utils.jsonToMap(result.getString("items_crafted")),
                                Utils.jsonToMap(result.getString("eyes_used")),
                                Utils.jsonToMap(result.getString("trades_made"))),
                        result.getString("spawn_point"),
                        result.getInt("time"),
                        result.getString("return_location")
                );
            }
        } catch (SQLException e) {
            // TODO: Actual error handling
            e.printStackTrace();
        }
        return null;
    }

    public List<UUID> getAllPlayers() {
        try (PreparedStatement ps = plugin.getSqlConnection().prepareStatement(
                "SELECT uuid FROM hardcore_season WHERE season_number=?")) {
            ps.setInt(1, plugin.getSeasonNumber());

            ResultSet results = ps.executeQuery();

            List<UUID> playerList = new ArrayList<>();
            while(results.next()){
                playerList.add(Utils.asUuid(results.getBytes("uuid")));
            }
            return playerList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
