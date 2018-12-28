package me.wilk3z.kpractice.commands;

import me.wilk3z.kpractice.Practice;
import me.wilk3z.kpractice.duels.DuelHandler;
import me.wilk3z.kpractice.duels.DuelRequest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class cmdAccept implements CommandExecutor
{
    public Practice plugin;

    public cmdAccept(Practice plugin)
    {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        DuelHandler duelHandler = new DuelHandler(plugin);
        if(sender instanceof Player)
        {
            Player p = (Player) sender;
            if(args.length >= 1)
            {
                Player target = Bukkit.getPlayer(args[0]);
                if(target != null)
                {
                    if(plugin.getDuelManager().hasDuelRequest(p, target))
                    {
                        DuelRequest duelRequest = plugin.getDuelManager().getDuelRequest(p, target);
                        duelHandler.acceptDuelRequest(p, duelRequest);
                        return true;
                    }
                    p.sendMessage(ChatColor.RED + "You don't have a duel request from " + ChatColor.YELLOW + target.getName() + ChatColor.RED + ".");
                    return false;
                }
                p.sendMessage(ChatColor.YELLOW + args[0] + ChatColor.RED + " is currently not online.");
                return false;
            }
            p.sendMessage(ChatColor.RED + "Usage: /accept <player>");
            return false;
        }
        return false;
    }
}
