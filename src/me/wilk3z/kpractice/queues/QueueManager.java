package me.wilk3z.kpractice.queues;

import me.wilk3z.kpractice.kits.Kit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class QueueManager
{
    public Map<UUID, Queue> inQueue;
    public Map<UUID, Boolean> inQueueMenu;
    public Map<UUID, Inventory> queueMenu;
    public Map<UUID, Integer> searchQueue;

    public QueueManager()
    {
        inQueue = new HashMap();
        inQueueMenu = new HashMap();
        queueMenu = new HashMap();
        searchQueue = new HashMap();
    }

    public void addToQueue(Player p, Queue queue)
    {
        inQueue.put(p.getUniqueId(), queue);
    }

    public void removeFromQueue(Player p)
    {
        inQueue.remove(p.getUniqueId());
    }

    public boolean isInQueue(Player p)
    {
        return inQueue.containsKey(p.getUniqueId());
    }

    public Queue getQueue(Player p)
    {
        return inQueue.get(p.getUniqueId());
    }

    public int getAmountInQueue(Queue queue)
    {
        int amount = 0;
        if(!inQueue.isEmpty()) for(Queue queues : inQueue.values()) if(queue.isSame(queues)) amount++;
        return amount;
    }

    public List<UUID> getPlayersInQueue(Queue queue)
    {
        List<UUID> players = new ArrayList();
        if(!inQueue.isEmpty()) for(UUID uuid : inQueue.keySet()) if(queue.isSame(inQueue.get(uuid))) players.add(uuid);
        return players;
    }

    public void putInQueueMenu(Player p, Inventory inventory, boolean ranked)
    {
        inQueueMenu.put(p.getUniqueId(), ranked);
        queueMenu.put(p.getUniqueId(), inventory);
    }

    public void removeFromQueueMenu(Player p)
    {
        inQueueMenu.remove(p.getUniqueId());
        queueMenu.remove(p.getUniqueId());
    }

    public boolean isInQueueMenu(Player p)
    {
        return inQueueMenu.containsKey(p.getUniqueId());
    }

    public Set<UUID> getPlayersInQueueMenu(boolean ranked)
    {
        Set<UUID> players = new HashSet();
        if(!inQueueMenu.isEmpty()) for(UUID uuid : inQueueMenu.keySet()) if(ranked == inQueueMenu.get(uuid)) players.add(uuid);
        return players;
    }

    public Inventory getQueueMenu(Player p)
    {
        return queueMenu.get(p.getUniqueId());
    }

    public void startSearchingQueue(Player p, int search)
    {
        searchQueue.put(p.getUniqueId(), search);
    }

    public void stopSearchingQueue(Player p)
    {
        int search = searchQueue.get(p.getUniqueId());
        Bukkit.getServer().getScheduler().cancelTask(search);
        searchQueue.remove(p.getUniqueId());
    }

    public boolean isSearchingQueue(Player p)
    {
        return searchQueue.containsKey(p.getUniqueId());
    }
}
