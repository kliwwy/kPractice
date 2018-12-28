package me.wilk3z.kpractice.spectators;

import me.wilk3z.kpractice.duels.Duel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class SpectatorManager
{
    public Map<UUID, Duel> spectators;

    public SpectatorManager()
    {
        spectators = new HashMap();
    }

    public void spectateDuel(Player p, Duel duel)
    {
        spectators.put(p.getUniqueId(), duel);
    }

    public void stopSpectating(Player p)
    {
        spectators.remove(p.getUniqueId());
    }

    public boolean isSpectating(Player p)
    {
        return spectators.containsKey(p.getUniqueId());
    }

    public Duel getSpectatedDuel(Player p)
    {
        return spectators.get(p.getUniqueId());
    }

    public boolean isSpectatingDuel(Player p, Duel duel)
    {
        if(getSpectatedDuel(p).toString().equals(duel.toString())) return true;
        else return false;
    }

    public List<Player> getPlayersSpectatingDuel(Duel duel)
    {
        List<Player> spectators = new ArrayList();
        for(UUID uuid : this.spectators.keySet())
        {
            Player spectator = Bukkit.getPlayer(uuid);
            if(isSpectatingDuel(spectator, duel)) spectators.add(spectator);
        }
        return spectators;
    }
}
