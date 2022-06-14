package me.exitium.hardcoreseason;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Utils {
    public static String colorize(final String text) {
        return text == null ? null : ChatColor.translateAlternateColorCodes('&', text);
//        return text == null ? null : LegacyComponentSerializer.legacy('&').deserialize(text);
    }

    public static byte[] asBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    public static UUID asUuid(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long firstLong = bb.getLong();
        long secondLong = bb.getLong();
        return new UUID(firstLong, secondLong);
    }

    public static Map<String, Integer> jsonToMap(String json) {
        if (json == null) return new HashMap<>();

        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String, Integer>>() {
        }.getType();
        return gson.fromJson(json, mapType);
    }

    public static Location processLocationString(String location) {
        String[] splitLoc = location.split(":");

        String worldName = splitLoc[0];
        double pSpawnX = Double.parseDouble(splitLoc[1]);
        double pSpawnY = Double.parseDouble(splitLoc[2]);
        double pSpawnZ = Double.parseDouble(splitLoc[3]);

        World world = Bukkit.getWorld(worldName);
        return new Location(world, pSpawnX, pSpawnY, pSpawnZ);
    }

    public static TextComponent convertTime(long millis) {
        return Component.text(TimeUnit.MILLISECONDS.toHours(millis), NamedTextColor.GOLD)
                .append(Component.text("h ", NamedTextColor.GRAY))
                .append(Component.text(TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), NamedTextColor.GOLD))
                .append(Component.text("m", NamedTextColor.GRAY));
    }

    public static class UUIDDataType implements PersistentDataType<byte[], UUID> {
        @Override
        public @NotNull Class<byte[]> getPrimitiveType() {
            return byte[].class;
        }

        @Override
        public @NotNull Class<UUID> getComplexType() {
            return UUID.class;
        }

        @Override
        public byte @NotNull [] toPrimitive(UUID complex, PersistentDataAdapterContext context) {
            ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
            bb.putLong(complex.getMostSignificantBits());
            bb.putLong(complex.getLeastSignificantBits());
            return bb.array();
        }

        @Override
        public @NotNull UUID fromPrimitive(byte[] primitive, PersistentDataAdapterContext context) {
            ByteBuffer bb = ByteBuffer.wrap(primitive);
            long firstLong = bb.getLong();
            long secondLong = bb.getLong();
            return new UUID(firstLong, secondLong);
        }
    }

    public static void saveInventoryToFile(HardcoreSeason plugin, Player player) {
        List<ItemStack> inventoryList = new ArrayList<>();
        for (ItemStack itemStack : player.getInventory()) {
            inventoryList.add(itemStack);
        }

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH-mm-ss");

        File playerFolder = new File(plugin.getDataFolder(), "PlayerData");
        if (!playerFolder.exists()) {
            playerFolder.mkdir();
        }

        File playerFile = new File(playerFolder, player.getName() + "-" + dateFormat.format(date) + ".yml");
        FileConfiguration playerConfig = new YamlConfiguration();
        playerConfig.set("name", player.getName());
        playerConfig.set("exp", player.getExp());
        playerConfig.set("level", player.getLevel());
        playerConfig.set("inventory", inventoryList);
        try {
            playerConfig.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
