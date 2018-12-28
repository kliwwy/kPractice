package me.wilk3z.kpractice.teams;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class Team
{
    public Player leader;
    public Map<Player, Boolean> members;
    public List<UUID> invited;
    public String name;
    public boolean open;

    public Team(Player leader)
    {
        this.leader = leader;
        members = new HashMap();
        invited = new ArrayList();
        name = leader.getName();
        open = false;
    }

    public void setLeader(Player p)
    {
        members.put(leader, true);
        members.remove(p);
        this.leader = p;
        setName(p.getName());
    }

    public Player getLeader()
    {
        return leader;
    }

    public boolean isLeader(Player p)
    {
        return leader.equals(p);
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void addMember(Player p)
    {
        members.put(p, false);
    }

    public void removeMember(Player p)
    {
        members.remove(p);
    }

    public List<Player> getMembers()
    {
        List<Player> members = new ArrayList();
        members.add(leader);
        for(Player member : this.members.keySet()) members.add(member);
        return members;
    }

    public boolean isMember(Player p)
    {
        return getMembers().contains(p);
    }

    public void setCaptain(Player p, boolean captain)
    {
        members.put(p, captain);
    }

    public boolean isCaptain(Player p)
    {
        if(members.containsKey(p)) return members.get(p);
        else return false;
    }

    public void addInvite(Player p)
    {
        invited.add(p.getUniqueId());
    }

    public void removeInvite(Player p)
    {
        invited.remove(p.getUniqueId());
    }

    public boolean isInvited(Player p)
    {
        return invited.contains(p.getUniqueId());
    }

    public void setOpen(boolean open)
    {
        this.open = open;
    }

    public boolean isOpen()
    {
        return open;
    }

    public void sendTeamMessage(String msg)
    {
        for(Player member : getMembers()) member.sendMessage(ChatColor.BLUE + "[Team] " + ChatColor.RESET + msg);
    }

    public int getSize()
    {
        return getMembers().size();
    }

    public String getPrefix(Player p)
    {
        String prefix = "";
        if(isLeader(p)) prefix = ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + "L" + ChatColor.DARK_GRAY + "]";
        if(isCaptain(p)) prefix = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_PURPLE + "C" + ChatColor.DARK_GRAY + "]";
        return prefix;
    }
}
