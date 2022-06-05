package me.exitium.hardcoreseason.worldhandler;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import me.exitium.hardcoreseason.HardcoreSeason;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

public class HCWorldManager {
    private final HardcoreSeason plugin;

    public HCWorldManager(HardcoreSeason plugin) {
        this.plugin = plugin;
    }

    public void createAll() {
        Arrays.stream(World.Environment.values()).sequential()
                .filter(env -> env != World.Environment.CUSTOM)
                .forEach(this::createWorld);
    }

    public boolean isHardcoreWorld(String worldName) {
        String hardcoreName = plugin.getConfig().getString("hardcore-world");
        return Objects.equals(worldName, hardcoreName) ||
                Objects.equals(worldName, hardcoreName + "_NETHER") ||
                Objects.equals(worldName, hardcoreName + "_THE_END");
    }

    private HCWorld fromConfig(World.Environment environment) {
        YamlConfiguration worldConfig = YamlConfiguration.loadConfiguration(
                new File(plugin.getDataFolder(), "hardcore-worlds.yml"));

        ConfigurationSection section = worldConfig.getConfigurationSection(environment.name());
        if (section == null) {
            // TODO: load default?
            return null;
        }

        return new HCWorld(
                section.getString("name"),
                section.getString("alias"),
                section.getString("color"),
                section.getObject("difficulty", Difficulty.class),
                section.getObject("type", WorldType.class),
                section.getStringList("spawn-exceptions")
        );
    }

    public void createWorld(World.Environment environment) {
        MultiverseCore mvCore = plugin.getMultiverseCore();
        HCWorld hcWorld = fromConfig(environment);

        if (hcWorld == null) {
            plugin.getLogger().severe("Could not create world from config settings!");
            return;
        }

        String worldName = hcWorld.getName();
        if (Bukkit.getWorld(worldName) != null) {
            mvCore.deleteWorld(worldName);
            plugin.getLogger().info("Deleting world before creation: " + worldName);
        }

        if (mvCore.getMVWorldManager().addWorld(
                worldName,                   // name
                environment,    // environment
                null,                        // seed
                hcWorld.getType(),           // type
                true,                        // generate structures
                null)) {                     // generator
            plugin.getLogger().info("Successfully created world: " + worldName);

            MultiverseWorld world = mvCore.getMVWorldManager().getMVWorld(worldName);
            world.setAlias(hcWorld.getAlias());
            world.setColor(hcWorld.getAliasColor());
            world.setDifficulty(hcWorld.getDifficulty());
            world.setKeepSpawnInMemory(false);

            if (!hcWorld.getSpawnExceptions().isEmpty()) {
                for (String mob : hcWorld.getSpawnExceptions()) {
                    world.getMonsterList().add(mob);
                }
            }
        } else {
            plugin.getLogger().info("Failed to create world: " + worldName);
        }
    }
}
