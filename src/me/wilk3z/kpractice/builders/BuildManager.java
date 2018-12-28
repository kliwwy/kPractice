package me.wilk3z.kpractice.builders;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BuildManager
{
    public Set<UUID> builders;

    public BuildManager()
    {
        builders = new HashSet();
    }

    public void setBuilder(Player p, boolean build)
    {
        if(build) builders.add(p.getUniqueId());
        else builders.remove(p.getUniqueId());
    }

    public boolean isBuilder(Player p)
    {
        return builders.contains(p.getUniqueId());
    }
}
