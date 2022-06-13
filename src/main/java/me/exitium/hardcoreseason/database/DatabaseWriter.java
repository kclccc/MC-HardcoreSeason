package me.exitium.hardcoreseason.database;

import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.Utils;
import me.exitium.hardcoreseason.player.HCPlayer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public record DatabaseWriter(HardcoreSeason plugin) {

    public void addPlayer(HCPlayer hcPlayer) {
        try (PreparedStatement ps = plugin.getSqlConnection().prepareStatement(
                "INSERT INTO hardcore_season (uuid, season_number, status) " +
                        "VALUES(?, ?, ?);")) {
            ps.setObject(1, Utils.asBytes(hcPlayer.getUUID()));
            ps.setInt(2, plugin.getSeasonNumber());
            ps.setInt(3, hcPlayer.getStatus().ordinal());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePlayer(HCPlayer hcPlayer) {
        String mysqlUpsert = "INSERT INTO hardcore_season (uuid, season_number, status, time, spawn_point, death_type, return_location, " +
                "monster_kills, damage_taken, damage_dealt, items_crafted, trades_made, food_eaten, potions_used, eyes_used) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "time=VALUES(time), " +
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
                "eyes_used=VALUES(eyes_used)";

        String sqliteUpsert = "INSERT INTO hardcore_season (uuid, season_number, status, time, spawn_point, death_type, return_location, " +
                "monster_kills, damage_taken, damage_dealt, items_crafted, trades_made, food_eaten, potions_used, eyes_used) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT(uuid, season_number) DO UPDATE SET " +
                "status=excluded.status, " +
                "time=excluded.time, " +
                "spawn_point=excluded.spawn_point, " +
                "death_type=excluded.death_type, " +
                "return_location=excluded.return_location," +
                "monster_kills=excluded.monster_kills, " +
                "damage_taken=excluded.damage_taken, " +
                "damage_dealt=excluded.damage_dealt, " +
                "items_crafted=excluded.items_crafted, " +
                "trades_made=excluded.trades_made, " +
                "food_eaten=excluded.food_eaten, " +
                "potions_used=excluded.potions_used, " +
                "eyes_used=excluded.eyes_used;";

        try (PreparedStatement ps = plugin.getSqlConnection().prepareStatement(
                plugin.getDb().getStorageType().equals("MYSQL") ? mysqlUpsert : sqliteUpsert)) {

            ps.setObject(1, Utils.asBytes(hcPlayer.getUUID()));
            ps.setInt(2, plugin.getSeasonNumber());
            ps.setInt(3, hcPlayer.getStatus().ordinal());
            ps.setLong(4, hcPlayer.getTime());
            ps.setString(5, hcPlayer.getBedLocation());
            ps.setString(6, hcPlayer.getDeathMessage());
            ps.setString(7, hcPlayer.getReturnLocation());
            ps.setString(8, hcPlayer.getStatistics().toJson(hcPlayer.getStatistics().getMobKillList()));
            ps.setString(9, hcPlayer.getStatistics().toJson(hcPlayer.getStatistics().getDamageTakenList()));
            ps.setString(10, hcPlayer.getStatistics().toJson(hcPlayer.getStatistics().getDamageDealtList()));
            ps.setString(11, hcPlayer.getStatistics().toJson(hcPlayer.getStatistics().getItemCraftedList()));
            ps.setString(12, hcPlayer.getStatistics().toJson(hcPlayer.getStatistics().getTradesList()));
            ps.setString(13, hcPlayer.getStatistics().toJson(hcPlayer.getStatistics().getConsumeFoodList()));
            ps.setString(14, hcPlayer.getStatistics().toJson(hcPlayer.getStatistics().getDrinkPotionList()));
            ps.setString(15, hcPlayer.getStatistics().toJson(hcPlayer.getStatistics().getEyesUsedList()));

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
