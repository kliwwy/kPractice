package me.wilk3z.kpractice.commands;

import me.wilk3z.kpractice.Practice;
import me.wilk3z.kpractice.duels.DuelHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class cmdDuel implements CommandExecutor
{
    public Practice plugin;

    public cmdDuel(Practice plugin)
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
                    if(!p.getUniqueId().equals(target.getUniqueId()))
                    {
                        if(!plugin.getDuelManager().isInDuel(target))
                        {
                            if(!plugin.getDuelManager().hasDuelRequest(target, p))
                            {
                                duelHandler.openDuelMenu(p, target);
                                return true;
                            }
                            p.sendMessage(ChatColor.RED + "You've already requested to duel this player.");
                            return false;
                        }
                        p.sendMessage(ChatColor.RED + target.getName() + " is currently in a duel.");
                        return false;
                    }
                    p.sendMessage(ChatColor.RED + "You can't duel yourself.");
                    return false;
                }
                p.sendMessage(ChatColor.YELLOW + args[0] + ChatColor.RED + " is currently not online.");
                return false;
            }
            p.sendMessage(ChatColor.RED + "Usage: /duel <player>");
            return false;
        }
        return false;
    }
}
