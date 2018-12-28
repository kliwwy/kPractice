package me.wilk3z.kpractice.listeners;

import me.wilk3z.kpractice.Practice;
import me.wilk3z.kpractice.duels.Duel;
import me.wilk3z.kpractice.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BasicListener implements Listener
{
    public Practice plugin;

    public BasicListener(Practice plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void despawn(CreatureSpawnEvent e)
    {
        e.setCancelled(true);
    }

    @EventHandler
    public void chat(AsyncPlayerChatEvent e)
    {
        Player p = e.getPlayer();
        String msg = e.getMessage();
        if(msg.startsWith("@"))
        {
            if(plugin.getTeamManager().isOnTeam(p))
            {
                Team team = plugin.getTeamManager().getTeam(p);
                msg = msg.replace("@", "");
                msg = team.getPrefix(p) + " " + (p.isOp() ? ChatColor.RED : ChatColor.GREEN) + p.getName() + ChatColor.YELLOW + ": " + msg;
                team.sendTeamMessage(msg);
                e.setCancelled(true);
                return;
            }
        }
        if(plugin.getSpectatorManager().isSpectating(p))
        {
            e.setCancelled(true);
            Duel duel = plugin.getSpectatorManager().getSpectatedDuel(p);
            for(Player spectator : plugin.getSpectatorManager().getPlayersSpectatingDuel(duel)) spectator.sendMessage(ChatColor.GRAY + "[SPECTATE] " + p.getName() + ChatColor.RESET + ": " + msg);
        }
    }

    @EventHandler
    public void join(PlayerJoinEvent e)
    {
        e.setJoinMessage(null);
        Player p = e.getPlayer();
        if(p.isOp())
        {
            if(!plugin.hasSpawn()) p.sendMessage(ChatColor.RED + "Spawn is currently not set up.");
            if(!plugin.hasKitEditor()) p.sendMessage(ChatColor.RED + "Kit editor is currently not set up.");
        }
        plugin.sendToSpawn(p);
    }

    @EventHandler
    public void respawn(PlayerRespawnEvent e)
    {
        Player p = e.getPlayer();
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            public void run()
            {
                if(plugin.getDuelManager().isInDuel(p)) plugin.getDuelManager().removeFromDuel(p);
                plugin.sendToSpawn(p);
            }
        }, 1L);
    }

    @EventHandler
    public void quit(PlayerQuitEvent e)
    {
        e.setQuitMessage(null);
    }

    @EventHandler
    public void cancelAchievement(PlayerAchievementAwardedEvent e)
    {
        e.setCancelled(true);
    }

    @EventHandler
    public void cancelDamage(EntityDamageEvent e)
    {
        if(e.getEntity() instanceof Player)
        {
            Player p = (Player) e.getEntity();
            if(!plugin.getDuelManager().isInDuel(p)) e.setCancelled(true);
        }
    }

    @EventHandler
    public void cancelHunger(FoodLevelChangeEvent e)
    {
        if(e.getEntity() instanceof Player)
        {
            Player p = (Player) e.getEntity();
            if(!plugin.getDuelManager().isInDuel(p))
            {
                e.setCancelled(true);
                p.setFoodLevel(20);
                p.setSaturation(20);
            }
        }
    }

    @EventHandler
    public void cancelDrop(PlayerDropItemEvent e)
    {
        Player p = e.getPlayer();
        if(!plugin.getDuelManager().isInDuel(p) && !plugin.getBuildManager().isBuilder(p) && !plugin.getKitManager().isEditingKit(p)) e.setCancelled(true);
        else
        {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
            {
                @Override
                public void run()
                {
                    e.getItemDrop().remove();
                }
            }, 3 * 20L);
        }
    }

    @EventHandler
    public void cancelPickup(PlayerPickupItemEvent e)
    {
        Player p = e.getPlayer();
        if(!plugin.getDuelManager().isInDuel(p) && !plugin.getBuildManager().isBuilder(p) && !plugin.getKitManager().isEditingKit(p)) e.setCancelled(true);
    }

    @EventHandler
    public void cancelInventoryClick(InventoryClickEvent e)
    {
        if(e.getWhoClicked() instanceof Player)
        {
            Player p = (Player) e.getWhoClicked();
            if(!plugin.getDuelManager().isInDuel(p) && !plugin.getDuelManager().isInDuelMenu(p) && !plugin.getKitManager().isEditingKit(p) && !plugin.getBuildManager().isBuilder(p)) e.setCancelled(true);
            if(plugin.getDuelManager().isInDuel(p)) if(e.getInventory().getTitle().startsWith(ChatColor.BLUE.toString())) e.setCancelled(true);
        }
    }

    @EventHandler
    public void cancelBlockBreak(BlockBreakEvent e)
    {
        Player p = e.getPlayer();
        if(!plugin.getBuildManager().isBuilder(p))
        {
            e.setCancelled(true);
            p.sendMessage(ChatColor.RED + "You can't build here.");
        }
    }

    @EventHandler
    public void cancelBlockPlace(BlockPlaceEvent e)
    {
        Player p = e.getPlayer();
        if(!plugin.getBuildManager().isBuilder(p))
        {
            e.setCancelled(true);
            p.sendMessage(ChatColor.RED + "You can't build here.");
        }
    }

    @EventHandler
    public void weather(WeatherChangeEvent e)
    {
        if(!e.toWeatherState()) return;
        e.setCancelled(true);
        e.getWorld().setWeatherDuration(0);
        e.getWorld().setThundering(false);
    }

    @EventHandler
    public void colorSign(SignChangeEvent e)
    {
        Player p = e.getPlayer();
        if(p.isOp()) for(int i = 0; i < e.getLines().length; i++) e.setLine(i, ChatColor.translateAlternateColorCodes('&', e.getLine(i)));
    }
}
