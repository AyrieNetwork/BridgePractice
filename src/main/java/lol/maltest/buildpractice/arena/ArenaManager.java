package lol.maltest.buildpractice.arena;

import lol.maltest.buildpractice.BuildPractice;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ArenaManager {

    private final BuildPractice plugin;
    private final FileConfiguration config;
    private final Set<Arena> arenaSet;

    public ArenaManager(BuildPractice plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.arenaSet = new HashSet<>();
    }

    public void deserialise() {
        ConfigurationSection configSection = config.getConfigurationSection("arena");
        if(configSection == null) return;
        configSection.getKeys(false).forEach(s -> arenaSet.add(new Arena(
                s, (Location) config.get("arena." + s + ".location")
        )));
        System.out.println(arenaSet);
    }

    public Arena getArena(Player player) {
        return arenaSet.stream().filter(arena -> arena.getPlayers().contains(player)).findFirst().orElse(null);
    }

    public Arena getArena(String key) {
        return arenaSet.stream().filter(arena -> arena.getName().equals(key)).findFirst().orElse(null);
    }

    public Set<Arena> getArenaList() {
        return arenaSet;
    }

    public void addArena(Arena arena) {
        this.arenaSet.add(arena);
    }

    public void removeArena(Arena arena) {
        this.arenaSet.remove(arena);
    }
}