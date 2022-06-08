package me.exitium.hardcoreseason.database;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.Utils;
import me.exitium.hardcoreseason.player.HCPlayer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public record DatabaseWriter(HardcoreSeason plugin) {

    public void addPlayer(HCPlayer player) {
        try (PreparedStatement ps = plugin.getSqlConnection().prepareStatement(
                "INSERT INTO hardcore_season (uuid, season_number, status) " +
                        "VALUES(?, ?, ?);")) {
            ps.setObject(1, Utils.asBytes(player.getUUID()));
            ps.setInt(2, plugin.getSeasonNumber());
            ps.setInt(3, player.getStatus().ordinal());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updatePlayer(HCPlayer player) {
        try (PreparedStatement ps = plugin.getSqlConnection().prepareStatement(
                "INSERT INTO hardcore_season (uuid, season_number, status, time, spawn_point, death_type, return_location, " +
                        "monster_kills, damage_taken, damage_dealt, items_crafted, trades_made, food_eaten, potions_used, eyes_used) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY " +
                        "UPDATE time=VALUES(time), " +
                        "spawn_point=VALUES(spawn_point), " +
                        "death_type=VALUES(death_type), " +
                        "return_location=VALUES(return_location)," +
                        "monster_kills=VALUES(monster_kills), " +
                        "damage_taken=VALUES(damage_taken), " +
                        "damage_dealt=VALUES(damage_dealt), " +
                        "items_crafted=VALUES(items_crafted), " +
                        "trades_made=VALUES(trades_made), " +
                        "food_eaten=VALUES(food_eaten), " +
                        "potions_used=VALUES(potions_used), " +
                        "eyes_used=VALUES(eyes_used)")) {
            ps.setObject(1, Utils.asBytes(player.getUUID()));
            ps.setInt(2, plugin.getSeasonNumber());
            ps.setInt(3, player.getStatus().ordinal());
            ps.setLong(4, player.getTime());
            ps.setString(5, player.getBedLocation());
            ps.setString(6, player.getDeathMessage());
            ps.setString(7, player.getReturnLocation());
            ps.setString(8, player.getStatistics().toJson(player.getStatistics().getMobKillList()));
            ps.setString(9, player.getStatistics().toJson(player.getStatistics().getDamageTakenList()));
            ps.setString(10, player.getStatistics().toJson(player.getStatistics().getDamageDealtList()));
            ps.setString(11, player.getStatistics().toJson(player.getStatistics().getItemCraftedList()));
            ps.setString(12, player.getStatistics().toJson(player.getStatistics().getTradesList()));
            ps.setString(13, player.getStatistics().toJson(player.getStatistics().getConsumeFoodList()));
            ps.setString(14, player.getStatistics().toJson(player.getStatistics().getDrinkPotionList()));
            ps.setString(15, player.getStatistics().toJson(player.getStatistics().getEyesUsedList()));

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
