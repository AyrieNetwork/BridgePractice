package lol.maltest.buildpractice.cmds.staff;

import lol.maltest.buildpractice.utils.ChatUtil;
import lol.maltest.buildpractice.utils.GameInfo;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.nio.Buffer;

public class GameMode implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if(player.hasPermission(GameInfo.STAFF_PERMNODE.getValue())) {
                if(args.length == 0) {
                    player.sendMessage(ChatUtil.clr("&cUsage: /gm <gamemode> [player]"));
                    player.sendMessage(ChatUtil.clr("&cPossible gamemodes: &7CREATIVE, SURVIVAL, SPECTATOR"));
                } else {
                    if(args.length == 1) {
                        if (checkGamemodeArg(args, player)) return false;
                        player.sendMessage(ChatUtil.clr("&cI don't understand the gamemode: &7" + args[0]));
                    } else {
                        Player target = Bukkit.getPlayer(args[1]);
                        if(target == null) {
                            player.sendMessage(ChatUtil.clr("&7I can't find a player with the name: " + args[1]));
                        } else {
                            if (checkGamemodeArg(args, target)) return false;
                        }
                    }
                }
            } else {
                player.sendMessage(ChatUtil.clr(GameInfo.STAFF_NOPERMISSION.getValue()));
            }
        }
        return false;
    }

    private boolean checkGamemodeArg(String[] args, Player target) {
        if(args[0].startsWith("su")) {
            target.setGameMode(org.bukkit.GameMode.SURVIVAL);
            target.sendMessage(ChatUtil.clr("&7Your &cgamemode &7was updated to &cSURVIVAL!"));
            return true;
        }
        if(args[0].startsWith("sp")) {
            target.setGameMode(org.bukkit.GameMode.SPECTATOR);
            target.sendMessage(ChatUtil.clr("&7Your &cgamemode &7was updated to &cSPECTATOR!"));
            return true;
        }
        if(args[0].startsWith("c")) {
            target.setGameMode(org.bukkit.GameMode.CREATIVE);
            target.sendMessage(ChatUtil.clr("&7Your &cgamemode &7was updated to &cCREATIVE!"));
            return true;
        }
        return false;
    }
}
