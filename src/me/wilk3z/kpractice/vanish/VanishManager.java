package me.wilk3z.kpractice.vanish;

import me.wilk3z.kpractice.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import java.util.*;

public class VanishManager
{
    public Map<UUID, List<String>> particles;
    public Map<Integer, UUID> sources;
    public LocationUtil locationUtil;

    public VanishManager()
    {
        particles = new HashMap();
        sources = new HashMap();
        locationUtil = new LocationUtil();
    }

    public void init(Player p)
    {
        List<String> particles = new ArrayList();
        this.particles.put(p.getUniqueId(), particles);
    }

    public void reset(Player p)
    {
        particles.remove(p.getUniqueId());
    }

    public void addParticle(Player p, Location location)
    {
        List<String> locations = getParticles(p);
        locations.add(locationUtil.toString(location));
        setParticles(p, locations);
    }

    public void removeParticle(Player p, Location location)
    {
        List<String> locations = getParticles(p);
        locations.remove(locationUtil.toString(location));
        setParticles(p, locations);
    }

    public void setParticles(Player p, List<String> locations)
    {
        particles.put(p.getUniqueId(), locations);
    }

    public List<String> getParticles(Player p)
    {
        return particles.get(p.getUniqueId());
    }

    public boolean hasParticle(Player p, Location location)
    {
        return particles.get(p.getUniqueId()).contains(locationUtil.toString(location));
    }

    public void setSource(int id, Player p)
    {
        sources.put(id, p.getUniqueId());
    }

    public void removeSource(int id)
    {
        sources.remove(id);
    }

    public boolean hasSource(int id)
    {
        return sources.containsKey(id);
    }

    public UUID getSource(int id)
    {
        return sources.get(id);
    }
}
