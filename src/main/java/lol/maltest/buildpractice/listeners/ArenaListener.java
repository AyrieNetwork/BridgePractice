package lol.maltest.buildpractice.listeners;

import lol.maltest.buildpractice.BuildPractice;
import lol.maltest.buildpractice.arena.Arena;
import lol.maltest.buildpractice.arena.ArenaManager;
import lol.maltest.buildpractice.methods.Scoreboard;
import lol.maltest.buildpractice.utils.ChatUtil;
import lol.maltest.buildpractice.utils.GameInfo;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ArenaListener implements Listener {

    private final ArenaManager arenaManager;
    private BlockListener blockListener;
    private Scoreboard scoreboard;

    private final BuildPractice plugin;

    public ArenaListener(BuildPractice plugin) {
        this.plugin = plugin;
        this.arenaManager = plugin.getArenaManager();
        this.blockListener = plugin.getBlockListener();
        this.scoreboard = plugin.getScoreboard();
    }


    @EventHandler
    public void onhealth(EntityDamageEvent e) {
        e.setDamage(0);
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage("");
        if(!event.getPlayer().hasPermission(GameInfo.STAFF_PERMNODE.getValue())) {
            event.getPlayer().setGameMode(GameMode.SURVIVAL);
        }
        Arena found;
        for(Arena arena : arenaManager.getArenaList()) {
            if(arena.getLocation() == null) {
                arenaManager.removeArena(arena);
                continue;
            }
            if(arena.getPlayers().size() < 1) {
                found = arena;
                Player player = event.getPlayer();
                found.addPlayer(player);
                player.sendMessage(ChatUtil.clr("&7+&7&m--------------------------------------------------&7+"));
                ChatUtil.sendCenteredMessage(player, "&4");
                ChatUtil.sendCenteredMessage(player, "&f&l" + GameInfo.GAME_NAME.getValue());
                ChatUtil.sendCenteredMessage(player, "&4");
                ChatUtil.sendCenteredMessage(player, GameInfo.GAME_DESCRIPTION1.getValue());
                ChatUtil.sendCenteredMessage(player, GameInfo.GAME_DESCRIPTION2.getValue());
                ChatUtil.sendCenteredMessage(player, "&4");
                ChatUtil.sendCenteredMessage(player, "&f&lCreated By: &f" + GameInfo.GAME_CREDITS.getValue());
                ChatUtil.sendCenteredMessage(player, "&4");
                player.sendMessage(ChatUtil.clr("&7+&7&m--------------------------------------------------&7+"));
                player.getInventory().clear();
                giveStuff(player);
                player.teleport(found.getLocation());
                return;
            }
        }
    }

    public static void giveStuff(Player player) {
        ItemStack blocks = new ItemStack(Material.SANDSTONE, 64);
        ItemStack pickaxe = new ItemStack(Material.DIAMOND_PICKAXE, 1);
        ItemMeta pickaxeMeta = pickaxe.getItemMeta();

        ItemStack selectArena = new ItemStack(Material.EYE_OF_ENDER, 1);
        ItemMeta itemMeta = selectArena.getItemMeta();

        itemMeta.setDisplayName(ChatUtil.clr("&cChange Arena"));
        selectArena.setItemMeta(itemMeta);



        pickaxeMeta.addEnchant(Enchantment.DURABILITY, 10000, true);
        pickaxeMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        pickaxe.setItemMeta(pickaxeMeta);
        player.getInventory().setItem(0, blocks);
        player.getInventory().setItem(1, blocks);
        player.getInventory().setItem(2, pickaxe);
        player.getInventory().setItem(8, selectArena);
    }



    @EventHandler
    public void noFall(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player) {
            if(e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        if(arenaManager.getArena(player) != null) {
            player.teleport(arenaManager.getArena(player).getLocation());
            player.sendMessage(ChatUtil.clr("&chow tf did you die? report in discord pls"));
        }
    }

    @EventHandler
    public void playerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if(arenaManager.getArena(player) != null) {
            Arena playerArena = arenaManager.getArena(player);
            double X = playerArena.getLocation().getBlockX();
            double Z = playerArena.getLocation().getBlockX();
            if(player.getLocation().getBlockX() == X + 5 || player.getLocation().getBlockX() == X - 5 || player.getLocation().getBlockZ() == Z - 5 || player.getLocation().getBlockZ() >= 61) {
                player.teleport(playerArena.getLocation());
                player.sendMessage(ChatUtil.clr("&cYou can't go outside your island!"));
            }
        }
    }

    @EventHandler
    public void setMaxPlayers(ServerListPingEvent e) {
        e.setMaxPlayers(arenaManager.getArenaList().size());

    }

}
