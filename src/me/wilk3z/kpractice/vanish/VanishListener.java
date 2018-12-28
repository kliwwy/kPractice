package me.wilk3z.kpractice.vanish;

import me.wilk3z.kpractice.Practice;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;

public class VanishListener implements Listener
{
    public Practice plugin;

    public VanishListener(Practice plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void join(PlayerJoinEvent e)
    {
        Player p = e.getPlayer();
        plugin.getVanishManager().init(p);
    }

    @EventHandler
    public void quit(PlayerQuitEvent e)
    {
        Player p = e.getPlayer();
        plugin.getVanishManager().reset(p);
    }

    @EventHandler
    public void launchProjectile(ProjectileLaunchEvent e)
    {
        if(e.getEntity().getShooter() instanceof Player)
        {
            Player p = (Player) e.getEntity().getShooter();
            int id = e.getEntity().getEntityId();
            plugin.getVanishManager().setSource(id, p);
        }
    }

    @EventHandler
    public void projectileHit(ProjectileHitEvent e)
    {
        if(plugin.getVanishManager().hasSource(e.getEntity().getEntityId())) plugin.getVanishManager().removeSource(e.getEntity().getEntityId());
    }

    @EventHandler
    public void dropItem(PlayerDropItemEvent e)
    {
        Player p = e.getPlayer();
        int id = e.getItemDrop().getEntityId();
        plugin.getVanishManager().setSource(id, p);
    }

    @EventHandler
    public void pickupItem(PlayerPickupItemEvent e)
    {
        Player p = e.getPlayer();
        int id = e.getItem().getEntityId();
        Player source = Bukkit.getServer().getPlayer(plugin.getVanishManager().getSource(id));
        if(source != null) if(!p.canSee(source)) e.setCancelled(true);
    }

    @EventHandler
    public void itemDespawn(ItemDespawnEvent e)
    {
        if(plugin.getVanishManager().hasSource(e.getEntity().getEntityId())) plugin.getVanishManager().removeSource(e.getEntity().getEntityId());
    }

    @EventHandler
    public void splashPotion(PotionSplashEvent e)
    {
        ThrownPotion thrownPotion = e.getEntity();
        if(thrownPotion.getShooter() instanceof Player)
        {
            Player p = (Player) thrownPotion.getShooter();
            Location location = new Location(p.getWorld(), (int) Math.round(thrownPotion.getLocation().getX()), (int) Math.round(thrownPotion.getLocation().getY()), (int) Math.round(thrownPotion.getLocation().getZ()));
            plugin.getVanishManager().addParticle(p, location);
            for(Player all : Bukkit.getServer().getOnlinePlayers()) if(all.canSee(p)) plugin.getVanishManager().addParticle(all, location);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
            {
                public void run()
                {
                    for(Player all : Bukkit.getServer().getOnlinePlayers()) if(plugin.getVanishManager().hasParticle(all, location)) plugin.getVanishManager().removeParticle(all, location);
                }
            }, 1L);
        }
    }

    @EventHandler
    public void teleport(PlayerTeleportEvent e)
    {
        Player p = e.getPlayer();
        for(Player all : Bukkit.getServer().getOnlinePlayers())
        {
            if(all.canSee(p))
            {
                all.hidePlayer(p);
                all.showPlayer(p);
            }
        }
    }
}
