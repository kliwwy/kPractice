package me.wilk3z.kpractice.teams;

import com.mysql.jdbc.Buffer;
import me.wilk3z.kpractice.Practice;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TeamHandler
{
    public Practice plugin;

    public TeamHandler(Practice plugin)
    {
        this.plugin = plugin;
    }

    public void createTeam(Player p)
    {
        if(plugin.getTeamManager().isOnTeam(p))
        {
            p.sendMessage(ChatColor.RED + "You're already on a team.");
            return;
        }
        Team team = new Team(p);
        plugin.getTeamManager().addTeam(team);
        plugin.getTeamManager().addToTeam(p, team);
        p.sendMessage(ChatColor.YELLOW + "You've created a team.");
        giveTeamItems(p);
        updateTeams();
    }

    public void invite(Player p, Player target)
    {
        if(!plugin.getTeamManager().isOnTeam(p))
        {
            p.sendMessage(ChatColor.RED + "You're not on a team.");
            return;
        }
        if(plugin.getTeamManager().isOnTeam(target))
        {
            p.sendMessage(ChatColor.RED + target.getName() + " is already on a team.");
            return;
        }
        Team team = plugin.getTeamManager().getTeam(p);
        if(team.isMember(target))
        {
            p.sendMessage(ChatColor.RED + target.getName() + " is already on the team.");
            return;
        }
        if(!team.isLeader(p) && !team.isCaptain(p))
        {
            p.sendMessage("You must be leader or a captain of the team to invite other players to join.");
            return;
        }
        if(p.getUniqueId().equals(target.getUniqueId()))
        {
            p.sendMessage(ChatColor.RED + "You can't invite yourself to the team.");
            return;
        }
        if(team.isInvited(target))
        {
            p.sendMessage(ChatColor.RED + target.getName() + " is already invited to the team.");
            return;
        }
        team.addInvite(target);
        team.sendTeamMessage(ChatColor.GREEN + p.getName() + ChatColor.YELLOW + " has invited " + ChatColor.GREEN + target.getName() + ChatColor.YELLOW + " to the team.");
        target.sendMessage(ChatColor.YELLOW + "You've been invited to join " + ChatColor.GREEN + p.getName() + "'s" + ChatColor.YELLOW + " team.");
    }

    public void joinTeam(Player p, Player target)
    {
        if(!plugin.getTeamManager().isOnTeam(target))
        {
            p.sendMessage(ChatColor.RED + target.getName() + " is currently not on a team.");
            return;
        }
        Team team = plugin.getTeamManager().getTeam(target);
        if(!team.isOpen())
        {
            if(!team.isInvited(p))
            {
                p.sendMessage(ChatColor.RED + "You haven't been invited to join " + target.getName() + "'s team.");
                return;
            }
            team.removeInvite(p);
        }
        team.addMember(p);
        plugin.getTeamManager().addToTeam(p, team);
        team.sendTeamMessage(ChatColor.GREEN + p.getName() + ChatColor.YELLOW + " has joined the team.");
        giveTeamItems(p);
        giveTeamItems(team.getLeader());
    }

    public void quitTeam(Player p)
    {
        if(!plugin.getTeamManager().isOnTeam(p))
        {
            p.sendMessage(ChatColor.RED + "You're not on a team.");
            return;
        }
        Team team = plugin.getTeamManager().getTeam(p);
        if(team.isLeader(p))
        {
            disbandTeam(p);
            return;
        }
        team.removeMember(p);
        plugin.getTeamManager().removeFromTeam(p);
        plugin.giveSpawnItems(p);
        p.sendMessage(ChatColor.YELLOW + "You've left the team.");
        team.sendTeamMessage(ChatColor.GREEN + p.getName() + ChatColor.YELLOW + " has left the team.");
        giveTeamItems(team.getLeader());
    }

    public void kickFromTeam(Player p, Player target)
    {
        if(!plugin.getTeamManager().isOnTeam(p))
        {
            p.sendMessage(ChatColor.RED + "You're not on a team.");
            return;
        }
        Team team = plugin.getTeamManager().getTeam(p);
        if(!team.isMember(target))
        {
            p.sendMessage(ChatColor.RED + target.getName() + " isn't on the team.");
            return;
        }
        if(!team.isLeader(p) && !team.isCaptain(p))
        {
            p.sendMessage(ChatColor.RED + "You must be leader or a captain of the team to kick team members.");
            return;
        }
        if(p.getUniqueId().equals(target.getUniqueId()))
        {
            p.sendMessage(ChatColor.RED + "You can't kick yourself from the team.");
            return;
        }
        if(team.isLeader(target))
        {
            p.sendMessage(ChatColor.RED + "You can't kick the leader of the team.");
            return;
        }
        plugin.getTeamManager().removeFromTeam(target);
        team.removeMember(target);
        plugin.giveSpawnItems(target);
        giveTeamItems(team.getLeader());
        team.sendTeamMessage(ChatColor.GREEN + p.getName() + ChatColor.YELLOW + " has kicked " + ChatColor.GREEN + target.getName() + ChatColor.YELLOW + " from the team.");
        target.sendMessage(ChatColor.YELLOW + "You've been kicked from the team.");
    }

    public void disbandTeam(Player p)
    {
        if(!plugin.getTeamManager().isOnTeam(p))
        {
            p.sendMessage(ChatColor.RED + "You're not on a team.");
            return;
        }
        Team team = plugin.getTeamManager().getTeam(p);
        if(!team.isLeader(p))
        {
            p.sendMessage(ChatColor.RED + "You're not the leader of this team.");
            return;
        }
        for(Player member : team.getMembers())
        {
            plugin.getTeamManager().removeFromTeam(member);
            plugin.giveSpawnItems(member);
        }
        plugin.getTeamManager().removeTeam(team);
        team.sendTeamMessage(ChatColor.GREEN + p.getName() + ChatColor.YELLOW + " has disbanded the team.");
        p.sendMessage(ChatColor.YELLOW + "You've disbanded the team.");
        updateTeams();
    }

    public void setLeader(Player p, Player target)
    {
        if(!plugin.getTeamManager().isOnTeam(p))
        {
            p.sendMessage(ChatColor.RED + "You're not on a team.");
            return;
        }
        Team team = plugin.getTeamManager().getTeam(p);
        if(!team.isLeader(p))
        {
            p.sendMessage(ChatColor.RED + "You're not the leader of this team.");
            return;
        }
        if(!team.isMember(target))
        {
            p.sendMessage(ChatColor.RED + target.getName() + " isn't on the team.");
            return;
        }
        team.setLeader(target);
        team.sendTeamMessage(ChatColor.GREEN + p.getName() + ChatColor.YELLOW + " has transferred leadership of the team to " + ChatColor.GREEN + target.getName() + ChatColor.YELLOW + ".");
        target.sendMessage(ChatColor.YELLOW + "You're now the leader of the team.");
    }

    public void promote(Player p, Player target)
    {
        if(!plugin.getTeamManager().isOnTeam(p))
        {
            p.sendMessage(ChatColor.RED + "You're not on a team.");
            return;
        }
        Team team = plugin.getTeamManager().getTeam(p);
        if(!team.isLeader(p))
        {
            p.sendMessage(ChatColor.RED + "You're not the leader of this team.");
            return;
        }
        if(!team.isMember(target))
        {
            p.sendMessage(ChatColor.RED + target.getName() + " isn't on the team.");
            return;
        }
        if(team.isCaptain(target))
        {
            p.sendMessage(ChatColor.RED + target.getName() + " is already a captain on the team.");
            return;
        }
        team.setCaptain(target, true);
        team.sendTeamMessage(ChatColor.GREEN + p.getName() + ChatColor.YELLOW + " has promoted " + ChatColor.GREEN + target.getName() + ChatColor.YELLOW + " to one of the captains of the team.");
        target.sendMessage(ChatColor.YELLOW + "You've been promoted to one of the captains of the team, you can now invite players to the team and kick players from the team.");
    }

    public void demote(Player p, Player target)
    {
        if(!plugin.getTeamManager().isOnTeam(p))
        {
            p.sendMessage(ChatColor.RED + "You're not on a team.");
            return;
        }
        Team team = plugin.getTeamManager().getTeam(p);
        if(!team.isLeader(p))
        {
            p.sendMessage(ChatColor.RED + "You're not the leader of this team.");
            return;
        }
        if(!team.isMember(target))
        {
            p.sendMessage(ChatColor.RED + target.getName() + " isn't on the team.");
            return;
        }
        if(!team.isCaptain(target))
        {
            p.sendMessage(ChatColor.RED + target.getName() + " isn't one of the captains of the team.");
            return;
        }
        team.setCaptain(target, false);
        team.sendTeamMessage(ChatColor.GREEN + p.getName() + ChatColor.YELLOW + " has demoted " + ChatColor.GREEN + target.getName() + ChatColor.YELLOW + " from being one of the captains of the team.");
        target.sendMessage(ChatColor.YELLOW + "You've been demoted from being one of the captains of the team.");
    }

    public void open(Player p)
    {
        if(!plugin.getTeamManager().isOnTeam(p))
        {
            p.sendMessage(ChatColor.RED + "You're not on a team.");
            return;
        }
        Team team = plugin.getTeamManager().getTeam(p);
        if(!team.isLeader(p))
        {
            p.sendMessage(ChatColor.RED + "You're not the leader of this team.");
            return;
        }
        if(team.isOpen())
        {
            p.sendMessage(ChatColor.RED + "The team is already opened.");
            return;
        }
        team.setOpen(true);
        team.sendTeamMessage(ChatColor.GREEN + p.getName() + ChatColor.YELLOW + " has opened the team.");
    }

    public void close(Player p)
    {
        if(!plugin.getTeamManager().isOnTeam(p))
        {
            p.sendMessage(ChatColor.RED + "You're not on a team.");
            return;
        }
        Team team = plugin.getTeamManager().getTeam(p);
        if(!team.isLeader(p))
        {
            p.sendMessage(ChatColor.RED + "You're not the leader of this team.");
            return;
        }
        if(!team.isOpen())
        {
            p.sendMessage(ChatColor.RED + "The team is already closed.");
            return;
        }
        team.setOpen(false);
        team.sendTeamMessage(ChatColor.GREEN + p.getName() + ChatColor.YELLOW + " has closed the team.");
    }

    public void giveTeamItems(Player p)
    {
        p.getInventory().setArmorContents(null);
        p.getInventory().clear();

        Team team = plugin.getTeamManager().getTeam(p);

        ItemStack teamInfo = new ItemStack(Material.NETHER_STAR);
        ItemMeta teamInfom = teamInfo.getItemMeta();
        teamInfom.setDisplayName(ChatColor.GREEN + team.getName() + ChatColor.BLUE + "'s" + " Team");
        teamInfo.setItemMeta(teamInfom);

        ItemStack quitTeam = new ItemStack(Material.FIRE);
        ItemMeta quitTeamm = quitTeam.getItemMeta();
        quitTeamm.setDisplayName(ChatColor.RED + (team.isLeader(p) ? "Disband Team" : "Leave Team"));
        quitTeam.setItemMeta(quitTeamm);

        ItemStack showTeams = new ItemStack(Material.EYE_OF_ENDER);
        ItemMeta showTeamsm = showTeams.getItemMeta();
        showTeamsm.setDisplayName(ChatColor.BLUE + "Show Other Teams");
        showTeams.setItemMeta(showTeamsm);

        p.getInventory().setItem(0, teamInfo);
        p.getInventory().setItem(1, showTeams);
        p.getInventory().setItem(2, quitTeam);

        if(team.getSize() >= 2 && team.isLeader(p))
        {
            ItemStack teamEvents = new ItemStack(Material.GOLD_SWORD);
            ItemMeta teamEventsm = teamEvents.getItemMeta();
            teamEventsm.setDisplayName(ChatColor.GOLD + "Start Team Event");
            teamEvents.setItemMeta(teamEventsm);

            ItemStack unrankedQueue = new ItemStack(Material.IRON_SWORD);
            ItemMeta unrankedQueuem = unrankedQueue.getItemMeta();
            unrankedQueuem.setDisplayName(ChatColor.BLUE + "Un-Ranked Team Queue");
            unrankedQueue.setItemMeta(unrankedQueuem);

            ItemStack rankedQueue = new ItemStack(Material.DIAMOND_SWORD);
            ItemMeta rankedQueuem = rankedQueue.getItemMeta();
            rankedQueuem.setDisplayName(ChatColor.GREEN + "Ranked Team Queue");
            rankedQueue.setItemMeta(rankedQueuem);

            p.getInventory().setItem(6, teamEvents);
            p.getInventory().setItem(7, unrankedQueue);
            p.getInventory().setItem(8, rankedQueue);
        }

        p.updateInventory();
    }

    public void showTeamInfo(Player p, Player target)
    {
        if(!plugin.getTeamManager().isOnTeam(target))
        {
            p.sendMessage(ChatColor.RED + target.getName() + " is currently not on a team.");
            return;
        }
        Team team = plugin.getTeamManager().getTeam(target);
        StringBuilder build = new StringBuilder();
        for(int i = 0; i < team.getSize(); i++)
        {
            if(i == team.getSize() - 1) build.append(ChatColor.GREEN + team.getMembers().get(i).getName());
            else build.append(ChatColor.GREEN + team.getMembers().get(i).getName() + ChatColor.YELLOW + ", ");
        }
        p.sendMessage(ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH.toString() + "-----------------------------------------------");
        p.sendMessage((team.getLeader() != null ? ChatColor.GREEN + team.getLeader().getName() : team.getName()) + ChatColor.YELLOW + "'s " + "Team [" + ChatColor.GREEN + team.getSize() + ChatColor.YELLOW + "/" + ChatColor.GREEN + "10" + ChatColor.YELLOW + "]");
        p.sendMessage(ChatColor.YELLOW + "Leader: " + ChatColor.GREEN + (team.getLeader() != null ? team.getLeader().getName() : team.getName()));
        p.sendMessage(ChatColor.YELLOW + "Members: " + build.toString());
        p.sendMessage(ChatColor.YELLOW + "Status: " + (team.isOpen() ? ChatColor.GREEN + "Opened" : ChatColor.RED + "Closed"));
        p.sendMessage(ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH.toString() + "-----------------------------------------------");
    }

    public void showOtherTeams(Player p, int page)
    {
        Inventory otherTeams = Bukkit.createInventory(null, 27, ChatColor.BLUE + "Teams (" + plugin.getTeamManager().getTeams().size() + ") - Page " + (page + 1));
        int size = plugin.getTeamManager().getTeams().size();
        int start = page * 18;
        int finish = (page + 1) * 18 <= size ? (page + 1) * 18 : size;
        for(int i = start; i < finish; i++)
        {
            Team team = plugin.getTeamManager().getTeams().get(i + start);
            ItemStack teamIcon = new ItemStack(Material.SKULL_ITEM, team.getSize(), (short)3);
            ItemMeta teamIconm = teamIcon.getItemMeta();
            teamIconm.setDisplayName(ChatColor.GREEN + team.getLeader().getName() + ChatColor.BLUE + "'s" + " Team (" + team.getSize() + ")");
            List<String> members = new ArrayList();
            for(Player member : team.getMembers()) members.add(team.getPrefix(member) + " " + ChatColor.GREEN + member.getName());
            teamIconm.setLore(members);
            teamIcon.setItemMeta(teamIconm);
            otherTeams.addItem(teamIcon);
        }
        if(start != 0)
        {
            ItemStack goBack = new ItemStack(Material.CARPET, page + 1, (short)11);
            ItemMeta goBackm = goBack.getItemMeta();
            goBackm.setDisplayName(ChatColor.BLUE + "<- Page " + (page + 1));
            goBack.setItemMeta(goBackm);
            otherTeams.setItem(18, goBack);
        }
        if(finish != size)
        {
            ItemStack goForward = new ItemStack(Material.CARPET, page + 2, (short)11);
            goForward.setAmount(page + 1);
            ItemMeta goForwardm = goForward.getItemMeta();
            goForwardm.setDisplayName(ChatColor.BLUE + "Page " + (page + 2) + " ->");
            goForward.setItemMeta(goForwardm);
            otherTeams.setItem(26, goForward);
        }
        p.openInventory(otherTeams);
    }

    public void showTeamEvents(Player p)
    {
        Inventory events = Bukkit.createInventory(null, 9, ChatColor.BLUE + "Team Events");
        ItemStack split = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta splitm = split.getItemMeta();
        splitm.setDisplayName(ChatColor.YELLOW + "Split Fight");
        split.setItemMeta(splitm);
        ItemStack ffa = new ItemStack(Material.IRON_SWORD);
        ItemMeta ffam = ffa.getItemMeta();
        ffam.setDisplayName(ChatColor.YELLOW + "Free For All");
        ffa.setItemMeta(ffam);
        ItemStack juggernaut = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemMeta juggernautm = juggernaut.getItemMeta();
        juggernautm.setDisplayName(ChatColor.YELLOW + "Juggernaut");
        juggernaut.setItemMeta(juggernautm);
        events.setItem(2, split);
        events.setItem(4, ffa);
        events.setItem(6, juggernaut);
        p.openInventory(events);
    }

    public void updateTeams()
    {
        for(Player all : plugin.getTeamManager().getPlayersOnTeams()) all.getInventory().getItem(1).setAmount(plugin.getTeamManager().getTeams().size() < 64 ? plugin.getTeamManager().getTeams().size() : 64);
    }
}
