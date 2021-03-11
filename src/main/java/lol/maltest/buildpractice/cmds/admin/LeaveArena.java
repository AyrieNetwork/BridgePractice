package lol.maltest.buildpractice.cmds.admin;

import lol.maltest.buildpractice.BuildPractice;
import lol.maltest.buildpractice.arena.Arena;
import lol.maltest.buildpractice.arena.ArenaManager;
import lol.maltest.buildpractice.utils.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveArena implements CommandExecutor {
    private ArenaManager arenaManager;


    public LeaveArena(BuildPractice plugin) {
        this.arenaManager = plugin.getArenaManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(player.hasPermission("bp.admin")) {
                if(arenaManager.getArena(player) != null) {
                    arenaManager.getArena(player).removePlayer(player);
                    player.sendMessage(ChatUtil.clr("&cDone, removed you from your island!"));
                } else {
                    player.sendMessage(ChatUtil.clr("&cYou already aren't in an island!"));
                }
            } else {
                player.sendMessage(ChatUtil.clr("&cNo permission!"));
            }
        }
        return false;
    }
}
