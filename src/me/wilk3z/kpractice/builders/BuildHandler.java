package me.wilk3z.kpractice.builders;

import me.wilk3z.kpractice.Practice;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class BuildHandler
{
    public Practice plugin;

    public BuildHandler(Practice plugin)
    {
        this.plugin = plugin;
    }

    public void setBuilder(Player p)
    {
        if(plugin.getBuildManager().isBuilder(p))
        {
            plugin.getBuildManager().setBuilder(p, false);
            p.setGameMode(GameMode.SURVIVAL);
            for(Player builders : Bukkit.getServer().getOnlinePlayers())
            {
                if(!p.getUniqueId().equals(builders.getUniqueId()))
                {
                    if(plugin.getBuildManager().isBuilder(builders))
                    {
                        p.hidePlayer(builders);
                        builders.hidePlayer(p);
                    }
                }
            }
            plugin.sendToSpawn(p);
        }
        else
        {
            plugin.getBuildManager().setBuilder(p, true);
            p.setGameMode(GameMode.CREATIVE);
            p.getInventory().clear();
            p.getInventory().setHelmet(null);
            p.getInventory().setChestplate(null);
            p.getInventory().setLeggings(null);
            p.getInventory().setBoots(null);
            for(Player builders : Bukkit.getServer().getOnlinePlayers())
            {
                if(!p.getUniqueId().equals(builders.getUniqueId()))
                {
                    if(plugin.getBuildManager().isBuilder(builders))
                    {
                        p.showPlayer(builders);
                        builders.showPlayer(p);
                    }
                }
            }
        }
        p.sendMessage(ChatColor.YELLOW + "You're now " + (plugin.getBuildManager().isBuilder(p) ? ChatColor.GREEN + "able" : ChatColor.RED + "unable") + ChatColor.YELLOW + " to build.");
    }
}
