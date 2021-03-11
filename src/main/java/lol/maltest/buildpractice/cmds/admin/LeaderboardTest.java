package lol.maltest.buildpractice.cmds.admin;

import lol.maltest.buildpractice.BuildPractice;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class LeaderboardTest implements CommandExecutor {

    public BuildPractice plugin;

    public LeaderboardTest(BuildPractice plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if(player.hasPermission("bp.admin")) {
                for(Map.Entry ye : entriesSortedByValues(plugin.getBestAttempts())) {
                    UUID uuid = (UUID) ye.getKey();
                    Player player1 = Bukkit.getPlayer(uuid);
                    player.sendMessage(player1 + " got " + Integer.parseInt(ye.getValue().toString()) / 1000 + " seconds");
                }
            }
        }
        return false;
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
}
