package me.wilk3z.kpractice.duels;

import me.wilk3z.kpractice.Practice;
import me.wilk3z.kpractice.arenas.Arena;
import me.wilk3z.kpractice.kits.Kit;
import me.wilk3z.kpractice.kits.KitHandler;
import me.wilk3z.kpractice.utils.MessageBuilder;
import me.wilk3z.kpractice.utils.TimeUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DuelHandler
{
    public Practice plugin;

    public DuelHandler(Practice plugin)
    {
        this.plugin = plugin;
    }

    public void openDuelMenu(Player challenger, Player requested)
    {
        Set<Kit> kits = plugin.getKitManager().getAvailableKits();
        if(kits.isEmpty())
        {
            challenger.sendMessage(ChatColor.RED + "There are currently no available kits.");
            return;
        }
        int size = (kits.size() > 9 ? ((kits.size() / 9) + 1) * 9 : 9);
        Inventory menu = Bukkit.createInventory(null, size, ChatColor.BLUE + "Select a Kit");
        for(Kit kit : kits)
        {
            ItemStack icon = kit.getIcon().clone();
            ItemMeta iconm = icon.getItemMeta();
            iconm.setDisplayName(ChatColor.BLUE + kit.getDisplayName());
            icon.setItemMeta(iconm);
            menu.addItem(icon);
        }
        challenger.openInventory(menu);
        plugin.getDuelManager().putInDuelMenu(challenger, requested);
    }

    public void sendDuelRequest(Player challenger, Player requested, Kit kit)
    {
        if(plugin.getDuelManager().isInDuel(challenger))
        {
            challenger.sendMessage(ChatColor.RED + "You can't send duel requests to other players while in a duel.");
            return;
        }
        if(plugin.getKitManager().isEditingKit(challenger))
        {
            challenger.sendMessage(ChatColor.RED + "You can't send duel requests to other players while editing a kit.");
            return;
        }
        if(plugin.getQueueManager().isInQueue(challenger))
        {
            challenger.sendMessage(ChatColor.RED + "You can't send duel requests to players while in queue.");
            return;
        }
        if(plugin.getSpectatorManager().isSpectating(requested) || plugin.getKitManager().isEditingKit(requested) || plugin.getQueueManager().isInQueue(requested))
        {
            challenger.sendMessage(ChatColor.YELLOW + requested.getName() + ChatColor.RED + " is currently unavailable to duel.");
            return;
        }
        if(plugin.getDuelManager().isInDuel(requested))
        {
            challenger.sendMessage(ChatColor.YELLOW + requested.getName() + ChatColor.RED + " is already in a duel.");
            return;
        }
        plugin.getDuelManager().addDuelRequest(requested, challenger, kit);
        challenger.sendMessage(ChatColor.YELLOW + "Duel request sent to " + ChatColor.GREEN + requested.getName() + ChatColor.YELLOW + " with " + ChatColor.DARK_GREEN + kit.getDisplayName() + ChatColor.YELLOW + ".");
        MessageBuilder message = new MessageBuilder();
        message.addMessage(ChatColor.GREEN + challenger.getName() + ChatColor.YELLOW + " has requested to duel you with " + ChatColor.DARK_GREEN + kit.getDisplayName() + ChatColor.YELLOW + ". ");
        message.addMessage(ChatColor.YELLOW + "Click here to accept.");
        message.addCommand("/accept " + challenger.getName());
        message.send(requested);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            public void run()
            {
                if(challenger != null && requested != null)
                {
                    if(plugin.getDuelManager().hasDuelRequest(requested, challenger))
                    {
                        challenger.sendMessage(ChatColor.RED + "Your duel request to " + ChatColor.YELLOW + requested.getName() + ChatColor.RED + " has expired.");
                        plugin.getDuelManager().removeDuelRequest(requested, challenger);
                    }
                }
            }
        }, 15 * 20L);
    }

    public void acceptDuelRequest(Player p, DuelRequest duelRequest)
    {
        if(plugin.getDuelManager().isInDuel(p))
        {
            p.sendMessage(ChatColor.RED + "You can't accept duel requests while in a duel.");
            return;
        }
        if(plugin.getKitManager().isEditingKit(p))
        {
            p.sendMessage(ChatColor.RED + "You can't accept duel requests editing a kit.");
            return;
        }
        if(plugin.getQueueManager().isInQueue(p))
        {
            p.sendMessage(ChatColor.RED + "You can't accept duel requests while in queue.");
            return;
        }
        Player challenger = Bukkit.getPlayer(duelRequest.getChallenger());
        if(plugin.getSpectatorManager().isSpectating(challenger) || plugin.getKitManager().isEditingKit(challenger) || plugin.getQueueManager().isInQueue(challenger))
        {
            p.sendMessage(ChatColor.YELLOW + challenger.getName() + ChatColor.RED + " is currently unavailable to duel.");
            return;
        }
        if(plugin.getDuelManager().isInDuel(challenger))
        {
            p.sendMessage(ChatColor.YELLOW + challenger.getName() + ChatColor.RED + " is already in a duel.");
            return;
        }
        Kit kit = duelRequest.getKit();
        startDuel(p, challenger, kit.getRandomArena(), kit, false);
    }

    public void denyDuelRequest(Player p, DuelRequest duelRequest)
    {

    }

    public void startDuel(Player p1, Player p2, Arena arena, Kit kit, boolean ranked)
    {
        KitHandler kitHandler = new KitHandler(plugin);
        plugin.getQueueManager().removeFromQueue(p1);
        plugin.getQueueManager().removeFromQueue(p2);
        plugin.getDuelManager().reset(p1);
        plugin.getDuelManager().reset(p2);
        if(plugin.getQueueManager().isSearchingQueue(p1)) plugin.getQueueManager().stopSearchingQueue(p1);
        if(plugin.getQueueManager().isSearchingQueue(p2)) plugin.getQueueManager().stopSearchingQueue(p2);
        if(plugin.getDuelManager().hasPreviousDuel(p1)) plugin.getDuelManager().removePreviousDuel(p1);
        if(plugin.getDuelManager().hasPreviousDuel(p2)) plugin.getDuelManager().removePreviousDuel(p2);
        if(plugin.getDuelManager().hasPostDuelInventory(p1.getUniqueId())) plugin.getDuelManager().removePostDuelInventory(p1.getUniqueId());
        if(plugin.getDuelManager().hasPostDuelInventory(p2.getUniqueId())) plugin.getDuelManager().removePostDuelInventory(p2.getUniqueId());
        Duel duel = new Duel(plugin, p1, p2, arena, kit, ranked);
        plugin.getDuelManager().putInDuel(p1, duel);
        plugin.getDuelManager().putInDuel(p2, duel);
        duel.start();
        kitHandler.giveKits(p1, kit);
        kitHandler.giveKits(p2, kit);
    }

    public void endDuel(Duel duel, Player winner, Player loser)
    {
        duel.end(winner, loser);
        if(plugin.getEnderpearlManager().isOnCooldown(winner)) plugin.getEnderpearlManager().removeFromCooldown(winner);
        if(plugin.getEnderpearlManager().isOnCooldown(loser)) plugin.getEnderpearlManager().removeFromCooldown(loser);
        plugin.getDuelManager().removeFromDuel(winner);
        plugin.getDuelManager().removeFromDuel(loser);
        plugin.getDuelManager().setPostDuelInventory(winner, generatePostDuelInventory(winner, false));
        plugin.getDuelManager().setPostDuelInventory(loser, generatePostDuelInventory(loser, true));
        plugin.getDuelManager().setPreviousDuel(duel);
        sendInventories(duel);
        plugin.getPlayerDataManager().getPlayerData(winner).setWins(duel.getKit(), plugin.getPlayerDataManager().getPlayerData(winner).getWins(duel.getKit(), duel.isRanked()) + 1, duel.isRanked());
        plugin.getPlayerDataManager().getPlayerData(loser).setLosses(duel.getKit(), plugin.getPlayerDataManager().getPlayerData(loser).getLosses(duel.getKit(), duel.isRanked()) + 1, duel.isRanked());
        if(duel.isRanked())
        {
            int eloChange = plugin.getPlayerDataManager().getEloChange(winner, loser, duel.getKit());
            plugin.getPlayerDataManager().getPlayerData(winner).setElo(duel.getKit(), plugin.getPlayerDataManager().getPlayerData(winner).getElo(duel.getKit()) + eloChange);
            plugin.getPlayerDataManager().getPlayerData(loser).setElo(duel.getKit(), plugin.getPlayerDataManager().getPlayerData(loser).getElo(duel.getKit()) - eloChange);
            duel.sendDuelMessage(ChatColor.YELLOW + "Elo Changes: " + ChatColor.GREEN + winner.getName() + " +" + eloChange + " (" + plugin.getPlayerDataManager().getPlayerData(winner).getElo(duel.getKit()) + ") " + ChatColor.RED + loser.getName() + " -" + eloChange + " (" + plugin.getPlayerDataManager().getPlayerData(loser).getElo(duel.getKit()) + ")");
        }
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            public void run()
            {
                if(plugin.getDuelManager().hasPreviousDuel(loser)) if(plugin.getDuelManager().getPreviousDuel(winner).equals(loser.getUniqueId())) plugin.getDuelManager().removePreviousDuel(winner);
                if(plugin.getDuelManager().hasPreviousDuel(loser)) if(plugin.getDuelManager().getPreviousDuel(loser).equals(winner.getUniqueId())) plugin.getDuelManager().removePreviousDuel(loser);
            }
        }, 15 * 20L);
    }

    public void sendInventories(Duel duel)
    {
        MessageBuilder message = new MessageBuilder();
        message.addMessage(ChatColor.GOLD + "Inventories: ");
        message.addMessage(ChatColor.YELLOW + duel.getPlayers()[0].getName());
        message.addCommand("/_ " + duel.getPlayers()[0].getUniqueId().toString());
        message.addMessage(ChatColor.YELLOW + ", ");
        message.addMessage(ChatColor.YELLOW + duel.getPlayers()[1].getName());
        message.addCommand("/_ " + duel.getPlayers()[1].getUniqueId().toString());
        message.addMessage(ChatColor.YELLOW + ".");
        message.send(duel.getPlayers()[0]);
        message.send(duel.getPlayers()[1]);
        if(!plugin.getSpectatorManager().getPlayersSpectatingDuel(duel).isEmpty()) for(Player spectator : plugin.getSpectatorManager().getPlayersSpectatingDuel(duel)) message.send(spectator);
    }

    public Inventory generatePostDuelInventory(Player p, boolean dead)
    {
        TimeUtil timeUtil = new TimeUtil();
        Inventory inventory = Bukkit.createInventory(null, 54, ChatColor.BLUE + p.getName());
        for(int i = 0; i < 4; i++) inventory.setItem(39 - i, p.getInventory().getArmorContents()[i]);
        for(int i = p.getInventory().getSize(); i > 0; i--) inventory.setItem(i - 1, p.getInventory().getItem(i - 1));

        ItemStack health = new ItemStack(Material.SKULL_ITEM, 1, (short) (dead ? 0 : 3));
        health.setAmount(dead ? 1 : (int) ((CraftPlayer)p).getHealth());
        ItemMeta healthm = health.getItemMeta();
        healthm.setDisplayName(ChatColor.GOLD + "Health Points");
        health.setItemMeta(healthm);

        ItemStack foodLevel = new ItemStack(Material.COOKED_BEEF);
        foodLevel.setAmount(p.getFoodLevel() > 0 ? p.getFoodLevel() : 1);
        ItemMeta foodLevelm = foodLevel.getItemMeta();
        foodLevelm.setDisplayName(ChatColor.GOLD + "Food Level");
        foodLevel.setItemMeta(foodLevelm);

        ItemStack effects = new ItemStack(Material.BREWING_STAND_ITEM);
        effects.setAmount(!p.getActivePotionEffects().isEmpty() ? p.getActivePotionEffects().size() : 1);
        ItemMeta effectsm = effects.getItemMeta();
        effectsm.setDisplayName(ChatColor.GOLD + "Potion Effects");
        if(!p.getActivePotionEffects().isEmpty())
        {
            List<String> effectsInfo = new ArrayList();
            for(PotionEffect potionEffect : p.getActivePotionEffects()) effectsInfo.add(ChatColor.DARK_PURPLE + potionEffect.getType().getName() + " " + (potionEffect.getAmplifier() + 1) + " for " + (timeUtil.formatTime(potionEffect.getDuration() / 20)));
            effectsm.setLore(effectsInfo);
        }
        effects.setItemMeta(effectsm);

        inventory.setItem(48, health);
        inventory.setItem(49, foodLevel);
        inventory.setItem(50, effects);
        return inventory;
    }
}
