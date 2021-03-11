package lol.maltest.buildpractice;

import fr.mrmicky.fastboard.FastBoard;
import lol.maltest.buildpractice.arena.Arena;
import lol.maltest.buildpractice.arena.ArenaManager;
import lol.maltest.buildpractice.cmds.admin.CreateArena;
import lol.maltest.buildpractice.cmds.admin.ForceJoinArena;
import lol.maltest.buildpractice.cmds.admin.LeaderboardTest;
import lol.maltest.buildpractice.cmds.admin.LeaveArena;
import lol.maltest.buildpractice.cmds.staff.GameMode;
import lol.maltest.buildpractice.listeners.ArenaListener;
import lol.maltest.buildpractice.listeners.BlockListener;
import lol.maltest.buildpractice.listeners.InventoryListener;
import lol.maltest.buildpractice.methods.Scoreboard;
import lol.maltest.buildpractice.methods.Timer;
import lol.maltest.buildpractice.methods.menu.ArenaChoose;
import lol.maltest.buildpractice.utils.ChatUtil;
import lol.maltest.buildpractice.utils.PlayerMenuUtility;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class BuildPractice extends JavaPlugin {


    public static BuildPractice plugin;

    private final Map<UUID, FastBoard> boards = new HashMap<>();
    public static HashMap<UUID, Float> bestAttempts = new HashMap<>();
    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();



    private ArenaManager arenaManager;
    private ArenaListener arenaListener;
    private Scoreboard scoreboard;
    private Arena arena;
    private Timer timer;
    private BlockListener blockListener;

    float top1;
    float top2;
    float top3;
    String top1Name;
    String top2Name;
    String top3Name;

    public void onEnable() {

        plugin = this;
        FileConfiguration config = this.getConfig();
        config.options().copyDefaults(true);
        saveDefaultConfig();

        arenaManager = new ArenaManager(this);
        blockListener = new BlockListener(this);
        arenaListener = new ArenaListener(this);
        scoreboard = new Scoreboard(this);
        timer = new Timer(this);

        arenaManager.deserialise();

        getCommand("createarena").setExecutor(new CreateArena(this)); // admin
        getCommand("leavearena").setExecutor(new LeaveArena(this));
        getCommand("joinarena").setExecutor(new ForceJoinArena(this));
        getCommand("leaderboardtest").setExecutor(new LeaderboardTest(this));
        getCommand("gamemode").setExecutor(new GameMode());

        getServer().getPluginManager().registerEvents(new Scoreboard(this), this);
        getServer().getPluginManager().registerEvents(new ArenaListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                int i = 1;
                    for(Map.Entry<UUID, Float> ye : entriesSortedByValues(plugin.getBestAttempts())) {
                        if(i == 1) {
                            UUID uuid = (UUID) ye.getKey();
                            Player p = Bukkit.getPlayer(uuid);
                            top1 = ye.getValue();
                            top1Name = p.getName();
                        }
                        if(i == 2) {
                            UUID uuid = (UUID) ye.getKey();
                            Player p = Bukkit.getPlayer(uuid);
                            top2 = (float) ye.getValue();
                            top2Name = p.getName();
                        }
                        if(i == 3) {
                            UUID uuid = (UUID) ye.getKey();
                            Player p = Bukkit.getPlayer(uuid);
                            top3 = (float) ye.getValue();
                            top3Name = p.getName();
                        }
                        i++;
                    }
                    System.out.println("Refreshing leaderboard");
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        if(!BlockListener.briding.contains(player.getUniqueId())) {
                            scoreboard.defaultScoreBoard(player);
                        }
                    }
            }
        }, 0, 80L);
    }

    public void onDisable() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer("Server Stopped");
        }
        getServer().getConsoleSender().sendMessage(ChatUtil.clr("&cDeleted all blocks!"));
        for(Map.Entry yes : boards.entrySet()) {
            FastBoard fastBoard = (FastBoard) yes.getValue();
            fastBoard.delete();
        }
        getServer().getConsoleSender().sendMessage(ChatUtil.clr("&cDeleted all boards!"));
        getServer().getConsoleSender().sendMessage(ChatUtil.clr("&cGoodbye <3"));
        if(getConfig().getBoolean("no-save")) {
            Runtime.getRuntime().halt(0);
        }
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public BlockListener getBlockListener() {
        return blockListener;
    }

    public Timer getTimerClass() {
        return timer;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public Map<UUID, FastBoard> getBoards() {
        return boards;
    }

    public Map<UUID, Float> getBestAttempts() {
        return bestAttempts;
    }


    public String getTop1Name() {
        return top1Name;
    }

    public String getTop2Name() {
        return top2Name;
    }

    public String getTop3Name() {
        return top3Name;
    }

    public float getTop1() {
        return top1;
    }

    public float getTop2() {
        return top2;
    }

    public float getTop3() {
        return top3;
    }


    static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
                new Comparator<Map.Entry<K,V>>() {
                    @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                        int res = e1.getValue().compareTo(e2.getValue());
                        return res != 0 ? res : 1; // Special fix to preserve items with equal values
                    }
                }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }


    //Provide a player and return a menu system for that player
    //create one if they don't already have one
    public static PlayerMenuUtility getPlayerMenuUtility(Player p) {
        PlayerMenuUtility playerMenuUtility;
        if (!(playerMenuUtilityMap.containsKey(p))) { //See if the player has a playermenuutility "saved" for them

            //This player doesn't. Make one for them add add it to the hashmap
            playerMenuUtility = new PlayerMenuUtility(p);
            playerMenuUtilityMap.put(p, playerMenuUtility);

            return playerMenuUtility;
        } else {
            return playerMenuUtilityMap.get(p); //Return the object by using the provided player
        }
    }

}
