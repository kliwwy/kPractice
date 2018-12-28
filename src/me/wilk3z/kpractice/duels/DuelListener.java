package me.wilk3z.kpractice.duels;

import me.wilk3z.kpractice.Practice;
import me.wilk3z.kpractice.kits.CustomKit;
import me.wilk3z.kpractice.kits.Kit;
import me.wilk3z.kpractice.kits.KitHandler;
import me.wilk3z.kpractice.playerdata.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Iterator;

public class DuelListener implements Listener
{
    public Practice plugin;

    public DuelListener(Practice plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void join(PlayerJoinEvent e)
    {
        Player p = e.getPlayer();
        plugin.getDuelManager().init(p);
    }

    @EventHandler
    public void quit(PlayerQuitEvent e)
    {
        Player p = e.getPlayer();
        plugin.getDuelManager().reset(p);
    }

    @EventHandler
    public void pickDuelRequest(InventoryClickEvent e)
    {
        DuelHandler duelHandler = new DuelHandler(plugin);
        if(e.getWhoClicked() instanceof Player)
        {
            Player p = (Player) e.getWhoClicked();
            if(plugin.getDuelManager().isInDuelMenu(p))
            {
                if(e.getCurrentItem() == null) return;
                e.setCancelled(true);
                if(!e.getCurrentItem().hasItemMeta()) return;
                ItemStack item = e.getCurrentItem();
                String kitName = ChatColor.stripColor(item.getItemMeta().getDisplayName().replaceAll(" ", "_"));
                if(plugin.getKitManager().kitExists(kitName))
                {
                    Kit kit = plugin.getKitManager().getKit(kitName);
                    Player requested = Bukkit.getServer().getPlayer(plugin.getDuelManager().getRequestedInDuelMenu(p));
                    if(requested != null) duelHandler.sendDuelRequest(p, requested, kit);
                    else p.sendMessage(ChatColor.RED + "The player you were requesting to duel is no longer online.");
                    p.closeInventory();
                    return;
                }
            }
        }
    }

    @EventHandler
    public void stopPickingDuel(InventoryCloseEvent e)
    {
        if(e.getPlayer() instanceof Player)
        {
            Player p = (Player) e.getPlayer();
            if(plugin.getDuelManager().isInDuelMenu(p))
            {
                plugin.getDuelManager().removeFromDuelMenu(p);
                return;
            }
        }
    }

    @EventHandler
    public void applyKit(PlayerInteractEvent e)
    {
        KitHandler kitHandler = new KitHandler(plugin);
        Player p = e.getPlayer();
        if(plugin.getDuelManager().isInDuel(p))
        {
            if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            {
                if(e.getItem() == null) return;
                if(!e.getItem().hasItemMeta()) return;
                ItemStack item = e.getItem();
                if(item.getType().equals(Material.ENCHANTED_BOOK))
                {
                    e.setCancelled(true);
                    Kit kit = plugin.getDuelManager().getDuel(p).getKit();
                    if(item.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Default " + kit.getDisplayName() + " Kit") && !item.getItemMeta().hasLore())
                    {
                        kitHandler.applyInventory(p, kit.getInventory());
                        p.sendMessage(ChatColor.YELLOW + "Giving you " + ChatColor.GREEN + ChatColor.stripColor(item.getItemMeta().getDisplayName()) + ChatColor.YELLOW + ".");
                        return;
                    }
                    CustomKit customKit = plugin.getPlayerDataManager().getPlayerData(p).getCustomKit(kit, Integer.parseInt(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).replace("Custom Kit #", "")));
                    kitHandler.applyInventory(p, customKit.getInventory());
                    p.sendMessage(ChatColor.YELLOW + "Giving you " + ChatColor.GREEN + customKit.getName() + ChatColor.YELLOW + ".");
                }
            }
        }
    }

    @EventHandler
    public void cancelDamage(EntityDamageEvent e)
    {
        if(e.getEntity() instanceof Player)
        {
            Player p = (Player) e.getEntity();
            if(plugin.getDuelManager().isInDuel(p))
            {
                Duel duel = plugin.getDuelManager().getDuel(p);
                if(!duel.isActive()) e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void endDuelDeath(PlayerDeathEvent e)
    {
        e.setDeathMessage(null);
        DuelHandler duelHandler = new DuelHandler(plugin);
        Player p = e.getEntity();
        if(plugin.getDuelManager().isInDuel(p))
        {
            Duel duel = plugin.getDuelManager().getDuel(p);
            duelHandler.endDuel(duel, duel.getOtherPlayer(p), p);
            Iterator<ItemStack> drops = e.getDrops().iterator();
            while(drops.hasNext())
            {
                ItemStack drop = drops.next();
                drops.remove();
            }
        }
    }

    @EventHandler
    public void endDuelQuit(PlayerQuitEvent e)
    {
        DuelHandler duelHandler = new DuelHandler(plugin);
        Player p = e.getPlayer();
        if(plugin.getDuelManager().isInDuel(p))
        {
            Duel duel = plugin.getDuelManager().getDuel(p);
            duelHandler.endDuel(duel, duel.getOtherPlayer(p), p);
        }
    }
}
