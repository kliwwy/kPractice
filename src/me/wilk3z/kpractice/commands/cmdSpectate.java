package me.wilk3z.kpractice.commands;

import me.wilk3z.kpractice.Practice;
import me.wilk3z.kpractice.spectators.SpectatorHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class cmdSpectate implements CommandExecutor
{
    public Practice plugin;

    public cmdSpectate(Practice plugin)
    {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        SpectatorHandler spectatorHandler = new SpectatorHandler(plugin);
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
                        spectatorHandler.spectateDuel(p, target);
                        return true;
                    }
                    p.sendMessage(ChatColor.RED + "You can't spectate yourself.");
                    return false;
                }
                p.sendMessage(ChatColor.YELLOW + args[0] + ChatColor.RED + " is currently not online.");
                return false;
            }
            p.sendMessage(ChatColor.RED + "Usage: /spectate <player>");
            return false;
        }
        return false;
    }
}
