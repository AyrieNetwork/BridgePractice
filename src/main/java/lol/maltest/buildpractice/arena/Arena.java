package lol.maltest.buildpractice.arena;

import lol.maltest.buildpractice.BuildPractice;
import lol.maltest.buildpractice.listeners.BlockListener;
import lol.maltest.buildpractice.methods.Scoreboard;
import lol.maltest.buildpractice.methods.Timer;
import lol.maltest.buildpractice.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class Arena {

    private final String name;
    private final Location location;
    private final Set<Player> players;
    private BuildPractice buildPractice;
    private Timer timer;
    private Scoreboard scoreboard;
    public Arena(String name, Location location) {
        this.name = name;
        this.location = location;
        this.players = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public Set<Player> getPlayers() {
        return Collections.unmodifiableSet(players);
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
        if(BuildPractice.bestAttempts.get(player.getUniqueId()) == null) return;
        BuildPractice.bestAttempts.remove(player.getUniqueId());
        if(Bukkit.getOnlinePlayers().contains(player)) {
            if(timer.getTime(player) != 0) {
                timer.stopTimer(player);
            }
            scoreboard.defaultScoreBoard(player);
        }
    }

}
