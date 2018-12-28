package me.wilk3z.kpractice.spectators;

import me.wilk3z.kpractice.Practice;
import me.wilk3z.kpractice.duels.Duel;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpectatorListener implements Listener
{
    public Practice plugin;

    public SpectatorListener(Practice plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void togglePlayers(PlayerInteractEvent e)
    {
        Player p = e.getPlayer();
        if(plugin.getSpectatorManager().isSpectating(p))
        {
            if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            {
                if(e.getItem() == null) return;
                if(!e.getItem().hasItemMeta()) return;
                if(!e.getItem().getItemMeta().hasDisplayName()) return;
                ItemStack item = e.getItem();
                if(item.getType().equals(Material.INK_SACK))
                {
                    e.setCancelled(true);
                    Duel duel = plugin.getSpectatorManager().getSpectatedDuel(p);
                    if(item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Show Players"))
                    {
                        if(plugin.getSpectatorManager().getPlayersSpectatingDuel(duel).size() > 1) for(Player spectators : plugin.getSpectatorManager().getPlayersSpectatingDuel(duel)) p.showPlayer(spectators);
                        ItemStack togglePlayers = new ItemStack(Material.INK_SACK, 1, (short)8);
                        ItemMeta togglePlayersm = togglePlayers.getItemMeta();
                        togglePlayersm.setDisplayName(ChatColor.GRAY + "Hide Players");
                        togglePlayers.setItemMeta(togglePlayersm);
                        p.getInventory().setItem(0, togglePlayers);
                        p.updateInventory();
                        p.sendMessage(ChatColor.YELLOW + "Now showing all other players spectating the duel.");
                        return;
                    }
                    if(item.getItemMeta().getDisplayName().equals(ChatColor.GRAY + "Hide Players"))
                    {
                        if(plugin.getSpectatorManager().getPlayersSpectatingDuel(duel).size() > 1) for(Player spectators : plugin.getSpectatorManager().getPlayersSpectatingDuel(duel)) p.hidePlayer(spectators);
                        ItemStack togglePlayers = new ItemStack(Material.INK_SACK, 1, (short)10);
                        ItemMeta togglePlayersm = togglePlayers.getItemMeta();
                        togglePlayersm.setDisplayName(ChatColor.GREEN + "Show Players");
                        togglePlayers.setItemMeta(togglePlayersm);
                        p.getInventory().setItem(0, togglePlayers);
                        p.updateInventory();
                        p.sendMessage(ChatColor.YELLOW + "Now hiding all other players spectating the duel.");
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void stopSpectating(PlayerInteractEvent e)
    {
        SpectatorHandler spectatorHandler = new SpectatorHandler(plugin);
        Player p = e.getPlayer();
        if(plugin.getSpectatorManager().isSpectating(p))
        {
            if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            {
                if(e.getItem() == null) return;
                if(!e.getItem().hasItemMeta()) return;
                if(!e.getItem().getItemMeta().hasDisplayName()) return;
                ItemStack item = e.getItem();
                if(item.getType().equals(Material.INK_SACK))
                {
                    if(item.getItemMeta().getDisplayName().equals(ChatColor.RED + "Stop Spectating"))
                    {
                        e.setCancelled(true);
                        spectatorHandler.stopSpectatingDuel(p);
                        return;
                    }
                }
            }
        }
    }
}
