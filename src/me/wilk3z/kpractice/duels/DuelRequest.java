package me.wilk3z.kpractice.duels;

import me.wilk3z.kpractice.kits.Kit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DuelRequest
{
    public UUID challenger;
    public Kit kit;

    public DuelRequest(Player challenger, Kit kit)
    {
        this.challenger = challenger.getUniqueId();
        this.kit = kit;
    }

    public UUID getChallenger()
    {
        return challenger;
    }

    public boolean isChallenger(Player p)
    {
        if(p.getUniqueId().equals(challenger)) return true;
        else return false;
    }

    public Kit getKit()
    {
        return kit;
    }
}
