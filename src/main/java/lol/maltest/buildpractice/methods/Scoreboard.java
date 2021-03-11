package lol.maltest.buildpractice.methods;

import fr.mrmicky.fastboard.FastBoard;
import lol.maltest.buildpractice.BuildPractice;
import lol.maltest.buildpractice.arena.ArenaManager;
import lol.maltest.buildpractice.listeners.BlockListener;
import lol.maltest.buildpractice.utils.ChatUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Scoreboard implements Listener {

    private FastBoard board;

    private final BuildPractice plugin;
    private BlockListener blockListener;
    private ArenaManager arenaManager;

    public Scoreboard(BuildPractice plugin) {
        this.plugin = plugin;
        this.blockListener = plugin.getBlockListener();
        this.arenaManager = plugin.getArenaManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        board = new FastBoard(player);

        board.updateTitle(ChatUtil.clr("&4&lBRIDGING"));
        plugin.getBoards().put(player.getUniqueId(), board);
        new BukkitRunnable() {
            @Override
            public void run() {
                defaultScoreBoard(player);
                cancel();
            }
        }.runTaskLater(plugin, 10L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        FastBoard board = plugin.getBoards().remove(player.getUniqueId());

        if (board != null) {
            board.delete();
        }
    }


    public void defaultScoreBoard(Player player) {
        FastBoard b = plugin.getBoards().get(player.getUniqueId());
        List<String> scoreboardLines = new ArrayList<>();
        scoreboardLines.add(ChatUtil.clr("&7&m------------------"));
        scoreboardLines.add(ChatUtil.clr("&f&l" + player.getName() + ":"));
        if(arenaManager.getArena(player) != null) {
            scoreboardLines.add(ChatUtil.clr("&fStatus: &cIdle"));
        } else {
            scoreboardLines.add(ChatUtil.clr("&fStatus: &cNot in Arena"));
        }
        scoreboardLines.add(ChatUtil.clr("&8"));
        scoreboardLines.add(ChatUtil.clr("&f&lLeaderboard:"));
        if(plugin.getTop1Name() == null) {
            scoreboardLines.add(ChatUtil.clr("&f1: &c???"));
        } else {
            scoreboardLines.add(ChatUtil.clr("&f1: " + plugin.getTop1Name() + ": &c" + timeToString(plugin.getTop1())));
        }
        if(plugin.getTop2Name() == null) {
            scoreboardLines.add(ChatUtil.clr("&f2: &c???"));
        } else {
            scoreboardLines.add(ChatUtil.clr("&f2: " + plugin.getTop2Name() + ": &c" + timeToString(plugin.getTop2())));

        }
        if(plugin.getTop3Name() == null) {
            scoreboardLines.add(ChatUtil.clr("&f3: &c???"));
        } else {
            scoreboardLines.add(ChatUtil.clr("&f3: " + plugin.getTop3Name() + ": &c" + timeToString(plugin.getTop3())));
        }
        scoreboardLines.add(ChatUtil.clr("&7&m------------------"));
        scoreboardLines.add(ChatUtil.clr("&4ayrie.club"));
        b.updateLines(scoreboardLines);
    }


    public void timerScoreBoard(Player player) {
        FastBoard b = plugin.getBoards().get(player.getUniqueId());
        DecimalFormat df=new DecimalFormat("#.00");
        double seconds = plugin.getTimerClass().getTime(player) / 1000;
        double mins = seconds / 60;
        String ms = String.format(String.valueOf(plugin.getTimerClass().getTime(player) / 1000.f % 60.f));
        if(BlockListener.blocks.get(player.getUniqueId()).size() == 0 || seconds == 0) {
            b.updateLines(
                    ChatUtil.clr("&7&m-----------------"),
                    ChatUtil.clr("&cTime: &f" + currentTimeToString(player)),
                    ChatUtil.clr("&cBlocks &f" + BlockListener.blocks.get(player.getUniqueId()).size()),
                    ChatUtil.clr("&cSpeed: &f0"),
                    ChatUtil.clr("&7&m-----------------")
            );
        } else {
            b.updateLines(
                    ChatUtil.clr("&7&m-----------------"),
                    ChatUtil.clr("&cTime: &7" + currentTimeToString(player)),
                    ChatUtil.clr("&cBlocks &7" + BlockListener.blocks.get(player.getUniqueId()).size()),
                    ChatUtil.clr("&cSpeed: &7" + df.format(BlockListener.blocks.get(player.getUniqueId()).size() / seconds)),
                    ChatUtil.clr("&7&m-----------------")
            );
        }
    }

    public String currentTimeToString(Player player) {
        float time = plugin.getTimerClass().getTime(player) / 1000.f;
        float seconds = time % 60.0f;
        float minutes = (float) Math.floor(time / 60.0f);
        return String.format("%.0f", minutes) + ":" + (seconds < 10 ? "0" : "") + String.format("%.3f", seconds);
    }

    public String timeToString(float time) {
        time = time / 1000;
        float seconds = time % 60.0f;
        float minutes = (float) Math.floor(time / 60.0f);
        return String.format("%.0f", minutes) + ":" + (seconds < 10 ? "0" : "") + String.format("%.3f", seconds);
    }
}
