package me.wilk3z.kpractice.commands;

import me.wilk3z.kpractice.Practice;
import me.wilk3z.kpractice.duels.DuelHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class cmdDeny implements CommandExecutor
{
    public Practice plugin;

    public cmdDeny(Practice plugin)
    {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        DuelHandler duelHandler = new DuelHandler(plugin);
        if(sender instanceof Player)
        {
            Player p = (Player) sender;
        }
        return false;
    }
}
