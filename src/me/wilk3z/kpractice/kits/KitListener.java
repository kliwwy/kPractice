package me.wilk3z.kpractice.kits;

import me.wilk3z.kpractice.Practice;
import me.wilk3z.kpractice.arenas.Arena;
import me.wilk3z.kpractice.playerdata.PlayerData;
import me.wilk3z.kpractice.queues.QueueHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class KitListener implements Listener
{
    public Practice plugin;

    public KitListener(Practice plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void selectArena(InventoryClickEvent e)
    {
        if(e.getWhoClicked() instanceof Player)
        {
            Player p = (Player) e.getWhoClicked();
            if(!plugin.getDuelManager().isInDuel(p) && !plugin.getDuelManager().isInDuelMenu(p) && !plugin.getBuildManager().isBuilder(p) && !plugin.getKitManager().isEditingKit(p))
            {
                if(e.getInventory().getTitle().contains(ChatColor.BLUE + "Arenas"))
                {
                    if(e.getCurrentItem() == null) return;
                    e.setCancelled(true);
                    if(!e.getCurrentItem().hasItemMeta()) return;
                    ItemStack item = e.getCurrentItem();
                    if(item.getType().equals(Material.BOOK) || item.getType().equals(Material.ENCHANTED_BOOK))
                    {
                        if(plugin.getArenaManager().arenaExists(ChatColor.stripColor(item.getItemMeta().getDisplayName().replaceAll(" ", "_"))))
                        {
                            if(item.getType().equals(Material.BOOK))
                            {
                                item.setType(Material.ENCHANTED_BOOK);
                                p.updateInventory();
                                return;
                            }
                            if(item.getType().equals(Material.ENCHANTED_BOOK))
                            {
                                item.setType(Material.BOOK);
                                p.updateInventory();
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void saveArenas(InventoryCloseEvent e)
    {
        if(e.getPlayer() instanceof Player)
        {
            Player p = (Player) e.getPlayer();
            if(e.getInventory().getTitle().contains(ChatColor.BLUE + "Arenas"))
            {
                String kitName = ChatColor.stripColor(e.getInventory().getTitle().replace("Arenas", "")).trim().replaceAll(" ", "_");
                if(plugin.getKitManager().kitExists(kitName))
                {
                    Kit kit = plugin.getKitManager().getKit(kitName);
                    Set<Arena> arenas = new HashSet();
                    for(ItemStack icon : e.getInventory().getContents())
                    {
                        if(icon == null) break;
                        else
                        {
                            if(icon.getType().equals(Material.ENCHANTED_BOOK))
                            {
                                if(icon.hasItemMeta())
                                {
                                    String arenaName = ChatColor.stripColor(icon.getItemMeta().getDisplayName()).replaceAll(" ", "_");
                                    if(plugin.getArenaManager().arenaExists(arenaName))
                                    {
                                        Arena arena = plugin.getArenaManager().getArena(arenaName);
                                        arenas.add(arena);
                                    }
                                }
                            }
                        }
                    }
                    kit.setArenas(arenas);
                    p.sendMessage(ChatColor.YELLOW + "Set arenas for kit " + ChatColor.DARK_GREEN + kit.getDisplayName() + ChatColor.YELLOW + ".");
                }
            }
        }
    }

    @EventHandler
    public void openEditor(PlayerInteractEvent e)
    {
        KitHandler kitHandler = new KitHandler(plugin);
        Player p = e.getPlayer();
        if(!plugin.getDuelManager().isInDuel(p) && !plugin.getBuildManager().isBuilder(p) && !plugin.getKitManager().isEditingKit(p))
        {
            if(e.getItem() == null) return;
            if(!e.getItem().hasItemMeta()) return;
            if(!e.getItem().getItemMeta().hasDisplayName()) return;
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR))
            {
                ItemStack item = e.getItem();
                if(item.getType().equals(Material.BOOK))
                {
                    String name = item.getItemMeta().getDisplayName();
                    if(name.equals(ChatColor.GOLD + "Edit Kits"))
                    {
                        e.setCancelled(true);
                        kitHandler.openEditor(p);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void editKit(InventoryClickEvent e)
    {
        KitHandler kitHandler = new KitHandler(plugin);
        if(e.getWhoClicked() instanceof Player)
        {
            Player p = (Player) e.getWhoClicked();
            if(!plugin.getDuelManager().isInDuel(p) && !plugin.getDuelManager().isInDuelMenu(p) && !plugin.getBuildManager().isBuilder(p) && !plugin.getKitManager().isEditingKit(p))
            {
                if(e.getInventory().getTitle().equals(ChatColor.BLUE + "Select a Kit to Edit"))
                {
                    if(e.getCurrentItem() == null) return;
                    e.setCancelled(true);
                    if(!e.getCurrentItem().hasItemMeta()) return;
                    ItemStack item = e.getCurrentItem();
                    String kitName = ChatColor.stripColor(item.getItemMeta().getDisplayName().replaceAll(" ", "_"));
                    if(plugin.getKitManager().kitExists(kitName))
                    {
                        Kit kit = plugin.getKitManager().getKit(kitName);
                        kitHandler.editKit(p, kit);
                        p.closeInventory();
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void openEditorInventory(PlayerInteractEvent e)
    {
        Player p = e.getPlayer();
        if(plugin.getKitManager().isEditingKit(p))
        {
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            {
                if(e.getClickedBlock() == null) return;
                if(e.getClickedBlock().getType().equals(Material.CHEST))
                {
                    e.setCancelled(true);
                    Kit kit = plugin.getKitManager().getKitPlayerIsEditing(p);
                    Inventory editor = Bukkit.createInventory(null, 54, ChatColor.BLUE + kit.getDisplayName());
                    editor.setContents(kit.getEditorInventory().getContents());
                    p.openInventory(editor);
                }
            }
        }
    }

    @EventHandler
    public void openCustomKits(PlayerInteractEvent e)
    {
        KitHandler kitHandler = new KitHandler(plugin);
        Player p = e.getPlayer();
        if(plugin.getKitManager().isEditingKit(p))
        {
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            {
                if(e.getClickedBlock() == null) return;
                if(e.getClickedBlock().getType().equals(Material.ANVIL))
                {
                    e.setCancelled(true);
                    kitHandler.showCustomKits(p);
                }
            }
        }
    }

    @EventHandler
    public void closeCustomKits(InventoryCloseEvent e)
    {
        if(e.getPlayer() instanceof Player)
        {
            Player p = (Player) e.getPlayer();
            if(plugin.getKitManager().isEditingKit(p))
            {
                if(plugin.getKitManager().isInCustomKitMenu(p))
                {
                    plugin.getKitManager().removeFromCustomKitMenu(p);
                }
            }
        }
    }

    @EventHandler
    public void saveCustomKit(InventoryClickEvent e)
    {
        KitHandler kitHandler = new KitHandler(plugin);
        if(e.getWhoClicked() instanceof Player)
        {
            Player p = (Player) e.getWhoClicked();
            if(plugin.getKitManager().isEditingKit(p))
            {
                Kit kit = plugin.getKitManager().getKitPlayerIsEditing(p);
                if(e.getInventory().getTitle().equals(ChatColor.BLUE + "Edit " + kit.getDisplayName() + " Kits"))
                {
                    if(e.getCurrentItem() == null) return;
                    e.setCancelled(true);
                    if(!e.getCurrentItem().hasItemMeta()) return;
                    ItemStack item = e.getCurrentItem();
                    if(item.getType().equals(Material.BOOK) || item.getType().equals(Material.ENCHANTED_BOOK))
                    {
                        if(item.getItemMeta().getDisplayName().contains(ChatColor.YELLOW + "Save " + ChatColor.GREEN + kit.getDisplayName()))
                        {
                            int customKit = Integer.parseInt(ChatColor.stripColor(item.getItemMeta().getDisplayName()).split(" ")[ChatColor.stripColor(item.getItemMeta().getDisplayName()).split(" ").length - 1].replace("#", ""));
                            kitHandler.saveCustomKit(p, kit, customKit);
                            kitHandler.refreshCustomKitsMenu(p, kit, customKit);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void loadCustomKit(InventoryClickEvent e)
    {
        KitHandler kitHandler = new KitHandler(plugin);
        if(e.getWhoClicked() instanceof Player)
        {
            Player p = (Player) e.getWhoClicked();
            if(plugin.getKitManager().isEditingKit(p))
            {
                Kit kit = plugin.getKitManager().getKitPlayerIsEditing(p);
                if(e.getInventory().getTitle().equals(ChatColor.BLUE + "Edit " + kit.getDisplayName() + " Kits"))
                {
                    if(e.getCurrentItem() == null) return;
                    e.setCancelled(true);
                    if(!e.getCurrentItem().hasItemMeta()) return;
                    ItemStack item = e.getCurrentItem();
                    if(item.getType().equals(Material.CHEST))
                    {
                        if(item.getItemMeta().getDisplayName().contains(ChatColor.YELLOW + "Load " + ChatColor.GREEN + kit.getDisplayName()))
                        {
                            int customKit = Integer.parseInt(ChatColor.stripColor(item.getItemMeta().getDisplayName()).split(" ")[ChatColor.stripColor(item.getItemMeta().getDisplayName()).split(" ").length - 1].replace("#", ""));
                            kitHandler.loadCustomKit(p, kit, customKit);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void renameCustomKit(InventoryClickEvent e)
    {
        if(e.getWhoClicked() instanceof Player)
        {
            Player p = (Player) e.getWhoClicked();
            if(plugin.getKitManager().isEditingKit(p))
            {
                Kit kit = plugin.getKitManager().getKitPlayerIsEditing(p);
                if(e.getInventory().getTitle().equals(ChatColor.BLUE + "Edit " + kit.getDisplayName() + " Kits"))
                {
                    if(e.getCurrentItem() == null) return;
                    e.setCancelled(true);
                    if(!e.getCurrentItem().hasItemMeta()) return;
                    ItemStack item = e.getCurrentItem();
                    if(item.getType().equals(Material.NAME_TAG))
                    {
                        if(item.getItemMeta().getDisplayName().contains(ChatColor.YELLOW + "Rename " + ChatColor.GREEN + kit.getDisplayName()))
                        {
                            int customKit = Integer.parseInt(ChatColor.stripColor(item.getItemMeta().getDisplayName()).split(" ")[ChatColor.stripColor(item.getItemMeta().getDisplayName()).split(" ").length - 1].replace("#", ""));
                            plugin.getKitManager().renameCustomKit(p, customKit);
                            p.closeInventory();
                            p.sendMessage(ChatColor.YELLOW + "Type a new name in chat to rename " + ChatColor.DARK_GREEN + kit.getDisplayName() + ChatColor.GREEN + " #" + customKit + ChatColor.YELLOW + ".");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void renameCustomKit(AsyncPlayerChatEvent e)
    {
        KitHandler kitHandler = new KitHandler(plugin);
        Player p = e.getPlayer();
        if(plugin.getKitManager().isEditingKit(p))
        {
            if(plugin.getKitManager().isRenamingCustomKit(p))
            {
                e.setCancelled(true);
                String name = e.getMessage();
                Kit kit = plugin.getKitManager().getKitPlayerIsEditing(p);
                int customKit = plugin.getKitManager().getCustomKitBeingRenamed(p);
                kitHandler.renameCustomKit(p, name, kit, customKit);
            }
        }
    }

    @EventHandler
    public void deleteCustomKit(InventoryClickEvent e)
    {
        KitHandler kitHandler = new KitHandler(plugin);
        if(e.getWhoClicked() instanceof Player)
        {
            Player p = (Player) e.getWhoClicked();
            if(plugin.getKitManager().isEditingKit(p))
            {
                Kit kit = plugin.getKitManager().getKitPlayerIsEditing(p);
                if(e.getInventory().getTitle().equals(ChatColor.BLUE + "Edit " + kit.getDisplayName() + " Kits"))
                {
                    if(e.getCurrentItem() == null) return;
                    e.setCancelled(true);
                    if(!e.getCurrentItem().hasItemMeta()) return;
                    ItemStack item = e.getCurrentItem();
                    if(item.getType().equals(Material.FIRE))
                    {
                        if(item.getItemMeta().getDisplayName().contains(ChatColor.YELLOW + "Delete " + ChatColor.GREEN + kit.getDisplayName()))
                        {
                            int customKit = Integer.parseInt(ChatColor.stripColor(item.getItemMeta().getDisplayName()).split(" ")[ChatColor.stripColor(item.getItemMeta().getDisplayName()).split(" ").length - 1].replace("#", ""));
                            kitHandler.deleteCustomKit(p, kit, customKit);
                            kitHandler.refreshCustomKitsMenu(p, kit, customKit);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void leaveEditor(PlayerInteractEvent e)
    {
        KitHandler kitHandler = new KitHandler(plugin);
        Player p = e.getPlayer();
        if(plugin.getKitManager().isEditingKit(p))
        {
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            {
                if(e.getClickedBlock() == null) return;
                if(e.getClickedBlock().getState() instanceof Sign)
                {
                    e.setCancelled(true);
                    kitHandler.stopEditingKit(p);
                }
            }
        }
    }

    @EventHandler
    public void saveEditorInventory(InventoryCloseEvent e)
    {
        if(e.getPlayer() instanceof Player)
        {
            Player p = (Player) e.getPlayer();
            if(plugin.getKitManager().isEditingKit(p))
            {
                Kit kit = plugin.getKitManager().getKitPlayerIsEditing(p);
                Inventory inventory = e.getInventory();
                if(inventory.getTitle().equals(ChatColor.BLUE + kit.getDisplayName() + " Editor"))
                {
                    kit.setEditorInventory(inventory);
                    p.sendMessage(ChatColor.YELLOW + "Set the editor inventory for kit " + ChatColor.GREEN + kit.getDisplayName() + ChatColor.YELLOW + ".");
                    plugin.getKitManager().stopEditingKit(p);
                }
            }
        }
    }

    @EventHandler
    public void setKitOrder(InventoryClickEvent e)
    {
        if(e.getWhoClicked() instanceof Player)
        {
            Player p = (Player) e.getWhoClicked();
            Inventory inventory = e.getClickedInventory();
            if(inventory != null)
            {
                if(inventory.getTitle().equals(ChatColor.BLUE + "Order Kits"))
                {
                    if(e.getCurrentItem() == null) return;
                    e.setCancelled(true);
                    if(!e.getCurrentItem().hasItemMeta()) return;
                    ItemStack item = e.getCurrentItem();
                    int order = e.getSlot();
                    if(e.isLeftClick())
                    {
                        if(order == 0) return;
                        ItemStack before = inventory.getItem(e.getSlot() - 1);
                        before.setAmount(before.getAmount() + 1);
                        item.setAmount(item.getAmount() - 1);
                        inventory.setItem(e.getSlot() - 1, item);
                        inventory.setItem(e.getSlot(), before);
                        return;
                    }
                    if(e.isRightClick())
                    {
                        if(order == plugin.getKitManager().getKits().size() - 1) return;
                        ItemStack before = inventory.getItem(e.getSlot() + 1);
                        before.setAmount(before.getAmount() - 1);
                        item.setAmount(item.getAmount() + 1);
                        inventory.setItem(e.getSlot() + 1, item);
                        inventory.setItem(e.getSlot(), before);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void saveKitOrder(InventoryCloseEvent e)
    {
        if(e.getPlayer() instanceof Player)
        {
            Player p = (Player) e.getPlayer();
            Inventory inventory = e.getInventory();
            if(inventory.getTitle().equals(ChatColor.BLUE + "Order Kits"))
            {
                for(int i = 0; i < inventory.getSize(); i++)
                {
                    if(inventory.getItem(i) == null) break;
                    Kit kit = plugin.getKitManager().getKit(ChatColor.stripColor(inventory.getItem(i).getItemMeta().getDisplayName().replaceAll(" ", "_")));
                    kit.setOrder(inventory.getItem(i).getAmount());
                }
                p.sendMessage(ChatColor.YELLOW + "Saved the kit order.");
            }
        }
    }

    @EventHandler
    public void quit(PlayerQuitEvent e)
    {
        Player p = e.getPlayer();
        if(plugin.getKitManager().isEditingKit(p)) plugin.getKitManager().stopEditingKit(p);
    }
}
