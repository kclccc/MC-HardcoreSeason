package me.exitium.hardcoreseason.listeners;

import com.comphenix.protocol.wrappers.Pair;
import com.destroystokyo.paper.MaterialTags;
import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.playerdata.HCPlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerInteractListener implements Listener {
    private final HardcoreSeason plugin;

    public PlayerInteractListener(HardcoreSeason plugin) {
        this.plugin = plugin;
    }

//    private static final ImmutableSet<Material> BED_MATERIALS = Sets.immutableEnumSet(
//      Material.BLACK_BED,
//      Material.BLUE_BED,
//      Material.BROWN_BED,
//      Material.CYAN_BED,
//      Material.GRAY_BED,
//      Material.GREEN_BED,
//      Material.LIGHT_BLUE_BED,
//      Material.LIGHT_GRAY_BED,
//      Material.LIME_BED,
//      Material.MAGENTA_BED,
//      Material.ORANGE_BED,
//      Material.PINK_BED,
//      Material.PURPLE_BED,
//      Material.RED_BED,
//      Material.WHITE_BED,
//      Material.YELLOW_BED
//    );

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        if (world.getName().equals(plugin.getHardcoreWorld(World.Environment.NETHER)) || world.getName().equals(plugin.getHardcoreWorld(World.Environment.THE_END)) ) {
            Block clickedBlock = event.getClickedBlock();

            if (clickedBlock != null && MaterialTags.BEDS.isTagged(clickedBlock)) {
                Bed bed = (Bed) clickedBlock.getBlockData();
                Location headLocation, footLocation;
                BlockFace bedFace = bed.getFacing();
                Location clickedLocation = clickedBlock.getLocation();

                switch (bed.getPart()) {
                    case HEAD -> {
                        headLocation = clickedLocation;
                        footLocation = clickedLocation;
                        if (bedFace == BlockFace.NORTH) footLocation.setZ(footLocation.getZ() + 1);
                        if (bedFace == BlockFace.SOUTH) footLocation.setZ(footLocation.getZ() - 1);
                        if (bedFace == BlockFace.EAST) footLocation.setX(footLocation.getX() - 1);
                        if (bedFace == BlockFace.WEST) footLocation.setX(footLocation.getX() + 1);
                    }
                    case FOOT -> {
                        headLocation = clickedLocation;
                        footLocation = clickedLocation;
                        if (bedFace == BlockFace.NORTH) headLocation.setZ(headLocation.getZ() - 1);
                        if (bedFace == BlockFace.SOUTH) headLocation.setZ(headLocation.getZ() + 1);
                        if (bedFace == BlockFace.EAST) headLocation.setX(headLocation.getX() + 1);
                        if (bedFace == BlockFace.WEST) headLocation.setX(headLocation.getX() - 1);
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + bed.getPart());
                }

                int numEntities = clickedLocation.getNearbyLivingEntities(7).size();

                HCPlayer hcPlayer = plugin.getHcPlayerService().getHardcorePlayer(player.getUniqueId());
                hcPlayer.setNetherBedLocation(new Pair<>(new Pair<>(headLocation, footLocation), numEntities));

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(hcPlayer.getNetherBedLocation() != null) {
                            hcPlayer.getNetherBedLocation().setSecond(0);
//                            plugin.getLogger().info("Clearing entity counter.");
                        }
                    }
                }.runTaskLaterAsynchronously(plugin, 10L);
            }
        }
    }
}
