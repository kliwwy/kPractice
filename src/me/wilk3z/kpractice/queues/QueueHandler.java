package me.wilk3z.kpractice.queues;

import me.wilk3z.kpractice.Practice;
import me.wilk3z.kpractice.duels.Duel;
import me.wilk3z.kpractice.duels.DuelHandler;
import me.wilk3z.kpractice.kits.Kit;
import me.wilk3z.kpractice.playerdata.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

public class QueueHandler
{
    public Practice plugin;

    public QueueHandler(Practice plugin)
    {
        this.plugin = plugin;
    }

    public void openQueue(Player p, boolean ranked)
    {
        Set<Kit> kits = ranked ? plugin.getKitManager().getRankedKits() : plugin.getKitManager().getAvailableKits();
        if(kits.isEmpty())
        {
            p.sendMessage(ChatColor.RED + "There are currently no " + (ranked ? "ranked" : "available") + " kits.");
            return;
        }
        int size = (kits.size() > 9 ? ((kits.size() / 9) + 1) * 9 : 9);
        Inventory menu = Bukkit.createInventory(null, size, ChatColor.BLUE + "Select an " + (ranked ? "Ranked" : "Un-Ranked") + " Queue");
        for(Kit kit : kits)
        {
            int duels = plugin.getDuelManager().getAmountInDuel(kit, ranked);
            int queue = plugin.getQueueManager().getAmountInQueue(new Queue(kit, ranked));
            ItemStack icon = kit.getIcon().clone();
            icon.setAmount(duels > 0 ? (duels <= 64 ? duels : 64) : 1);
            ItemMeta iconm = icon.getItemMeta();
            iconm.setDisplayName(ChatColor.BLUE + kit.getDisplayName());
            iconm.setLore(Arrays.asList(ChatColor.YELLOW + "In queue: " + ChatColor.GREEN + queue, ChatColor.YELLOW + "In duels: " + ChatColor.GREEN + duels));
            icon.setItemMeta(iconm);
            menu.addItem(icon);
        }
        p.openInventory(menu);
        plugin.getQueueManager().putInQueueMenu(p, menu, ranked);
    }

    public void joinQueue(Player p, Kit kit, boolean ranked)
    {
        if(kit.getAvailableArenas().isEmpty())
        {
            p.sendMessage(ChatColor.RED + "Kit " + ChatColor.YELLOW + kit.getDisplayName() + ChatColor.RED + " currently has no available arenas.");
            return;
        }
        plugin.getQueueManager().removeFromQueueMenu(p);
        refreshQueue(kit, ranked);
        ItemStack queue = new ItemStack(Material.INK_SACK, 1, (short)1);
        ItemMeta queuem = queue.getItemMeta();
        queuem.setDisplayName(ChatColor.RED + "Leave " + ChatColor.YELLOW + kit.getDisplayName() + ChatColor.RED + " Queue");
        queue.setItemMeta(queuem);
        p.getInventory().clear();
        p.getInventory().setItem(0, queue);
        p.updateInventory();
        p.sendMessage(ChatColor.YELLOW + "You've joined the " + ChatColor.GREEN + kit.getDisplayName() + ChatColor.YELLOW + (ranked ? " ranked " : " unranked ") + (ranked ? "queue with " + ChatColor.GREEN + plugin.getPlayerDataManager().getPlayerData(p).getElo(kit) + " elo" + ChatColor.YELLOW + "." : "queue."));
        searchQueue(p, kit, ranked);
    }

    public void leaveQueue(Player p)
    {
        Queue queue = plugin.getQueueManager().getQueue(p);
        Kit kit = queue.getKit();
        boolean ranked = queue.isRanked();
        plugin.getQueueManager().removeFromQueue(p);
        if(plugin.getQueueManager().isSearchingQueue(p)) plugin.getQueueManager().stopSearchingQueue(p);
        refreshQueue(kit, ranked);
        plugin.giveSpawnItems(p);
        p.sendMessage(ChatColor.YELLOW + "You've left the " + ChatColor.GREEN + kit.getDisplayName() + ChatColor.YELLOW + (ranked ? " ranked " : " unranked ") + "queue.");
    }

    public void refreshQueue(Kit kit, boolean ranked)
    {
        for(UUID uuid : plugin.getQueueManager().getPlayersInQueueMenu(ranked))
        {
            Player p = Bukkit.getPlayer(uuid);
            Inventory menu = plugin.getQueueManager().getQueueMenu(p);
            for(int slot : menu.all(kit.getIcon().getType()).keySet())
            {
                if(menu.getItem(slot).hasItemMeta())
                {
                    if(menu.getItem(slot).getItemMeta().hasDisplayName())
                    {
                        if(ChatColor.stripColor(menu.getItem(slot).getItemMeta().getDisplayName()).equals(kit.getDisplayName()))
                        {
                            int duels = plugin.getDuelManager().getAmountInDuel(kit, ranked);
                            int queue = plugin.getQueueManager().getAmountInQueue(new Queue(kit, ranked));
                            ItemStack icon = menu.getItem(slot);
                            ItemMeta iconm = icon.getItemMeta();
                            iconm.setLore(Arrays.asList(ChatColor.YELLOW + "In queue: " + ChatColor.GREEN + queue, ChatColor.YELLOW + "In duels: " + ChatColor.GREEN + duels));
                            icon.setItemMeta(iconm);
                            menu.setItem(slot, icon);
                            p.updateInventory();
                            break;
                        }
                    }
                }
            }
        }
    }

    public void searchQueue(Player p, Kit kit, boolean ranked)
    {
        DuelHandler duelHandler = new DuelHandler(plugin);
        if(!ranked)
        {
            if(!plugin.getQueueManager().getPlayersInQueue(new Queue(kit, ranked)).isEmpty())
            {
                Player opponent = Bukkit.getPlayer(plugin.getQueueManager().getPlayersInQueue(new Queue(kit, ranked)).get(0));
                p.sendMessage(ChatColor.YELLOW + "Starting duel against " + ChatColor.GREEN + opponent.getName() + ChatColor.YELLOW + ".");
                opponent.sendMessage(ChatColor.YELLOW + "Starting duel against " + ChatColor.GREEN + p.getName() + ChatColor.YELLOW + ".");
                duelHandler.startDuel(p, opponent, kit.getRandomArena(), kit, ranked);
                return;
            }
            else
            {
                plugin.getQueueManager().addToQueue(p, new Queue(kit, ranked));
                p.sendMessage(ChatColor.YELLOW + "Searching for another player.");
                return;
            }
        }
        else
        {
            int elo = plugin.getPlayerDataManager().getPlayerData(p).getElo(kit);
            Queue queue = new Queue(kit, ranked, new int[] { (((elo - 100) + 50) / 100) * 100, (((elo + 100) + 50) / 100) * 100 });
            plugin.getQueueManager().addToQueue(p, queue);
            p.sendMessage(ChatColor.YELLOW + "Searching in elo range " + ChatColor.GREEN + "[" + queue.getEloRange()[0] + " -> " + queue.getEloRange()[1] + "]" + ChatColor.YELLOW + ".");
            int search = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
            {
                public void run()
                {
                    queue.setEloRange(new int[] { queue.getEloRange()[0] - 50, queue.getEloRange()[1] + 50});
                    p.sendMessage(ChatColor.YELLOW + "Searching in elo range " + ChatColor.GREEN + "[" + queue.getEloRange()[0] + " -> " + queue.getEloRange()[1] + "]" + ChatColor.YELLOW + ".");
                    for(UUID uuid : plugin.getQueueManager().getPlayersInQueue(queue))
                    {
                        if(!p.getUniqueId().equals(uuid))
                        {
                            Player opponent = Bukkit.getPlayer(uuid);
                            if(queue.isInEloRange(plugin.getPlayerDataManager().getPlayerData(opponent).getElo(kit)) && plugin.getQueueManager().getQueue(opponent).isInEloRange(elo))
                            {
                                p.sendMessage(ChatColor.GREEN + kit.getDisplayName() + ChatColor.YELLOW + " ranked duel found: " + ChatColor.GREEN + p.getName() + "(" + elo + ")" + ChatColor.YELLOW + " vs " + ChatColor.GREEN + opponent.getName() + "(" + plugin.getPlayerDataManager().getPlayerData(opponent).getElo(kit) + ")" + ChatColor.YELLOW + ".");
                                opponent.sendMessage(ChatColor.GREEN + kit.getDisplayName() + ChatColor.YELLOW + " ranked duel found: " + ChatColor.GREEN + p.getName() + "(" + elo + ")" + ChatColor.YELLOW + " vs " + ChatColor.GREEN + opponent.getName() + "(" + plugin.getPlayerDataManager().getPlayerData(opponent).getElo(kit) + ")" + ChatColor.YELLOW + ".");
                                duelHandler.startDuel(p, opponent, kit.getRandomArena(), kit, ranked);
                                return;
                            }
                        }
                    }
                }
            }, 3 * 20L, 3 * 20L);
            plugin.getQueueManager().startSearchingQueue(p, search);
        }
    }
}
