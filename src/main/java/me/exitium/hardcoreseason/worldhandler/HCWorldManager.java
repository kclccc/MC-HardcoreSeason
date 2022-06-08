package me.exitium.hardcoreseason.worldhandler;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import me.exitium.hardcoreseason.HardcoreSeason;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class HCWorldManager {
    private final HardcoreSeason plugin;
    Map<World.Environment, String> hardcoreWorlds;
    World softcoreWorld;

    public HCWorldManager(HardcoreSeason plugin) {
        this.plugin = plugin;
    }

    public void loadWorldsFromConfig() {
        File hcWorldsFile = new File(plugin.getDataFolder(), "hardcore-worlds.yml");
        YamlConfiguration hcWorldsConfig = new YamlConfiguration();
        try {
            hcWorldsConfig.load(hcWorldsFile);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().warning("Could not load world config.");
            e.printStackTrace();
        }

        hardcoreWorlds.put(World.Environment.NORMAL, hcWorldsConfig.getString("NORMAL.name"));
        hardcoreWorlds.put(World.Environment.NETHER, hcWorldsConfig.getString("NETHER.name"));
        hardcoreWorlds.put(World.Environment.THE_END, hcWorldsConfig.getString("THE_END.name"));

        String scWorldName = plugin.getConfig().getString("softcore-world");
        if(scWorldName == null || scWorldName.equals("")) {
            plugin.getLogger().warning("Could not find a valid world: " + scWorldName + ". Trying 'world'");
            scWorldName = "world";
        }
        softcoreWorld = Bukkit.getWorld(scWorldName);
        if(softcoreWorld == null) {
            plugin.getLogger().warning("Softcore world is NULL, plugin may not operate correctly. Check config!");
        }
    }

    public Map<World.Environment, String> getHardcoreWorlds() {
        return hardcoreWorlds;
    }

    public String getHCWorld(World.Environment env) {
        return hardcoreWorlds.get(env);
    }

    public World getSoftcoreWorld() {
        return softcoreWorld;
    }

    public void createAll() {
        for (World.Environment env : hardcoreWorlds.keySet()) {
            createWorld(env);
        }
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

    public boolean isHardcoreWorld(String worldName) {
        String hardcoreName = plugin.getConfig().getString("hardcore-world");
        return Objects.equals(worldName, hardcoreName) ||
                Objects.equals(worldName, hardcoreName + "_NETHER") ||
                Objects.equals(worldName, hardcoreName + "_THE_END");
    }
}
