package me.wilk3z.kpractice.arenas;

import me.wilk3z.kpractice.Practice;
import me.wilk3z.kpractice.kits.Kit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ArenaHandler
{
    public Practice plugin;

    public ArenaHandler(Practice plugin)
    {
        this.plugin = plugin;
    }

    public void createArena(Player p, String arenaName)
    {
        if(plugin.getArenaManager().arenaExists(arenaName))
        {
            p.sendMessage(ChatColor.RED + "Arena " + ChatColor.YELLOW + plugin.getArenaManager().getArena(arenaName).getDisplayName() + ChatColor.RED + " already exists.");
            return;
        }
        plugin.getArenaManager().createArena(arenaName);
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        p.sendMessage(ChatColor.YELLOW + "You've created the arena " + ChatColor.GREEN + arena.getDisplayName() + ChatColor.YELLOW + ".");
    }

    public void deleteArena(Player p, String arenaName)
    {
        if(!plugin.getArenaManager().arenaExists(arenaName))
        {
            p.sendMessage(ChatColor.RED + "Arena " + ChatColor.YELLOW + arenaName + ChatColor.RED + " doesn't exist.");
            return;
        }
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        p.sendMessage(ChatColor.RED + "You've deleted the arena " + ChatColor.YELLOW + arena.getDisplayName() + ChatColor.RED + ".");
        plugin.getArenaManager().deleteArena(arenaName);
    }

    public void reloadArenas(Player p)
    {
        plugin.getArenaManager().saveArenas();
        plugin.getArenaManager().loadArenas();
        p.sendMessage("Reloaded " + plugin.getArenaManager().getArenas().size() + " arenas.");
    }

    public void showArenas(Player p)
    {
        if(plugin.getArenaManager().getArenas().isEmpty())
        {
            p.sendMessage(ChatColor.RED + "There are currently no arenas.");
            return;
        }
        Set<Arena> arenas = plugin.getArenaManager().getArenas();
        int size = (arenas.size() > 9 ? ((arenas.size() / 9) + 1) * 9 : 9);
        Inventory menu = Bukkit.createInventory(null, size, ChatColor.BLUE + "Arenas " + "(" + arenas.size() + ")");
        for(Arena arena : arenas)
        {
            ItemStack icon = new ItemStack(Material.GRASS);
            ItemMeta iconm = icon.getItemMeta();
            iconm.setDisplayName(ChatColor.BLUE + arena.getDisplayName());
            List<String> info = new ArrayList();
            info.add(ChatColor.YELLOW + "Usable: " + (arena.isUsable() ? ChatColor.GREEN + "true" : ChatColor.RED + "false"));
            info.add(ChatColor.YELLOW + "Spawns: " + (arena.getSpawn(1) != null ? ChatColor.GREEN + "1 is set" : ChatColor.RED + "1 isn't set") + ChatColor.YELLOW + ", " + (arena.getSpawn(2) != null ? ChatColor.GREEN + "2 is set" : ChatColor.RED + "2 isn't set"));
            iconm.setLore(info);
            icon.setItemMeta(iconm);
            menu.addItem(icon);
        }
        p.openInventory(menu);
    }

    public void setArenaSpawn(Player p, String arenaName, int spawn)
    {
        if(!plugin.getArenaManager().arenaExists(arenaName))
        {
            p.sendMessage(ChatColor.RED + "Arena " + ChatColor.YELLOW + arenaName + ChatColor.RED + " doesn't exist.");
            return;
        }
        if(spawn < 1 && spawn > 2)
        {
            p.sendMessage(ChatColor.RED + "Arena spawn must be either " + ChatColor.YELLOW + "1" + ChatColor.RED + " or " + ChatColor.YELLOW + "2" + ChatColor.RED + ".");
            return;
        }
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        arena.setSpawn(spawn, p.getLocation());
        p.sendMessage(ChatColor.YELLOW + "Set spawn " + ChatColor.GREEN + spawn + ChatColor.YELLOW + " for arena " + ChatColor.DARK_GREEN + arena.getDisplayName() + ChatColor.YELLOW + " to your location.");
    }

    public void setUsable(Player p, String arenaName)
    {
        if(!plugin.getArenaManager().arenaExists(arenaName))
        {
            p.sendMessage(ChatColor.RED + "Arena " + ChatColor.YELLOW + arenaName + ChatColor.RED + " doesn't exist.");
            return;
        }
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        if(arena.isUsable()) arena.setUsable(false);
        else
        {
            if(arena.getSpawn(1) == null || arena.getSpawn(2) == null)
            {
                p.sendMessage("Arena " + arena.getDisplayName() + " must have 2 spawns set before it can be usable.");
                return;
            }
            arena.setUsable(true);
        }
        p.sendMessage(ChatColor.YELLOW + "Arena " + ChatColor.DARK_GREEN + arena.getDisplayName() + ChatColor.YELLOW + " is now " + (arena.isUsable() ? ChatColor.GREEN + "usable" : ChatColor.RED + "unusable") + ChatColor.YELLOW + ".");
    }
}
