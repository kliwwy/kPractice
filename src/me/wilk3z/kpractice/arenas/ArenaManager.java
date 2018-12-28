package me.wilk3z.kpractice.arenas;

import me.wilk3z.kpractice.Practice;
import me.wilk3z.kpractice.kits.Kit;

import java.io.File;
import java.util.*;

public class ArenaManager
{
    public Practice plugin;
    public Map<String, Arena> arenas;

    public ArenaManager(Practice plugin)
    {
        this.plugin = plugin;
        arenas = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    }

    public void loadArenas()
    {
        File arenasFile = new File(plugin.getDataFolder(), "Arenas");
        if(!arenasFile.exists()) arenasFile.mkdirs();
        if(arenasFile.listFiles().length == 0) return;
        for(File arenaFile : arenasFile.listFiles())
        {
            String arenaName = arenaFile.getName().replace(".yml", "");
            Arena arena = new Arena(plugin, arenaName);
            arena.load();
            addArena(arena);
        }
    }

    public long saveArenas()
    {
        long before = System.currentTimeMillis();
        long after = System.currentTimeMillis();
        for(Arena arena : arenas.values()) arena.save();
        long time = after - before;
        return time;
    }

    public void addArena(Arena arena)
    {
        arenas.put(arena.getName(), arena);
    }

    public void createArena(String arenaName)
    {
        Arena arena = new Arena(plugin, arenaName);
        arena.save();
        arenas.put(arenaName, arena);
    }

    public void deleteArena(String arenaName)
    {
        Arena arena = getArena(arenaName);
        for(Kit kit : plugin.getKitManager().getKits())
        {
            if(kit.hasArena(arena))
            {
                Set<Arena> arenas = kit.getAllArenas();
                arenas.remove(arena);
                kit.setArenas(arenas);
            }
        }
        arenas.remove(arenaName);
        arena.delete();
    }

    public boolean arenaExists(String arenaName)
    {
        return arenas.containsKey(arenaName);
    }

    public Arena getArena(String arenaName)
    {
        return arenas.get(arenaName);
    }

    public Set<Arena> getArenas()
    {
        Set<Arena> arenas = new HashSet();
        for(Arena arena : this.arenas.values()) arenas.add(arena);
        return arenas;
    }
}
