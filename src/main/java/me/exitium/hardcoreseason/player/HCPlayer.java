package me.exitium.hardcoreseason.player;

import it.unimi.dsi.fastutil.Pair;
import me.exitium.hardcoreseason.statistics.StatisticsHandler;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class HCPlayer {
    public enum STATUS {
        ALIVE,
        DEAD,
        VICTORY
    }

    private final UUID uuid;
    private HCPlayer.STATUS status;
    private StatisticsHandler statistics;

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

    public UUID getUUID() {
        return uuid;
    }

    public STATUS getStatus() {
        return status;
    }
}
