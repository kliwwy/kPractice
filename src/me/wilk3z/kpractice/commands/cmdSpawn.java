package me.wilk3z.kpractice.commands;

import me.wilk3z.kpractice.Practice;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class cmdSpawn implements CommandExecutor
{
    public Practice plugin;

    public cmdSpawn(Practice plugin)
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
            if(!plugin.hasSpawn()) p.sendMessage(ChatColor.RED + "Spawn is currently not set up, sending you to [0, " + p.getWorld().getHighestBlockYAt(0, 0) + ", 0].");
            plugin.teleportToSpawn(p);
            return true;
        }
        return false;
    }
}
