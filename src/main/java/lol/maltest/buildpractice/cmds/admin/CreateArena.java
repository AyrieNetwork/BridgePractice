package lol.maltest.buildpractice.cmds.admin;

import lol.maltest.buildpractice.BuildPractice;
import lol.maltest.buildpractice.arena.Arena;
import lol.maltest.buildpractice.arena.ArenaManager;
import lol.maltest.buildpractice.utils.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateArena implements CommandExecutor {

    public BuildPractice plugin;

    private ArenaManager arenaManager;

    public CreateArena(BuildPractice plugin) {
        this.arenaManager = plugin.getArenaManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(player.hasPermission("bp.admin") || player.isOp()) {
                if(args.length < 1) {
                    player.sendMessage(ChatUtil.clr("&cUsage: /createarena <ID>"));
                } else {
                    try {
                        String n = args[0];
                        Arena arena = new Arena(n, player.getLocation());
                        arenaManager.addArena(arena);
                        player.sendMessage(ChatUtil.clr("&aDone, created arena " + n));
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatUtil.clr("&cThat is not a number!"));
                    }
                }
            } else {
                player.sendMessage(ChatUtil.clr("&cNo permission!"));
            }
        }
        return false;
    }
}
