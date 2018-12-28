package me.wilk3z.kpractice.duels;

import me.wilk3z.kpractice.kits.Kit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class DuelManager
{
    public Map<UUID, UUID> duelMenu;
    public Map<UUID, Set<DuelRequest>> duelRequests;
    public Map<UUID, Duel> inDuel;
    public Map<UUID, Inventory> postDuelInventory;
    public Map<UUID, UUID> previousDuel;

    public DuelManager()
    {
        duelMenu = new HashMap();
        duelRequests = new HashMap();
        inDuel = new HashMap();
        postDuelInventory = new HashMap();
        previousDuel = new HashMap();
    }

    public void init(Player p)
    {
        Set<DuelRequest> duelRequests = new HashSet();
        this.duelRequests.put(p.getUniqueId(), duelRequests);
    }

    public void reset(Player p)
    {
        duelRequests.remove(p.getUniqueId());
    }

    public void putInDuelMenu(Player challenger, Player requested)
    {
        duelMenu.put(challenger.getUniqueId(), requested.getUniqueId());
    }

    public void removeFromDuelMenu(Player p)
    {
        duelMenu.remove(p.getUniqueId());
    }

    public boolean isInDuelMenu(Player p)
    {
        return duelMenu.containsKey(p.getUniqueId());
    }

    public UUID getRequestedInDuelMenu(Player p)
    {
        return duelMenu.get(p.getUniqueId());
    }

    public void addDuelRequest(Player p, Player challenger, Kit kit)
    {
        Set<DuelRequest> duelRequests = getDuelRequests(p);
        DuelRequest duelRequest = new DuelRequest(challenger, kit);
        duelRequests.add(duelRequest);
        this.duelRequests.put(p.getUniqueId(), duelRequests);
    }

    public void removeDuelRequest(Player p, Player challenger)
    {
        Set<DuelRequest> duelRequests = getDuelRequests(p);
        duelRequests.remove(getDuelRequest(p, challenger));
        this.duelRequests.put(p.getUniqueId(), duelRequests);
    }

    public Set<DuelRequest> getDuelRequests(Player p)
    {
        if(duelRequests.containsKey(p.getUniqueId())) return duelRequests.get(p.getUniqueId());
        else
        {
            Set<DuelRequest> duelRequests = new HashSet();
            return duelRequests;
        }
    }

    public boolean hasDuelRequest(Player p, Player challenger)
    {
        boolean check = false;
        Set<DuelRequest> duelRequests = getDuelRequests(p);
        for(DuelRequest allDuelRequests : duelRequests)
        {
            if(allDuelRequests.isChallenger(challenger))
            {
                check = true;
                break;
            }
        }
        return check;
    }

    public DuelRequest getDuelRequest(Player p, Player challenger)
    {
        DuelRequest duelRequest = null;
        Set<DuelRequest> duelRequests = getDuelRequests(p);
        for(DuelRequest allDuelRequests : duelRequests)
        {
            if(allDuelRequests.isChallenger(challenger))
            {
                duelRequest = allDuelRequests;
                break;
            }
        }
        return duelRequest;
    }

    public void putInDuel(Player p, Duel duel)
    {
        inDuel.put(p.getUniqueId(), duel);
    }

    public void removeFromDuel(Player p)
    {
        inDuel.remove(p.getUniqueId());
    }

    public boolean isInDuel(Player p)
    {
        return inDuel.containsKey(p.getUniqueId());
    }

    public Duel getDuel(Player p)
    {
        return inDuel.get(p.getUniqueId());
    }

    public void setPostDuelInventory(Player p, Inventory inventory)
    {
        postDuelInventory.put(p.getUniqueId(), inventory);
    }

    public void removePostDuelInventory(UUID uuid)
    {
        postDuelInventory.remove(uuid);
    }

    public boolean hasPostDuelInventory(UUID uuid)
    {
        return postDuelInventory.containsKey(uuid);
    }

    public Inventory getPostDuelInventory(UUID uuid)
    {
        return postDuelInventory.get(uuid);
    }

    public void setPreviousDuel(Duel duel)
    {
        previousDuel.put(duel.getPlayers()[0].getUniqueId(), duel.getPlayers()[1].getUniqueId());
        previousDuel.put(duel.getPlayers()[1].getUniqueId(), duel.getPlayers()[0].getUniqueId());
    }

    public void removePreviousDuel(Player p)
    {
        previousDuel.remove(p.getUniqueId());
    }

    public boolean hasPreviousDuel(Player p)
    {
        return previousDuel.containsKey(p.getUniqueId());
    }

    public UUID getPreviousDuel(Player p)
    {
        return previousDuel.get(p.getUniqueId());
    }

    public int getAmountInDuel(Kit kit, boolean ranked)
    {
        int amount = 0;
        if(!inDuel.isEmpty()) for(Duel duels : inDuel.values()) if(kit.getName().equals(duels.getKit().getName()) && ranked == duels.isRanked()) amount++;
        return amount;
    }
}
