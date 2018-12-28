package me.wilk3z.kpractice.arenas;

import me.wilk3z.kpractice.Practice;
import me.wilk3z.kpractice.utils.FileUtil;
import me.wilk3z.kpractice.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Arena
{
    public Practice plugin;
    public String name;
    public Location[] spawns;
    public boolean usable;

    public Arena(Practice plugin, String name)
    {
        this.plugin = plugin;
        this.name = name;
        spawns = new Location[2];
        usable = false;
    }

    public void load()
    {
        LocationUtil locationUtil = new LocationUtil();
        File arenaFile = new File(plugin.getDataFolder() + File.separator + "Arenas", name + ".yml");
        YamlConfiguration arenaYaml = YamlConfiguration.loadConfiguration(arenaFile);
        if(arenaFile.exists())
        {
            for(int i = 0; i < 2; i++) if(arenaYaml.isSet("spawn." + i)) spawns[i] = locationUtil.toLocation(arenaYaml.getString("spawn." + i));
            usable = arenaYaml.getBoolean("usable");
        }
    }

    public long save()
    {
        LocationUtil locationUtil = new LocationUtil();
        long before = System.currentTimeMillis();
        long after = System.currentTimeMillis();
        File arenaFile = new File(plugin.getDataFolder() + File.separator + "Arenas", name + ".yml");
        YamlConfiguration arenaYaml = YamlConfiguration.loadConfiguration(arenaFile);
        for(int i = 0; i < 2; i++) if(spawns[i] != null) arenaYaml.set("spawn." + i, locationUtil.toString(spawns[i]));
        arenaYaml.set("usable", usable);
        try
        {
            arenaYaml.save(arenaFile);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        long time = after - before;
        return time;
    }

    public void delete()
    {
        FileUtil fileUtil = new FileUtil();
        File arenaFile = new File(plugin.getDataFolder() + File.separator + "Arenas", name + ".yml");
        fileUtil.delete(arenaFile);
    }

    public String getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return name.replaceAll("_", " ");
    }

    public void setSpawn(int spawn, Location location)
    {
        spawns[spawn - 1] = location;
    }

    public Location getSpawn(int spawn)
    {
        return spawns[spawn - 1];
    }

    public void setUsable(boolean usable)
    {
        this.usable = usable;
    }

    public boolean isUsable()
    {
        return usable;
    }
}
