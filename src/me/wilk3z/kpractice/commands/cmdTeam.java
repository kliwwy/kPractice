package me.wilk3z.kpractice.commands;

import me.wilk3z.kpractice.Practice;
import me.wilk3z.kpractice.teams.TeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class cmdTeam implements CommandExecutor
{
    public Practice plugin;

    public cmdTeam(Practice plugin)
    {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        TeamHandler teamHandler = new TeamHandler(plugin);
        if(sender instanceof Player)
        {
            Player p = (Player) sender;
            if(args.length >= 1)
            {
                String subCmd = args[0];
                if (subCmd.equalsIgnoreCase("create"))
                {
                    teamHandler.createTeam(p);
                    return true;
                }
                if (subCmd.equalsIgnoreCase("disband"))
                {
                    teamHandler.disbandTeam(p);
                    return true;
                }
                if (subCmd.equalsIgnoreCase("leader"))
                {
                    if(args.length >= 2)
                    {
                        Player target = Bukkit.getPlayer(args[1]);
                        if(target != null)
                        {
                            teamHandler.setLeader(p, target);
                            return true;
                        }
                        p.sendMessage(ChatColor.YELLOW + args[1] + ChatColor.RED + " is currently not online.");
                        return false;
                    }
                    p.sendMessage(ChatColor.RED + "Usage: /team leader <player>");
                    return false;
                }
                if (subCmd.equalsIgnoreCase("kick"))
                {
                    if(args.length >= 2)
                    {
                        Player target = Bukkit.getPlayer(args[1]);
                        if(target != null)
                        {
                            teamHandler.kickFromTeam(p, target);
                            return true;
                        }
                        p.sendMessage(ChatColor.YELLOW + args[1] + ChatColor.RED + " is currently not online.");
                        return true;
                    }
                    p.sendMessage(ChatColor.RED + "Usage: /team kick <player>");
                    return false;
                }
                if (subCmd.equalsIgnoreCase("promote"))
                {
                    if(args.length >= 2)
                    {
                        Player target = Bukkit.getPlayer(args[1]);
                        if(target != null)
                        {
                            teamHandler.promote(p, target);
                            return true;
                        }
                        p.sendMessage(ChatColor.YELLOW + args[1] + ChatColor.RED + " is currently not online.");
                        return true;
                    }
                    p.sendMessage(ChatColor.RED + "Usage: /team promote <player>");
                    return false;
                }
                if (subCmd.equalsIgnoreCase("demote"))
                {
                    if(args.length >= 2)
                    {
                        Player target = Bukkit.getPlayer(args[1]);
                        if(target != null)
                        {
                            teamHandler.demote(p, target);
                            return true;
                        }
                        p.sendMessage(ChatColor.YELLOW + args[1] + ChatColor.RED + " is currently not online.");
                        return true;
                    }
                    p.sendMessage(ChatColor.RED + "Usage: /team demote <player>");
                    return false;
                }
                if (subCmd.equalsIgnoreCase("invite"))
                {
                    if(args.length >= 2)
                    {
                        Player target = Bukkit.getPlayer(args[1]);
                        if(target != null)
                        {
                            teamHandler.invite(p, target);
                            return true;
                        }
                        p.sendMessage(ChatColor.YELLOW + args[1] + ChatColor.RED + " is currently not online.");
                        return true;
                    }
                    p.sendMessage(ChatColor.RED + "Usage: /team invite <player>");
                    return false;
                }
                if (subCmd.equalsIgnoreCase("join"))
                {
                    if(args.length >= 2)
                    {
                        Player target = Bukkit.getPlayer(args[1]);
                        if(target != null)
                        {
                            teamHandler.joinTeam(p, target);
                            return true;
                        }
                        p.sendMessage(ChatColor.YELLOW + args[1] + ChatColor.RED + " is currently not online.");
                        return true;
                    }
                    p.sendMessage(ChatColor.RED + "Usage: /team join <player>");
                    return false;
                }
                if (subCmd.equalsIgnoreCase("quit"))
                {
                    teamHandler.quitTeam(p);
                    return true;
                }
                if (subCmd.equalsIgnoreCase("open"))
                {
                    teamHandler.open(p);
                    return true;
                }
                if (subCmd.equalsIgnoreCase("close"))
                {
                    teamHandler.close(p);
                    return true;
                }
                if (subCmd.equalsIgnoreCase("info") || subCmd.equalsIgnoreCase("i"))
                {
                    if(args.length >= 2)
                    {
                        Player target = Bukkit.getPlayer(args[1]);
                        if(target != null)
                        {
                            teamHandler.showTeamInfo(p, target);
                            return true;
                        }
                        p.sendMessage(ChatColor.YELLOW + args[1] + ChatColor.RED + " is currently not online.");
                        return false;
                    }
                    teamHandler.showTeamInfo(p, p);
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
        p.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "Team Commands");
        p.sendMessage(ChatColor.DARK_PURPLE + "Use " + ChatColor.LIGHT_PURPLE + "@" + ChatColor.DARK_PURPLE + " at the start of a chat message to send a team message.");
        p.sendMessage(ChatColor.GOLD + "* " + ChatColor.GREEN + "/team create" + ChatColor.DARK_GREEN + " - " + ChatColor.YELLOW + "Creates kit.");
        p.sendMessage(ChatColor.GOLD + "* " + ChatColor.GREEN + "/team disband" + ChatColor.DARK_GREEN + " - " + ChatColor.YELLOW + "Deletes an existing kit.");
        p.sendMessage(ChatColor.GOLD + "* " + ChatColor.GREEN + "/team leader <player>" + ChatColor.DARK_GREEN + " - " + ChatColor.YELLOW + "Sets the arenas for a kit.");
        p.sendMessage(ChatColor.GOLD + "* " + ChatColor.GREEN + "/team kick <player>" + ChatColor.DARK_GREEN + " - " + ChatColor.YELLOW + "Sets the arenas for a kit.");
        p.sendMessage(ChatColor.GOLD + "* " + ChatColor.GREEN + "/team promote <player>" + ChatColor.DARK_GREEN + " - " + ChatColor.YELLOW + "Sets the arenas for a kit.");
        p.sendMessage(ChatColor.GOLD + "* " + ChatColor.GREEN + "/team demote <player>" + ChatColor.DARK_GREEN + " - " + ChatColor.YELLOW + "Sets the arenas for a kit.");
        p.sendMessage(ChatColor.GOLD + "* " + ChatColor.GREEN + "/team invite <player>" + ChatColor.DARK_GREEN + " - " + ChatColor.YELLOW + "Sets the arenas for a kit.");
        p.sendMessage(ChatColor.GOLD + "* " + ChatColor.GREEN + "/team join <player>" + ChatColor.DARK_GREEN + " - " + ChatColor.YELLOW + "Sets the icon for a kit.");
        p.sendMessage(ChatColor.GOLD + "* " + ChatColor.GREEN + "/team quit" + ChatColor.DARK_GREEN + " - " + ChatColor.YELLOW + "Sets the default or editor inventory for a kit.");
        p.sendMessage(ChatColor.GOLD + "* " + ChatColor.GREEN + "/team open" + ChatColor.DARK_GREEN + " - " + ChatColor.YELLOW + "Sets the arenas for a kit.");
        p.sendMessage(ChatColor.GOLD + "* " + ChatColor.GREEN + "/team close" + ChatColor.DARK_GREEN + " - " + ChatColor.YELLOW + "Sets the arenas for a kit.");
        p.sendMessage(ChatColor.GOLD + "* " + ChatColor.GREEN + "/team chat <msg>" + ChatColor.DARK_GREEN + " - " + ChatColor.YELLOW + "Sets the arenas for a kit.");
        p.sendMessage(ChatColor.GOLD + "* " + ChatColor.GREEN + "/team info [player]" + ChatColor.DARK_GREEN + " - " + ChatColor.YELLOW + "Sets the arenas for a kit.");
        p.sendMessage(ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH.toString() + "-----------------------------------------------");
    }
}
