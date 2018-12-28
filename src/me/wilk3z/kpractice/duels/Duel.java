package me.wilk3z.kpractice.duels;

import me.wilk3z.kpractice.Practice;
import me.wilk3z.kpractice.arenas.Arena;
import me.wilk3z.kpractice.kits.Kit;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.xml.soap.Text;

public class Duel
{
    public Practice plugin;
    public Player p1;
    public Player p2;
    public Arena arena;
    public Kit kit;
    public boolean ranked;
    public boolean active;
    public int countdown;
    public int task;

    public Duel(Practice plugin, Player p1, Player p2, Arena arena, Kit kit, boolean ranked)
    {
        this.plugin = plugin;
        this.p1 = p1;
        this.p2 = p2;
        this.arena = arena;
        this.kit = kit;
        this.ranked = ranked;
        active = false;
    }

    public void start()
    {
        sendToArena();
        countdown = 5;
        task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
        {
            public void run()
            {
                if(countdown != 0)
                {
                    sendDuelMessage(ChatColor.GREEN + "Starting duel in " + ChatColor.YELLOW + countdown + ChatColor.GREEN + " seconds!");
                    countdown--;
                }
                else
                {
                    active = true;
                    sendDuelMessage(ChatColor.GREEN + "Duel starting now!");
                    Bukkit.getServer().getScheduler().cancelTask(task);
                }
            }
        }, 1L, 20L);
    }

    public void end(Player winner, Player loser)
    {
        Duel duel = this;
        sendDuelMessage(ChatColor.YELLOW + "Winner: " + winner.getName());
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            public void run()
            {
                if(loser != null)
                {
                    if(plugin.getEnderpearlManager().isOnCooldown(loser)) plugin.getEnderpearlManager().removeFromCooldown(loser);
                    if(loser.isDead()) loser.spigot().respawn();
                }
                if(winner != null)
                {
                    if(plugin.getEnderpearlManager().isOnCooldown(winner)) plugin.getEnderpearlManager().removeFromCooldown(winner);
                    plugin.sendToSpawn(winner);
                }
                if(plugin.getSpectatorManager().getPlayersSpectatingDuel(duel).size() > 1)
                {
                    for(Player spectator : plugin.getSpectatorManager().getPlayersSpectatingDuel(duel))
                    {
                        plugin.getSpectatorManager().stopSpectating(spectator);
                        plugin.sendToSpawn(spectator);
                    }
                }
            }
        }, 3 * 20L);
    }

    public Player[] getPlayers()
    {
        return new Player[] { p1, p2 };
    }

    public Player getOtherPlayer(Player p)
    {
        Player other = null;
        if(p1.getUniqueId().equals(p.getUniqueId())) other = p2;
        if(p2.getUniqueId().equals(p.getUniqueId())) other = p1;
        return other;
    }

    public Arena getArena()
    {
        return arena;
    }

    public Kit getKit()
    {
        return kit;
    }

    public boolean isRanked()
    {
        return ranked;
    }

    public boolean isActive()
    {
        return active;
    }

    public void sendToArena()
    {
        p1.teleport(arena.getSpawn(1).clone().add(0, 1, 0));
        p2.teleport(arena.getSpawn(2).clone().add(0, 1, 0));
        p1.showPlayer(p2);
        p2.showPlayer(p1);
    }

    public void sendDuelMessage(String message)
    {
        p1.sendMessage(message);
        p2.sendMessage(message);
        if(plugin.getSpectatorManager().getPlayersSpectatingDuel(this).size() > 1) for(Player spectator : plugin.getSpectatorManager().getPlayersSpectatingDuel(this)) spectator.sendMessage(message);
    }

    public String toString()
    {
        return p1.getUniqueId().toString() + ", " + p2.getUniqueId().toString() + ", " + kit.getName() + ", " + ranked;
    }
}
