package me.wilk3z.kpractice.enderpearls;

import me.wilk3z.kpractice.Practice;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EnderpearlListener implements Listener
{
    public Practice plugin;

    public EnderpearlListener(Practice plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void throwEnderpearl(ProjectileLaunchEvent e)
    {
        if(e.getEntity().getShooter() instanceof Player)
        {
            if(e.getEntity().getType().equals(EntityType.ENDER_PEARL))
            {
                Player p = (Player) e.getEntity().getShooter();
                if(!plugin.getEnderpearlManager().isOnCooldown(p))
                {
                    p.sendMessage(ChatColor.YELLOW + "You're now on enderpearl cooldown.");
                    int seconds = 16;
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
                    {
                        public void run()
                        {
                            if(plugin.getEnderpearlManager().isOnCooldown(p))
                            {
                                p.sendMessage(ChatColor.YELLOW + "You're no longer on enderpearl cooldown.");
                                plugin.getEnderpearlManager().removeFromCooldown(p);
                                if(p.getItemInHand().getType().equals(Material.ENDER_PEARL))
                                {
                                    ItemStack enderpearl = p.getItemInHand();
                                    ItemMeta enderpearlm = enderpearl.getItemMeta();
                                    enderpearlm.setDisplayName(ChatColor.GREEN + "Enderpearl cooldown ended");
                                    enderpearl.setItemMeta(enderpearlm);
                                    p.updateInventory();
                                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
                                    {
                                        public void run()
                                        {
                                            if(p.getItemInHand().getType().equals(Material.ENDER_PEARL))
                                            {
                                                if(p.getItemInHand().hasItemMeta())
                                                {
                                                    if(p.getItemInHand().getItemMeta().hasDisplayName())
                                                    {
                                                        p.getInventory().setItem(p.getInventory().getHeldItemSlot(), new ItemStack(Material.ENDER_PEARL, p.getInventory().getItem(p.getInventory().getHeldItemSlot()).getAmount()));
                                                        p.updateInventory();
                                                    }
                                                }
                                            }
                                        }
                                    }, 20L);
                                }
                                if(!p.getInventory().all(Material.ENDER_PEARL).keySet().isEmpty())
                                {
                                    for(int slot : p.getInventory().all(Material.ENDER_PEARL).keySet())
                                    {
                                        if(slot != p.getInventory().getHeldItemSlot())
                                        {
                                            if(p.getInventory().getItem(slot).hasItemMeta())
                                            {
                                                if(p.getInventory().getItem(slot).getItemMeta().hasDisplayName())
                                                {
                                                    p.getInventory().setItem(slot, new ItemStack(Material.ENDER_PEARL, p.getInventory().getItem(slot).getAmount()));
                                                    p.updateInventory();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }, seconds * 20L);
                    int task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
                    {
                        public void run()
                        {
                            if(p.getItemInHand().getType().equals(Material.ENDER_PEARL))
                            {
                                ItemStack enderpearl = p.getItemInHand();
                                ItemMeta enderpearlm = enderpearl.getItemMeta();
                                enderpearlm.setDisplayName(ChatColor.YELLOW + "Enderpearl cooldown: " + ChatColor.RED + getTimeLeft(p));
                                enderpearl.setItemMeta(enderpearlm);
                                p.updateInventory();
                            }
                            for(int slot : p.getInventory().all(Material.ENDER_PEARL).keySet())
                            {
                                if(slot != p.getInventory().getHeldItemSlot())
                                {
                                    if(p.getInventory().getItem(slot).hasItemMeta())
                                    {
                                        if(p.getInventory().getItem(slot).getItemMeta().hasDisplayName())
                                        {
                                            p.getInventory().setItem(slot, new ItemStack(Material.ENDER_PEARL, p.getInventory().getItem(slot).getAmount()));
                                            p.updateInventory();
                                        }
                                    }
                                }
                            }
                        }
                    }, 1L, 2L);
                    plugin.getEnderpearlManager().putOnCooldown(p, System.currentTimeMillis() + (seconds * 1000));
                    plugin.getEnderpearlManager().startTask(p, task);
                }
            }
        }
    }

    @EventHandler
    public void useEnderpearl(PlayerInteractEvent e)
    {
        Player p = e.getPlayer();
        if(p.getItemInHand() != null)
        {
            if(p.getItemInHand().getType().equals(Material.ENDER_PEARL))
            {
                if(plugin.getEnderpearlManager().isOnCooldown(p))
                {
                    e.setCancelled(true);
                    p.sendMessage(ChatColor.YELLOW + "Enderpearl cooldown: " + ChatColor.RED + getTimeLeft(p));
                    p.updateInventory();
                }
            }
        }
    }

    @EventHandler
    public void clickEnderpearl(InventoryClickEvent e)
    {
        if(e.getWhoClicked() instanceof Player)
        {
            Player p = (Player) e.getWhoClicked();
            if(plugin.getDuelManager().isInDuel(p))
            {
                if(e.getCurrentItem() == null) return;
                if(e.getCurrentItem().getType().equals(Material.ENDER_PEARL))
                {
                    if(e.getCurrentItem().hasItemMeta())
                    {
                        ItemStack enderpearl = new ItemStack(Material.ENDER_PEARL, e.getCurrentItem().getAmount());
                        ItemMeta enderpearlm = enderpearl.getItemMeta();
                        e.getCurrentItem().setItemMeta(enderpearlm);
                    }
                }
            }
        }
    }

    @EventHandler
    public void dropEnderpearl(PlayerDropItemEvent e)
    {
        Player p = e.getPlayer();
        if(plugin.getDuelManager().isInDuel(p))
        {
            if(e.getItemDrop().getItemStack().getType().equals(Material.ENDER_PEARL))
            {
                if(e.getItemDrop().getItemStack().hasItemMeta())
                {
                    ItemStack enderpearl = new ItemStack(Material.ENDER_PEARL, e.getItemDrop().getItemStack().getAmount());
                    ItemMeta enderpearlm = enderpearl.getItemMeta();
                    e.getItemDrop().getItemStack().setItemMeta(enderpearlm);
                }
            }
        }
    }

    public String getTimeLeft(Player p)
    {
        long timeLeft = plugin.getEnderpearlManager().getCooldown(p) - System.currentTimeMillis();
        int seconds = (int) timeLeft / 1000;
        int milliseconds = (int) (timeLeft / 100) % 10;
        String time = seconds + "." + milliseconds + "s";
        return time;
    }
}
