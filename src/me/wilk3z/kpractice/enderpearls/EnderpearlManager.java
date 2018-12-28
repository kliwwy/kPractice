package me.wilk3z.kpractice.enderpearls;

import me.wilk3z.kpractice.Practice;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnderpearlManager
{
    public Practice plugin;
    public Map<UUID, Long> cooldowns;
    public Map<UUID, Integer> tasks;

    public EnderpearlManager()
    {
        cooldowns = new HashMap();
        tasks = new HashMap();
    }

    public void putOnCooldown(Player p, long cooldown)
    {
        cooldowns.put(p.getUniqueId(), cooldown);
    }

    public void removeFromCooldown(Player p)
    {
        cooldowns.remove(p.getUniqueId());
        endTask(p);
    }

    public boolean isOnCooldown(Player p)
    {
        return cooldowns.containsKey(p.getUniqueId());
    }

    public long getCooldown(Player p)
    {
        return cooldowns.get(p.getUniqueId());
    }

    public void startTask(Player p, int task)
    {
        tasks.put(p.getUniqueId(), task);
    }

    public void endTask(Player p)
    {
        Bukkit.getServer().getScheduler().cancelTask(tasks.get(p.getUniqueId()));
        tasks.remove(p.getUniqueId());
    }
}
