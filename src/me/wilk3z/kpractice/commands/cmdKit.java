package me.wilk3z.kpractice.commands;

import me.wilk3z.kpractice.Practice;
import me.wilk3z.kpractice.kits.KitHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class cmdKit implements CommandExecutor
{
    public Practice plugin;

    public cmdKit(Practice plugin)
    {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        KitHandler kitHandler = new KitHandler(plugin);
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
                        String kitName = args[1];
                        kitHandler.createKit(p, kitName);
                        return true;
                    }
                    p.sendMessage(ChatColor.RED + "Usage: /kit create <kit>");
                    return false;
                }
                if(subCmd.equalsIgnoreCase("delete"))
                {
                    if(args.length >= 2)
                    {
                        String kitName = args[1];
                        kitHandler.deleteKit(p, kitName);
                        return true;
                    }
                    p.sendMessage(ChatColor.RED + "Usage: /kit delete <kit>");
                    return false;
                }
                if(subCmd.equalsIgnoreCase("reload"))
                {
                    kitHandler.reloadKits(p);
                    return true;
                }
                if(subCmd.equalsIgnoreCase("icon"))
                {
                    if(args.length >= 2)
                    {
                        String kitName = args[1];
                        kitHandler.setIcon(p, kitName);
                        return true;
                    }
                    p.sendMessage(ChatColor.RED + "Usage: /kit icon <kit>");
                    return false;
                }
                if(subCmd.equalsIgnoreCase("setinventory") || subCmd.equalsIgnoreCase("setinv"))
                {
                    if(args.length >= 2)
                    {
                        String type = args[1];
                        if(type.equalsIgnoreCase("default") || type.equalsIgnoreCase("editor"))
                        {
                            if(args.length >= 3)
                            {
                                String kitName = args[2];
                                kitHandler.setInventory(p, kitName, type);
                                return true;
                            }
                            p.sendMessage(ChatColor.RED + "Usage: /kit setinventory <default,editor> <kit>");
                            return false;
                        }
                        p.sendMessage(ChatColor.RED + "Usage: /kit setinventory <default,editor> <kit>");
                        return false;
                    }
                    p.sendMessage(ChatColor.RED + "Usage: /kit setinventory <default,editor> <kit>");
                    return false;
                }
                if(subCmd.equalsIgnoreCase("arenas"))
                {
                    if(args.length >= 2)
                    {
                        String kitName = args[1];
                        kitHandler.setArenas(p, kitName);
                        return true;
                    }
                    p.sendMessage(ChatColor.RED + "Usage: /kit arenas <kit>");
                    return false;
                }
                if(subCmd.equalsIgnoreCase("usable"))
                {
                    if(args.length >= 2)
                    {
                        String kitName = args[1];
                        kitHandler.setUsable(p, kitName);
                        return true;
                    }
                    p.sendMessage(ChatColor.RED + "Usage: /kit usable <kit>");
                    return false;
                }
                if(subCmd.equalsIgnoreCase("editable"))
                {
                    if(args.length >= 2)
                    {
                        String kitName = args[1];
                        kitHandler.setEditable(p, kitName);
                        return true;
                    }
                    p.sendMessage(ChatColor.RED + "Usage: /kit editable <kit>");
                    return false;
                }
                if(subCmd.equalsIgnoreCase("ranked"))
                {
                    if(args.length >= 2)
                    {
                        String kitName = args[1];
                        kitHandler.setRanked(p, kitName);
                        return true;
                    }
                    p.sendMessage(ChatColor.RED + "Usage: /kit ranked <kit>");
                    return false;
                }
                if(subCmd.equalsIgnoreCase("load"))
                {
                    if(args.length >= 2)
                    {
                        String kitName = args[1];
                        kitHandler.loadKit(p, kitName);
                        return true;
                    }
                    p.sendMessage(ChatColor.RED + "Usage: /kit load <kit>");
                    return false;
                }
                if(subCmd.equalsIgnoreCase("order"))
                {
                    kitHandler.orderKits(p);
                    return true;
                }
                if(subCmd.equalsIgnoreCase("show"))
                {
                    kitHandler.showKits(p);
                    return true;
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
        p.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "Kit Commands");
        p.sendMessage(ChatColor.GOLD + "* " + ChatColor.GREEN + "/kit create <kit>" + ChatColor.DARK_GREEN + " - " + ChatColor.YELLOW + "Creates kit.");
        p.sendMessage(ChatColor.GOLD + "* " + ChatColor.GREEN + "/kit delete <kit>" + ChatColor.DARK_GREEN + " - " + ChatColor.YELLOW + "Deletes an existing kit.");
        p.sendMessage(ChatColor.GOLD + "* " + ChatColor.GREEN + "/kit icon <kit>" + ChatColor.DARK_GREEN + " - " + ChatColor.YELLOW + "Sets the icon for a kit.");
        p.sendMessage(ChatColor.GOLD + "* " + ChatColor.GREEN + "/kit setinventory " + ChatColor.DARK_GREEN + "(setinv)" + ChatColor.GREEN + " <default,editor> <kit>" + ChatColor.DARK_GREEN + " - " + ChatColor.YELLOW + "Sets the default or editor inventory for a kit.");
        p.sendMessage(ChatColor.GOLD + "* " + ChatColor.GREEN + "/kit arenas <kit>" + ChatColor.DARK_GREEN + " - " + ChatColor.YELLOW + "Sets the arenas for a kit.");
        p.sendMessage(ChatColor.GOLD + "* " + ChatColor.GREEN + "/kit usable <kit>" + ChatColor.DARK_GREEN + " - " + ChatColor.YELLOW + "Makes a kit be able to be played.");
        p.sendMessage(ChatColor.GOLD + "* " + ChatColor.GREEN + "/kit editable <kit>" + ChatColor.DARK_GREEN + " - " + ChatColor.YELLOW + "Makes a kit be able to be edited.");
        p.sendMessage(ChatColor.GOLD + "* " + ChatColor.GREEN + "/kit ranked <kit>" + ChatColor.DARK_GREEN + " - " + ChatColor.YELLOW + "Makes a kit be able to be played in ranked.");
        p.sendMessage(ChatColor.GOLD + "* " + ChatColor.GREEN + "/kit load <kit>" + ChatColor.DARK_GREEN + " - " + ChatColor.YELLOW + "Loads a kit's default inventory.");
        p.sendMessage(ChatColor.GOLD + "* " + ChatColor.GREEN + "/kit show" + ChatColor.DARK_GREEN + " - " + ChatColor.YELLOW + "Shows all kits and their information.");
        p.sendMessage(ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH.toString() + "-----------------------------------------------");
    }
}
