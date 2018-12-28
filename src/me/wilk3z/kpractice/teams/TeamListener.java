package me.wilk3z.kpractice.teams;

import me.wilk3z.kpractice.Practice;
import me.wilk3z.kpractice.queues.QueueHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class TeamListener implements Listener
{
    public Practice plugin;

    public TeamListener(Practice plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void createTeam(PlayerInteractEvent e)
    {
        TeamHandler teamHandler = new TeamHandler(plugin);
        Player p = e.getPlayer();
        if(!plugin.getDuelManager().isInDuel(p) && !plugin.getTeamManager().isOnTeam(p) && !plugin.getSpectatorManager().isSpectating(p) && !plugin.getKitManager().isEditingKit(p) && !plugin.getBuildManager().isBuilder(p))
        {
            if(e.getItem() == null) return;
            if(!e.getItem().hasItemMeta()) return;
            if(!e.getItem().getItemMeta().hasDisplayName()) return;
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR))
            {
                ItemStack item = e.getItem();
                if(item.getType().equals(Material.SKULL_ITEM))
                {
                    if(item.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Create Team"))
                    {
                        e.setCancelled(true);
                        teamHandler.createTeam(p);
                    }
                }
            }
        }
    }

    @EventHandler
    public void showTeamInfo(PlayerInteractEvent e)
    {
        TeamHandler teamHandler = new TeamHandler(plugin);
        Player p = e.getPlayer();
        if(plugin.getTeamManager().isOnTeam(p))
        {
            if(e.getItem() == null) return;
            if(!e.getItem().hasItemMeta()) return;
            if(!e.getItem().getItemMeta().hasDisplayName()) return;
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR))
            {
                ItemStack item = e.getItem();
                if(item.getType().equals(Material.NETHER_STAR))
                {
                    e.setCancelled(true);
                    teamHandler.showTeamInfo(p, p);
                }
            }
        }
    }

    @EventHandler
    public void showOtherTeams(PlayerInteractEvent e)
    {
        TeamHandler teamHandler = new TeamHandler(plugin);
        Player p = e.getPlayer();
        if(plugin.getTeamManager().isOnTeam(p))
        {
            if(e.getItem() == null) return;
            if(!e.getItem().hasItemMeta()) return;
            if(!e.getItem().getItemMeta().hasDisplayName()) return;
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR))
            {
                ItemStack item = e.getItem();
                if(item.getType().equals(Material.EYE_OF_ENDER))
                {
                    e.setCancelled(true);
                    teamHandler.showOtherTeams(p, 0);
                }
            }
        }
    }

    @EventHandler
    public void disbandTeam(PlayerInteractEvent e)
    {
        TeamHandler teamHandler = new TeamHandler(plugin);
        Player p = e.getPlayer();
        if(plugin.getTeamManager().isOnTeam(p))
        {
            if(e.getItem() == null) return;
            if(!e.getItem().hasItemMeta()) return;
            if(!e.getItem().getItemMeta().hasDisplayName()) return;
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR))
            {
                ItemStack item = e.getItem();
                if(item.getType().equals(Material.FIRE))
                {
                    e.setCancelled(true);
                    teamHandler.quitTeam(p);
                }
            }
        }
    }

    @EventHandler
    public void openTeamEvents(PlayerInteractEvent e)
    {
        TeamHandler teamHandler = new TeamHandler(plugin);
        Player p = e.getPlayer();
        if(plugin.getTeamManager().isOnTeam(p))
        {
            if(e.getItem() == null) return;
            if(!e.getItem().hasItemMeta()) return;
            if(!e.getItem().getItemMeta().hasDisplayName()) return;
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR))
            {
                ItemStack item = e.getItem();
                if(item.getType().equals(Material.GOLD_SWORD))
                {
                    e.setCancelled(true);
                    Team team = plugin.getTeamManager().getTeam(p);
                    if(team.isLeader(p))
                    {
                        if(team.getSize() >= 2)
                        {
                            teamHandler.showTeamEvents(p);
                            return;
                        }
                        p.sendMessage(ChatColor.RED + "You must have at least 2 players on your team to start team events.");
                        return;
                    }
                    p.sendMessage(ChatColor.RED + "You must be the leader of the team to start team events.");
                }
            }
        }
    }
}
