package me.wilk3z.kpractice.commands;

import me.wilk3z.kpractice.Practice;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class cmdSetspawn implements CommandExecutor
{
    public Practice plugin;

    public cmdSetspawn(Practice plugin)
    {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if(sender instanceof Player)
        {
            Player p = (Player) sender;
            if(!p.isOp())
            {
                p.sendMessage(ChatColor.RED + "No permission.");
                return false;
            }
            if(args.length >= 1)
            {
                String type = args[0];
                if(type.equalsIgnoreCase("spawn"))
                {
                    plugin.setSpawn(p.getLocation());
                    p.sendMessage(ChatColor.YELLOW + "Set " + ChatColor.GREEN + "spawn" + ChatColor.YELLOW + " to your location.");
                    return true;
                }
                if(type.equalsIgnoreCase("editor"))
                {
                    plugin.setKitEditor(p.getLocation());
                    p.sendMessage(ChatColor.YELLOW + "Set " + ChatColor.GREEN + "kit editor" + ChatColor.YELLOW + " to your location.");
                    return true;
                }
                p.sendMessage(ChatColor.RED + "Usage: /setspawn <spawn,editor>");
                return false;
            }
            p.sendMessage(ChatColor.RED + "Usage: /setspawn <spawn,editor>");
            return false;
        }
        return false;
    }
}
