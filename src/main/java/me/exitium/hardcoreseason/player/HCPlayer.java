package me.exitium.hardcoreseason.player;

import com.comphenix.protocol.wrappers.Pair;
import me.exitium.hardcoreseason.statistics.StatisticsHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class HCPlayer {
    private final UUID uuid;
    private HCPlayer.STATUS status;
    private StatisticsHandler statistics;
    private String deathMessage;
    private String bedLocation;
    private long time;
    private String returnLocation;
    // Temporary variables
    private Location lastLocation;
    private long timeCounter;
    private int teleportTaskID;
    private ItemStack[] shulkerInventory;
    private Location artifactLocation;
    private boolean enterVictoryPortal;
    private Pair<Pair<Location, Location>, Integer> netherBedLocation;
    public HCPlayer(UUID uuid, STATUS status, StatisticsHandler statistics, String bedLocation, long time, String returnLocation) {
        this.uuid = uuid;
        this.status = status;
        this.statistics = statistics;
        this.bedLocation = bedLocation;
        this.time = time;
        this.returnLocation = returnLocation;
    }

    public HCPlayer(UUID uuid) {
        this.uuid = uuid;
        this.status = STATUS.ALIVE;
    }

    public ItemStack[] getShulkerInventory() {
        return shulkerInventory;
    }

    public UUID getUUID() {
        return uuid;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public String getBedLocation() {
        return bedLocation;
    }

    public void setBedLocation(String bedLocation) {
        this.bedLocation = bedLocation;
    }

    public Location getArtifactLocation() {
        return this.artifactLocation;
    }

    public void setArtifactLocation(Location artifactLocation) {
        this.artifactLocation = artifactLocation;
    }

    public StatisticsHandler getStatistics() {
        return this.statistics;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void updateTime() {
        this.time = (int) (System.currentTimeMillis() - timeCounter) + time;
        this.timeCounter = System.currentTimeMillis();
    }

    public String getReturnLocation() {
        return returnLocation;
    }

    public String getDeathMessage() {
        return deathMessage;
    }

    public void killPlayer(String deathMessage, Location lastLocation) {
        this.status = STATUS.DEAD;
        this.deathMessage = deathMessage;
        this.lastLocation = lastLocation;
    }

    public void processVictory() {
        this.enterVictoryPortal = true;
        this.status = STATUS.VICTORY;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public Pair<Pair<Location, Location>, Integer> getNetherBedLocation() {
        return netherBedLocation;
    }

    public void setNetherBedLocation(Pair<Pair<Location, Location>, Integer> netherBedLocation) {
        this.netherBedLocation = netherBedLocation;
    }

    public void setTeleportTaskID(int taskID) {
        this.teleportTaskID = taskID;
    }

    public String getPlayerName() {
        Player player = Bukkit.getPlayer(uuid);
        if(player == null) {
            return "";
        }
        return player.getName();
    }

    public void setShulkerInventory(ItemStack[] contents) {
        this.shulkerInventory = contents;
    }

    public enum STATUS {
        ALIVE,
        DEAD,
        VICTORY
    }
}
