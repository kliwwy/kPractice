package me.wilk3z.kpractice.commands;

import me.wilk3z.kpractice.Practice;
import me.wilk3z.kpractice.builders.BuildHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class cmdBuild implements CommandExecutor
{
    public Practice plugin;

    public cmdBuild(Practice plugin)
    {
        this.plugin = plugin;
    }
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        BuildHandler buildHandler = new BuildHandler(plugin);
        if(sender instanceof Player)
        {
            Player p = (Player) sender;
            if(!p.isOp())
            {
                p.sendMessage(ChatColor.RED + "No permission.");
                return false;
            }
            buildHandler.setBuilder(p);
            return true;
        }
        return false;
    }
}
