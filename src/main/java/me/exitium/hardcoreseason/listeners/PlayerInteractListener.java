package me.exitium.hardcoreseason.listeners;

import com.comphenix.protocol.wrappers.Pair;
import com.destroystokyo.paper.MaterialTags;
import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.player.HCPlayer;
import me.exitium.hardcoreseason.statistics.GenericStat;
import me.exitium.hardcoreseason.statistics.StatisticsHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public record PlayerInteractListener(HardcoreSeason plugin) implements Listener {

    @EventHandler
    public void playerInteractBed(PlayerInteractEvent event) {
        World world = event.getPlayer().getWorld();
        if (!(plugin.getHcWorldManager().isHardcoreWorld(event.getPlayer().getWorld().getName()))) return;
        if ((event.getClickedBlock() == null) || !(MaterialTags.BEDS.isTagged(event.getClickedBlock()))) return;

        if (!world.getName().equals(plugin.getHcWorldManager().getHCWorld(World.Environment.NETHER))
                || !world.getName().equals(plugin.getHcWorldManager().getHCWorld(World.Environment.THE_END))) return;

        Block clickedBlock = event.getClickedBlock();
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

        HCPlayer hcPlayer = plugin.getOnlinePlayer(event.getPlayer().getUniqueId());
        hcPlayer.setNetherBedLocation(new Pair<>(new Pair<>(headLocation, footLocation), numEntities));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (hcPlayer.getNetherBedLocation() != null) {
                    hcPlayer.getNetherBedLocation().setSecond(0);
                }
            }
        }.runTaskLaterAsynchronously(plugin, 10L);
    }

    @EventHandler
    public void onUseEnderEye(PlayerInteractEvent event) {
        if (!(plugin.getHcWorldManager().isHardcoreWorld(event.getPlayer().getWorld().getName()))) return;
        if (!(event.getAction().isRightClick()
                && event.getItem() != null
                && event.getItem().getType().equals(Material.ENDER_EYE))) return;

        if ((event.getClickedBlock() != null) && !event.getClickedBlock().getType().equals(Material.END_PORTAL_FRAME)
                || event.getClickedBlock() == null) {
            plugin.getOnlinePlayer(event.getPlayer().getUniqueId()).getStatistics().addStat(
                    new GenericStat("Thrown"), StatisticsHandler.STATTYPE.EYE_USED);
        }

        if ((event.getClickedBlock() != null) && event.getClickedBlock().getType().equals(Material.END_PORTAL_FRAME)) {
            EndPortalFrame frame = (EndPortalFrame) event.getClickedBlock().getBlockData();
            if (frame.hasEye()) return;
            plugin.getOnlinePlayer(event.getPlayer().getUniqueId()).getStatistics().addStat(
                    new GenericStat("Placed"), StatisticsHandler.STATTYPE.EYE_USED);
        }
    }
}
