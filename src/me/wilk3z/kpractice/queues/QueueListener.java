package me.wilk3z.kpractice.queues;

import me.wilk3z.kpractice.Practice;
import me.wilk3z.kpractice.kits.Kit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class QueueListener implements Listener
{
    public Practice plugin;

    public QueueListener(Practice plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void openQueue(PlayerInteractEvent e)
    {
        QueueHandler queueHandler = new QueueHandler(plugin);
        Player p = e.getPlayer();
        if(!plugin.getDuelManager().isInDuel(p) && !plugin.getSpectatorManager().isSpectating(p) && !plugin.getKitManager().isEditingKit(p) && !plugin.getBuildManager().isBuilder(p))
        {
            if(e.getItem() == null) return;
            if(!e.getItem().hasItemMeta()) return;
            if(!e.getItem().getItemMeta().hasDisplayName()) return;
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR))
            {
                ItemStack item = e.getItem();
                if(item.getType().equals(Material.IRON_SWORD) || item.getType().equals(Material.DIAMOND_SWORD))
                {
                    String name = item.getItemMeta().getDisplayName();
                    if(name.contains("Queue"))
                    {
                        e.setCancelled(true);
                        String queue = name.split(" ")[0];
                        if(queue.equals(ChatColor.BLUE + "Un-Ranked"))
                        {
                            queueHandler.openQueue(p, false);
                            return;
                        }
                        if(queue.equals(ChatColor.GREEN + "Ranked"))
                        {
                            queueHandler.openQueue(p, true);
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void closeQueue(InventoryCloseEvent e)
    {
        if(e.getPlayer() instanceof Player)
        {
            Player p = (Player) e.getPlayer();
            if(plugin.getQueueManager().isInQueueMenu(p)) plugin.getQueueManager().removeFromQueueMenu(p);
        }
    }

    @EventHandler
    public void joinQueue(InventoryClickEvent e)
    {
        QueueHandler queueHandler = new QueueHandler(plugin);
        if(e.getWhoClicked() instanceof Player)
        {
            Player p = (Player) e.getWhoClicked();
            if(!plugin.getDuelManager().isInDuel(p) && !plugin.getSpectatorManager().isSpectating(p) && !plugin.getDuelManager().isInDuelMenu(p) && !plugin.getBuildManager().isBuilder(p) && !plugin.getKitManager().isEditingKit(p))
            {
                if(e.getInventory().getTitle().contains("Queue"))
                {
                    if(e.getCurrentItem() == null) return;
                    e.setCancelled(true);
                    if(!e.getCurrentItem().hasItemMeta()) return;
                    ItemStack item = e.getCurrentItem();
                    String kitName = ChatColor.stripColor(item.getItemMeta().getDisplayName().replaceAll(" ", "_"));
                    if(plugin.getKitManager().kitExists(kitName))
                    {
                        Kit kit = plugin.getKitManager().getKit(kitName);
                        String type = e.getInventory().getTitle().split(" ")[2];
                        if(type.equals("Un-Ranked"))
                        {
                            queueHandler.joinQueue(p, kit, false);
                            p.closeInventory();
                            return;
                        }
                        if(type.equals("Ranked"))
                        {
                            queueHandler.joinQueue(p, kit, true);
                            p.closeInventory();
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void leaveQueue(PlayerInteractEvent e)
    {
        QueueHandler queueHandler = new QueueHandler(plugin);
        Player p = e.getPlayer();
        if(!plugin.getDuelManager().isInDuel(p) && !plugin.getSpectatorManager().isSpectating(p) && !plugin.getKitManager().isEditingKit(p) && !plugin.getBuildManager().isBuilder(p))
        {
            if(e.getItem() == null) return;
            if(!e.getItem().hasItemMeta()) return;
            if(!e.getItem().getItemMeta().hasDisplayName()) return;
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR))
            {
                if(plugin.getQueueManager().isInQueue(p))
                {
                    ItemStack item = e.getItem();
                    if(item.getType().equals(Material.INK_SACK))
                    {
                        if(item.getItemMeta().getDisplayName().equals(ChatColor.RED + "Leave " + ChatColor.YELLOW + plugin.getQueueManager().getQueue(p).getKit().getDisplayName() + ChatColor.RED + " Queue"))
                        {
                            e.setCancelled(true);
                            queueHandler.leaveQueue(p);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void quit(PlayerQuitEvent e)
    {
        QueueHandler queueHandler = new QueueHandler(plugin);
        Player p = e.getPlayer();
        if(plugin.getQueueManager().isInQueueMenu(p)) plugin.getQueueManager().removeFromQueueMenu(p);
        if(plugin.getQueueManager().isInQueue(p))
        {
            Queue queue = plugin.getQueueManager().getQueue(p);
            plugin.getQueueManager().removeFromQueue(p);
            queueHandler.refreshQueue(queue.getKit(), queue.isRanked());
        }
    }
}
