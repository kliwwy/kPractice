package me.wilk3z.kpractice.spectators;

import me.wilk3z.kpractice.Practice;
import me.wilk3z.kpractice.duels.Duel;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpectatorHandler
{
    public Practice plugin;

    public SpectatorHandler(Practice plugin)
    {
        this.plugin = plugin;
    }

    public void spectateDuel(Player p, Player target)
    {
        if(plugin.getDuelManager().isInDuel(p))
        {
            p.sendMessage(ChatColor.RED + "You can't spectate other players while in a duel.");
            return;
        }
        if(plugin.getKitManager().isEditingKit(p))
        {
            p.sendMessage(ChatColor.RED + "You can't spectate other players while editing a kit.");
            return;
        }
        if(plugin.getQueueManager().isInQueue(p))
        {
            p.sendMessage(ChatColor.RED + "You can't spectate other players while in queue.");
            return;
        }
        if(!plugin.getDuelManager().isInDuel(target))
        {
            p.sendMessage(ChatColor.YELLOW + target.getName() + ChatColor.RED + " is currently not in a duel.");
            return;
        }
        Duel duel = plugin.getDuelManager().getDuel(target);
        p.setGameMode(GameMode.CREATIVE);
        giveSpectatorItems(p);
        duel.getPlayers()[0].hidePlayer(p);
        duel.getPlayers()[1].hidePlayer(p);
        p.showPlayer(duel.getPlayers()[0]);
        p.showPlayer(duel.getPlayers()[1]);
        p.teleport(target.getLocation());
        plugin.getSpectatorManager().spectateDuel(p, duel);
        if(duel.isRanked()) p.sendMessage(ChatColor.YELLOW + "Now spectating " + ChatColor.GREEN + duel.getPlayers()[0].getName() + "(" + plugin.getPlayerDataManager().getPlayerData(duel.getPlayers()[0]).getElo(duel.getKit()) + ")" + ChatColor.YELLOW + " vs " + ChatColor.GREEN + duel.getPlayers()[1].getName() + "(" + plugin.getPlayerDataManager().getPlayerData(duel.getPlayers()[1]).getElo(duel.getKit()) + ")" + ChatColor.YELLOW + " using kit " + ChatColor.DARK_GREEN + duel.getKit().getDisplayName() + ChatColor.YELLOW + ".");
        else p.sendMessage(ChatColor.YELLOW + "Now spectating " + ChatColor.GREEN + duel.getPlayers()[0].getName() + ChatColor.YELLOW + " vs " + ChatColor.GREEN + duel.getPlayers()[1].getName() + ChatColor.YELLOW + " using kit " + ChatColor.DARK_GREEN + duel.getKit().getDisplayName() + ChatColor.YELLOW + ".");
        duel.sendDuelMessage(ChatColor.GREEN + p.getName() + ChatColor.YELLOW + " is now spectating the duel.");
    }

    public void stopSpectatingDuel(Player p)
    {
        Duel duel = plugin.getSpectatorManager().getSpectatedDuel(p);
        p.hidePlayer(duel.getPlayers()[0]);
        p.hidePlayer(duel.getPlayers()[1]);
        plugin.getSpectatorManager().stopSpectating(p);
        plugin.sendToSpawn(p);
        p.sendMessage(ChatColor.YELLOW + "No longer spectating " + ChatColor.GREEN + duel.getPlayers()[0].getName() + ChatColor.YELLOW + " vs " + ChatColor.GREEN + duel.getPlayers()[1].getName());
        duel.sendDuelMessage(ChatColor.GREEN + p.getName() + ChatColor.YELLOW + " is no longer spectating the duel.");
    }

    public void giveSpectatorItems(Player p)
    {
        ItemStack togglePlayers = new ItemStack(Material.INK_SACK, 1, (short)10);
        ItemMeta togglePlayersm = togglePlayers.getItemMeta();
        togglePlayersm.setDisplayName(ChatColor.GREEN + "Show Players");
        togglePlayers.setItemMeta(togglePlayersm);

        ItemStack stopSpectating = new ItemStack(Material.INK_SACK, 1, (short)1);
        ItemMeta stopSpectatingm = stopSpectating.getItemMeta();
        stopSpectatingm.setDisplayName(ChatColor.RED + "Stop Spectating");
        stopSpectating.setItemMeta(stopSpectatingm);
        p.getInventory().clear();
        p.getInventory().setItem(0, togglePlayers);
        p.getInventory().setItem(8, stopSpectating);
        p.updateInventory();
    }
}
