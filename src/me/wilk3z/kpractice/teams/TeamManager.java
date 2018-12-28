package me.wilk3z.kpractice.teams;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class TeamManager
{
    public Map<UUID, Team> playerTeams;
    public List<Team> teams;

    public TeamManager()
    {
        playerTeams = new HashMap();
        teams = new ArrayList();
    }

    public void addTeam(Team team)
    {
        teams.add(team);
    }

    public void removeTeam(Team team)
    {
        teams.remove(team);
    }

    public List<Team> getTeams()
    {
        return teams;
    }

    public void addToTeam(Player p, Team team)
    {
        playerTeams.put(p.getUniqueId(), team);
    }

    public void removeFromTeam(Player p)
    {
        playerTeams.remove(p.getUniqueId());
    }

    public boolean isOnTeam(Player p)
    {
        return playerTeams.containsKey(p.getUniqueId());
    }

    public Team getTeam(Player p)
    {
        return playerTeams.get(p.getUniqueId());
    }

    public List<Player> getPlayersOnTeams()
    {
        List<Player> players = new ArrayList();
        for(UUID player : playerTeams.keySet()) players.add(Bukkit.getPlayer(player));
        return players;
    }
}