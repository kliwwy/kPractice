package me.wilk3z.kpractice.commands;

import me.wilk3z.kpractice.Practice;
import me.wilk3z.kpractice.arenas.ArenaHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class cmdArena implements CommandExecutor
{
    public Practice plugin;

    public cmdArena(Practice plugin)
    {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        ArenaHandler arenaHandler = new ArenaHandler(plugin);
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
                String subCmd = args[0];
                if(subCmd.equalsIgnoreCase("create"))
                {
                    if(args.length >= 2)
                    {
                        String arenaName = args[1];
                        arenaHandler.createArena(p, arenaName);
                        return true;
                    }
                    p.sendMessage(ChatColor.RED + "Usage: /arena create <arena>");
                    return false;
                }
                if(subCmd.equalsIgnoreCase("delete"))
                {
                    if(args.length >= 2)
                    {
                        String arenaName = args[1];
                        arenaHandler.deleteArena(p, arenaName);
                        return true;
                    }
                    p.sendMessage(ChatColor.RED + "Usage: /arena delete <arena>");
                    return false;
                }
                if(subCmd.equals("reload"))
                {
                    arenaHandler.reloadArenas(p);
                    return true;
                }
                if(subCmd.equals("show"))
                {
                    arenaHandler.showArenas(p);
                    return true;
                }
                if(subCmd.equalsIgnoreCase("setspawn"))
                {
                    if(args.length >= 3)
                    {
                        String arenaName = args[1];
                        int spawn;
                        try
                        {
                            spawn = Integer.parseInt(args[2]);
                        }
                        catch(Exception e)
                        {
                            p.sendMessage(ChatColor.RED + "Arena spawn must be either " + ChatColor.YELLOW + "1" + ChatColor.RED + " or " + ChatColor.YELLOW + "2" + ChatColor.RED + ".");
                            return false;
                        }
                        arenaHandler.setArenaSpawn(p, arenaName, spawn);
                        return true;
                    }
                    p.sendMessage(ChatColor.RED + "Usage: /arena setspawn <arena> <1,2>");
                    return false;
                }
                if(subCmd.equalsIgnoreCase("usable"))
                {
                    if(args.length >= 2)
                    {
                        String arenaName = args[1];
                        arenaHandler.setUsable(p, arenaName);
                        return true;
                    }
                    p.sendMessage(ChatColor.RED + "Usage: /arena usable <arena>");
                    return false;
                }
                displayHelp(p);
                p.sendMessage(ChatColor.RED + "Unknown sub-command '" + ChatColor.YELLOW + subCmd + ChatColor.RED + "'.");
                return false;
            }
            displayHelp(p);
            return false;
        }
        return false;
    }

    public void displayHelp(Player p)
    {
        p.sendMessage(ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH.toString() + "-----------------------------------------------");
        p.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "Arena Commands");
        p.sendMessage(ChatColor.GOLD + "* " + ChatColor.GREEN + "/arena create <arena>" + ChatColor.DARK_GREEN + " - " + ChatColor.YELLOW + "Creates arena.");
        p.sendMessage(ChatColor.GOLD + "* " + ChatColor.GREEN + "/arena delete <arena>" + ChatColor.DARK_GREEN + " - " + ChatColor.YELLOW + "Deletes an existing arena.");
        p.sendMessage(ChatColor.GOLD + "* " + ChatColor.GREEN + "/arena setspawn <arena> <1,2>" + ChatColor.DARK_GREEN + " - " + ChatColor.YELLOW + "Sets a spawn position for an arena.");
        p.sendMessage(ChatColor.GOLD + "* " + ChatColor.GREEN + "/arena usable <arena>" + ChatColor.DARK_GREEN + " - " + ChatColor.YELLOW + "Makes an arena be able to be used in duels.");
        p.sendMessage(ChatColor.GOLD + "* " + ChatColor.GREEN + "/arena show" + ChatColor.DARK_GREEN + " - " + ChatColor.YELLOW + "Shows all arenas and their information.");
        p.sendMessage(ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH.toString() + "-----------------------------------------------");
    }
}
