package lol.maltest.buildpractice.listeners;

import com.connorlinfoot.titleapi.TitleAPI;
import fr.mrmicky.fastboard.FastBoard;
import lol.maltest.buildpractice.BuildPractice;
import lol.maltest.buildpractice.arena.Arena;
import lol.maltest.buildpractice.arena.ArenaManager;
import lol.maltest.buildpractice.methods.Scoreboard;
import lol.maltest.buildpractice.methods.Timer;
import lol.maltest.buildpractice.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class BlockListener implements Listener {

    private ArenaManager arenaManager;
    private Timer timer;
    private ArenaListener arenaListener;
    private Scoreboard scoreboard;
    public BuildPractice plugin;
    public static Map<UUID, List<Block>> blocks = new HashMap<>();
    public static ArrayList<UUID> briding = new ArrayList<>();

    public BlockListener(BuildPractice plugin) {
        this.plugin = plugin;
        this.arenaManager = plugin.getArenaManager();
        this.timer = plugin.getTimerClass();
        this.scoreboard = plugin.getScoreboard();
    }


    public void onBlockPlaced(Player player, Block block) {
        List<Block> placed = blocks.get(player.getUniqueId());
        if (placed == null) {
            briding.add(player.getUniqueId());
            placed = new ArrayList<Block>();
            blocks.put(player.getUniqueId(), placed);
            timer.startTimer(player);
        }
        placed.add(block);
    }

    public List<Block> getBlocksPlacedBy(Player player) {
        return blocks.get(player.getUniqueId());
    }

    public void removePlayerFromMap(Player player) {
        blocks.remove(player.getUniqueId());
        briding.remove(player.getUniqueId());
    }

    public UUID getWhoPlaced(Block block) {
        UUID result = null;
        Iterator<Map.Entry<UUID, List<Block>>> iterator = blocks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, List<Block>> entry = iterator.next();
            if (entry.getValue().contains(block)) {
                result = entry.getKey();
                break;
            }
        }
        return result;
    }

    public void removeBlock(Block block) {
        UUID player = getWhoPlaced(block);
        if (player != null) {
            blocks.get(player).remove(block);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Arena arena = arenaManager.getArena(player);
        if(arena == null) return;
        briding.remove(player.getUniqueId());
        if(getBlocksPlacedBy(player) != null) {
            if (getBlocksPlacedBy(player).size() > 0) {
                for (Block blocks : getBlocksPlacedBy(player)) {
                    blocks.setType(Material.AIR);
                }
            }
        }
        removePlayerFromMap(player);
        arena.removePlayer(player);
        event.setQuitMessage("");
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent e) {
        Block block = e.getBlock();
        Player player = e.getPlayer();
        if(arenaManager.getArena(player) != null) {
            Double X = arenaManager.getArena(player).getLocation().getX();
            Double Y = arenaManager.getArena(player).getLocation().getY();
            Double Z = arenaManager.getArena(player).getLocation().getZ();

            if (block.getZ() < -2 || block.getX() == X + 3 || block.getX() == X - 3) {
                if(player.hasPermission("bp.admin")) return;
                e.setCancelled(true);
                player.sendMessage(ChatUtil.clr("&cYou can't build here."));
            } else {
                onBlockPlaced(player,block);
            }
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        Player player = e.getPlayer();
        if(!player.hasPermission("bp.admin")) {
            if(blocks.get(player.getUniqueId()) != null) {
                if (!blocks.get(player.getUniqueId()).contains(block)) {
                    e.setCancelled(true);
                    player.sendMessage(ChatUtil.clr("&cYou can only break blocks placed by you!"));
                }
            } else {
                e.setCancelled(true);
                player.sendMessage(ChatUtil.clr("&cYou can only break blocks placed by you!"));
            }
        }
    }

    @EventHandler
    public void onFall(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (arenaManager.getArena(player) != null) {
            if (player.getLocation().getY() < 60) {
                Arena arena = arenaManager.getArena(player);
                player.teleport(arena.getLocation());
                if(blocks.get(player.getUniqueId()) == null) return;
                if (blocks.get(player.getUniqueId()).size() > 0) {
                    player.sendMessage(ChatUtil.clr("&7You &7bridged &b" + getBlocksAmount(player) + "&7 blocks!"));
                    resetPlayer(player);
                }
            }
            if(player.getLocation().getY() >= 150) {
                if(player.hasPermission("bp.admin")) return;
                player.sendMessage(ChatUtil.clr("&7Sorry, only &6&lDOGE COIN &7is allowed up here. (doge coin to the moon)"));
                e.getPlayer().teleport(new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY() - 4, player.getLocation().getZ()));
            }
        }
    }

    @EventHandler
    public void onStepGoldplate(PlayerInteractEvent e) {

        if (e.getAction() != Action.PHYSICAL) {
            return;
        }
        if (e.getClickedBlock().getType() != Material.GOLD_PLATE) {
            return;
        }

        Player p = e.getPlayer();
        if(arenaManager.getArena(p) == null) return;

        Arena arena = arenaManager.getArena(p);

        if (arenaManager.getArena(p) != null) {
            p.teleport(arena.getLocation());
            if(blocks.get(p.getUniqueId()) == null) return;
            if (blocks.get(p.getUniqueId()).size() > 0) {
                TitleAPI.sendTitle(p, 10, 60, 20, ChatUtil.clr("&e&lgg you did it!"), ChatUtil.clr("&fin: &c" + scoreboard.timeToString(timer.getTime(p)) + "!"));
                p.sendMessage(ChatUtil.clr("&7You completed the &ccourse &7in &c" + scoreboard.timeToString(timer.getTime(p)) + " &7now do better :)"));
                if(plugin.getBestAttempts().containsKey(p.getUniqueId())) {
                    if(plugin.getBestAttempts().get(p.getUniqueId()) > timer.getTime(p)) {
                        plugin.getBestAttempts().remove(p.getUniqueId());
                        plugin.getBestAttempts().put(p.getUniqueId(), timer.getTime(p));
                    }
                } else {
                    plugin.getBestAttempts().put(p.getUniqueId(), timer.getTime(p));
                }
                p.teleport(arenaManager.getArena(p).getLocation());
                resetPlayer(p);
            } else {
                p.teleport(arenaManager.getArena(p).getLocation());
            }
        }
    }

    public void resetPlayer(Player p) {
        timer.stopTimer(p);
        p.getInventory().clear();
        ArenaListener.giveStuff(p);
        FastBoard board = plugin.getBoards().get(p.getUniqueId());
        scoreboard.defaultScoreBoard(p);
        for (Block blocks : getBlocksPlacedBy(p)) {
            blocks.setType(Material.AIR);
        }
        System.out.println(1);
        blocks.get(p.getUniqueId()).clear();
        blocks.remove(p.getUniqueId());
    }


    public int getBlocksAmount(Player player) {
        return blocks.get(player.getUniqueId()).size();
    }
}
