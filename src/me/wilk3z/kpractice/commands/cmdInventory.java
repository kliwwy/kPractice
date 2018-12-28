package me.wilk3z.kpractice.commands;

import me.wilk3z.kpractice.Practice;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class cmdInventory implements CommandExecutor
{
    public Practice plugin;

    public cmdInventory(Practice plugin)
    {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if(sender instanceof Player)
        {
            Player p = (Player) sender;
            if(args.length >= 1)
            {
                UUID uuid;
                try
                {
                    uuid = UUID.fromString(args[0]);
                }
                catch(Exception e)
                {
                    p.sendMessage(ChatColor.RED + "UUID must be a valid UUID.");
                    return false;
                }
                if(plugin.getDuelManager().hasPostDuelInventory(uuid))
                {
                    p.openInventory(plugin.getDuelManager().getPostDuelInventory(uuid));
                    return true;
                }
                p.sendMessage(ChatColor.RED + "No post duel inventory found for " + uuid.toString() + ".");
                return false;
            }
            p.sendMessage(ChatColor.RED + "Usage: /_ <uuid>");
            return false;
        }
        return false;
    }
}
