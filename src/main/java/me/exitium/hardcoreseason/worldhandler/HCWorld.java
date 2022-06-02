package me.exitium.hardcoreseason.worldhandler;

import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldType;

import java.util.List;

public class HCWorld {
    private org.bukkit.World.Environment environment;
    private String name;
    private String alias;
    private String aliasColor;
    private WorldType type;
    private Difficulty difficulty;
    private List<String> spawnExceptions;

    HCWorld(String name, String alias, String aliasColor, Difficulty difficulty, WorldType type, List<String> spawnExceptions){
        this.name = name;
        this.alias = alias;
        this.aliasColor = aliasColor;
        this.difficulty = difficulty;
        this.type = type;
        this.spawnExceptions = spawnExceptions;
    }

    public World.Environment getEnvironment() {
        return environment;
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

    public String getAliasColor() {
        return aliasColor;
    }

    public WorldType getType() {
        return type;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public List<String> getSpawnExceptions() {
        return spawnExceptions;
    }
}