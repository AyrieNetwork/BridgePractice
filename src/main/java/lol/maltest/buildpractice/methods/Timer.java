package lol.maltest.buildpractice.methods;

import lol.maltest.buildpractice.BuildPractice;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import sun.corba.BridgePermission;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Timer {

    public Map<UUID, Long> timers = new HashMap<UUID, Long>();

    private final BuildPractice plugin;

    private Scoreboard scoreboard;

    public Timer(BuildPractice plugin) {
        this.plugin = plugin;
        this.scoreboard = plugin.getScoreboard();
    }

    public void startTimer(Player player) {
        if(timers.containsKey(player.getUniqueId())) {
            System.out.println("already in");
        } else {
            timers.put(player.getUniqueId(), System.currentTimeMillis());
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(timers.containsKey(player.getUniqueId())) {
                        scoreboard.timerScoreBoard(player);
                    } else {
                        scoreboard.defaultScoreBoard(player);
                        cancel();
                    }
                }
            }.runTaskTimer(plugin,0, 1L);
        }
    }

    public float getTime(Player player) {
        Long time = timers.get(player.getUniqueId());
        if(time != null) {
            return System.currentTimeMillis() - time;
        } else {
            return 0;
        }
    }

    public void stopTimer(Player player) {
        timers.remove(player.getUniqueId());
    }
}