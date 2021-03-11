package lol.maltest.buildpractice.cmds.admin;

import lol.maltest.buildpractice.BuildPractice;
import lol.maltest.buildpractice.arena.ArenaManager;
import lol.maltest.buildpractice.utils.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ForceJoinArena implements CommandExecutor {

    private ArenaManager arenaManager;

    public ForceJoinArena(BuildPractice plugin) {
        this.arenaManager = plugin.getArenaManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if(player.hasPermission("bp.admin")) {
            if(args.length < 1) {
                player.sendMessage(ChatUtil.clr("&cUsage: /joinarena <ID>"));
            }
            if(arenaManager.getArena(player) != null) {
                player.sendMessage(ChatUtil.clr("&cYou are apart of an arena already, do /leavearena!"));
            } else{
                if(arenaManager.getArena(args[0]) != null) {
                    arenaManager.getArena(args[0]).addPlayer(player);
                    player.teleport(arenaManager.getArena(args[0]).getLocation());
                    player.sendMessage(ChatUtil.clr("&aYou are now apart of the arena called &c&n" + args[0]));
                } else {
                    player.sendMessage(ChatUtil.clr("&cThat arena doesn't exist!"));
                }
            }
        } else {
            player.sendMessage(ChatUtil.clr("&cNo permission!"));
        }
        return false;
    }
}
