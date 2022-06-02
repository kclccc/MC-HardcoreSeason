package me.exitium.hardcoreseason.worldhandler;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import me.exitium.hardcoreseason.HardcoreSeason;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

public class WorldManager {
    private final HardcoreSeason plugin;

    public WorldManager(HardcoreSeason plugin) {
        this.plugin = plugin;
    }

    MultiverseCore multiverseCore;

    public boolean initWorldManager() {
        Plugin mvPlugin = Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
        if ((mvPlugin instanceof MultiverseCore)) {
            multiverseCore = (MultiverseCore) mvPlugin;
            return true;
        }
        return false;
    }

    public void createAll(){
        Arrays.stream(World.Environment.values()).sequential()
                .filter(env -> env != World.Environment.CUSTOM)
                .forEach(this::createWorld);
    }

    private HCWorld fromConfig(World.Environment environment){
        ConfigurationSection section = plugin.getConfig().getConfigurationSection(environment.name());
        if(section == null) {
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
        HCWorld hcWorld = fromConfig(environment);

        if(hcWorld == null) {
            plugin.getLogger().severe("Could not create world from config settings!");
            return;
        }
        String worldName = hcWorld.getName();
        if(Bukkit.getWorld(worldName) == null) {
            if(multiverseCore.getMVWorldManager().addWorld(
                    worldName,                          // name
                    hcWorld.getEnvironment(),    // environment
                    null,                               // seed
                    hcWorld.getType(),           // type
                    true,                               // generate structures
                    null)) {                            // generator
                plugin.getLogger().info("Successfully created world: " + worldName);

                MultiverseWorld world = multiverseCore.getMVWorldManager().getMVWorld(worldName);
                world.setAlias(hcWorld.getAlias());
                world.setColor(hcWorld.getAliasColor());
                world.setDifficulty(hcWorld.getDifficulty());

                if(!hcWorld.getSpawnExceptions().isEmpty()) {
                    for (String mob : hcWorld.getSpawnExceptions()) {
                        world.getMonsterList().add(mob);
                    }
                }
            } else {
                plugin.getLogger().info("Failed to create world: " + worldName);

            }
        } else {
            plugin.getLogger().info("Error creating world, already exists.");
        }
    }
}
