package me.exitium.hardcoreseason.player;

import com.comphenix.protocol.wrappers.Pair;
import me.exitium.hardcoreseason.statistics.StatisticsHandler;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.UUID;

public class HCPlayer {
    private final UUID uuid;
    private String playerName;
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

    public HCPlayer(UUID uuid, String playerName, STATUS status, StatisticsHandler statistics, String bedLocation, long time, String returnLocation) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.status = status;
        this.statistics = statistics;
        this.bedLocation = bedLocation;
        this.time = time;
        this.returnLocation = returnLocation;
    }

    public HCPlayer(UUID uuid, String playerName) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.status = STATUS.ALIVE;
        this.statistics = new StatisticsHandler();
    }

    public HCPlayer(UUID uuid, String playerName, STATUS status, long time) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.status = status;
        this.time = time;
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

    public void setTimeCounter(long timeCounter) {
        this.timeCounter = timeCounter;
    }

    public void updateTime() {
        if (timeCounter == 0) return;
        this.time = ((System.currentTimeMillis() - timeCounter)) + time;
        this.timeCounter = System.currentTimeMillis();
    }

    public String getReturnLocation() {
        return returnLocation;
    }

    public void setReturnLocation(Location returnLocation) {
        this.returnLocation = String.format("%s:%d:%d:%d", returnLocation.getWorld().getName(), returnLocation.getBlockX(), returnLocation.getBlockY(), returnLocation.getBlockZ());
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
        return this.playerName;
    }

    public void setShulkerInventory(ItemStack[] contents) {
        this.shulkerInventory = contents;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public enum STATUS {
        DEAD,
        ALIVE,
        NETHER,
        END,
        VICTORY
    }

    @Override
    public String toString() {
        return "HCPlayer{" +
                "uuid=" + uuid +
                ", playerName=" + playerName +
                ", status=" + status +
                ", statistics=" + statistics +
                ", deathMessage='" + deathMessage + '\'' +
                ", bedLocation='" + bedLocation + '\'' +
                ", time=" + time +
                ", returnLocation='" + returnLocation + '\'' +
                ", lastLocation=" + lastLocation +
                ", timeCounter=" + timeCounter +
                ", teleportTaskID=" + teleportTaskID +
                ", shulkerInventory=" + Arrays.toString(shulkerInventory) +
                ", artifactLocation=" + artifactLocation +
                ", enterVictoryPortal=" + enterVictoryPortal +
                ", netherBedLocation=" + netherBedLocation +
                '}';
    }
}
