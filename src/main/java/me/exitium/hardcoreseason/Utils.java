package me.exitium.hardcoreseason;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Utils {
    public static String colorize(final String text) {
        return text == null ? null : ChatColor.translateAlternateColorCodes('&', text);
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

    public static Location processLocationString(World world, String location) {
        String[] splitLoc = location.split(":");

        double pSpawnX = Double.parseDouble(splitLoc[0]);
        double pSpawnY = Double.parseDouble(splitLoc[1]);
        double pSpawnZ = Double.parseDouble(splitLoc[2]);

        return new Location(world, pSpawnX, pSpawnY, pSpawnZ);
    }
}
