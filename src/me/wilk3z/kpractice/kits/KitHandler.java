package me.wilk3z.kpractice.kits;

import me.wilk3z.kpractice.Practice;
import me.wilk3z.kpractice.arenas.Arena;
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
import java.util.List;
import java.util.Set;

public class KitHandler
{
    public Practice plugin;

    public KitHandler(Practice plugin)
    {
        this.plugin = plugin;
    }

    public void createKit(Player p, String kitName)
    {
        if(plugin.getKitManager().kitExists(kitName))
        {
            p.sendMessage(ChatColor.RED + "Kit " + ChatColor.YELLOW + plugin.getKitManager().getKit(kitName).getDisplayName() + ChatColor.RED + " already exists.");
            return;
        }
        plugin.getKitManager().createKit(kitName, generateInventory(p));
        Kit kit = plugin.getKitManager().getKit(kitName);
        p.sendMessage(ChatColor.YELLOW + "You've created the kit " + ChatColor.GREEN + kit.getDisplayName() + ChatColor.YELLOW + ".");
    }

    public void deleteKit(Player p, String kitName)
    {
        if(!plugin.getKitManager().kitExists(kitName))
        {
            p.sendMessage(ChatColor.RED + "Kit " + ChatColor.YELLOW + kitName + ChatColor.RED + " doesn't exist.");
            return;
        }
        Kit kit = plugin.getKitManager().getKit(kitName);
        p.sendMessage(ChatColor.RED + "You've deleted the kit " + ChatColor.YELLOW + kit.getDisplayName() + ChatColor.RED + ".");
        plugin.getKitManager().deleteKit(kitName);
    }

    public void reloadKits(Player p)
    {
        plugin.getKitManager().saveKits();
        plugin.getKitManager().loadKits();
        p.sendMessage("Reloaded " + plugin.getKitManager().getKits().size() + " kits.");
    }

    public void setIcon(Player p, String kitName)
    {
        if(!plugin.getKitManager().kitExists(kitName))
        {
            p.sendMessage(ChatColor.RED + "Kit " + ChatColor.YELLOW + kitName + ChatColor.RED + " doesn't exist.");
            return;
        }
        Kit kit = plugin.getKitManager().getKit(kitName);
        ItemStack icon = p.getItemInHand();
        if(icon == null)
        {
            p.sendMessage(ChatColor.RED + "You must be holding an item in your hand to set the icon for the kit " + ChatColor.YELLOW + kit.getDisplayName() + ChatColor.RED + ".");
            return;
        }
        kit.setIcon(icon);
        p.sendMessage(ChatColor.YELLOW + "Set icon for the kit " + ChatColor.GREEN + kit.getDisplayName() + ChatColor.YELLOW + " to the item in your hand.");
    }

    public void setInventory(Player p, String kitName, String type)
    {
        if(!plugin.getKitManager().kitExists(kitName))
        {
            p.sendMessage(ChatColor.RED + "Kit " + ChatColor.YELLOW + kitName + ChatColor.RED + " doesn't exist.");
            return;
        }
        Kit kit = plugin.getKitManager().getKit(kitName);
        if(type.equals("default"))
        {
            Inventory inventory = generateInventory(p);
            kit.setInventory(inventory);
            p.sendMessage(ChatColor.YELLOW + "Set the default inventory for the kit " + ChatColor.GREEN + kit.getDisplayName() + ChatColor.YELLOW + " to your inventory.");
            return;
        }
        if(type.equals("editor"))
        {
            Inventory inventory = Bukkit.createInventory(null, kit.getEditorInventory().getSize(), ChatColor.BLUE + kit.getDisplayName() + " Editor");
            inventory.setContents(kit.getEditorInventory().getContents());
            p.openInventory(inventory);
            plugin.getKitManager().editKit(p, kit);
            return;
        }
    }

    public void setArenas(Player p, String kitName)
    {
        if(!plugin.getKitManager().kitExists(kitName))
        {
            p.sendMessage(ChatColor.RED + "Kit " + ChatColor.YELLOW + kitName + ChatColor.RED + " doesn't exist.");
            return;
        }
        Kit kit = plugin.getKitManager().getKit(kitName);
        Set<Arena> arenas = plugin.getArenaManager().getArenas();
        if(arenas.isEmpty())
        {
            p.sendMessage(ChatColor.RED + "There are currently no arenas.");
            return;
        }
        int size = (arenas.size() > 9 ? ((arenas.size() / 9) + 1) * 9 : 9);
        Inventory menu = Bukkit.createInventory(null, size, ChatColor.BLUE + "Arenas " + kit.getDisplayName());
        for(Arena arena : arenas)
        {
            ItemStack icon = new ItemStack(kit.hasArena(arena) ? Material.ENCHANTED_BOOK : Material.BOOK);
            ItemMeta iconm = icon.getItemMeta();
            iconm.setDisplayName(ChatColor.BLUE + arena.getDisplayName());
            iconm.setLore(Arrays.asList(ChatColor.YELLOW + "Usable: " + (arena.isUsable() ? ChatColor.GREEN + "true" : ChatColor.RED + "false")));
            icon.setItemMeta(iconm);
            menu.addItem(icon);
        }
        p.openInventory(menu);
    }

    public void setUsable(Player p, String kitName)
    {
        if(!plugin.getKitManager().kitExists(kitName))
        {
            p.sendMessage(ChatColor.RED + "Kit " + ChatColor.YELLOW + kitName + ChatColor.RED + " doesn't exist.");
            return;
        }
        Kit kit = plugin.getKitManager().getKit(kitName);
        if(!kit.isUsable()) kit.setUsable(true);
        else kit.setUsable(false);
        p.sendMessage(ChatColor.YELLOW + "Kit " + ChatColor.DARK_GREEN + kit.getDisplayName() + ChatColor.YELLOW + " is now " + (kit.isUsable() ? ChatColor.GREEN + "usable" : ChatColor.RED + "unusable") + ChatColor.YELLOW + ".");
    }

    public void setEditable(Player p, String kitName)
    {
        if(!plugin.getKitManager().kitExists(kitName))
        {
            p.sendMessage(ChatColor.RED + "Kit " + ChatColor.YELLOW + kitName + ChatColor.RED + " doesn't exist.");
            return;
        }
        Kit kit = plugin.getKitManager().getKit(kitName);
        if(!kit.isEditable()) kit.setEditable(true);
        else kit.setEditable(false);
        p.sendMessage(ChatColor.YELLOW + "Kit " + ChatColor.DARK_GREEN + kit.getDisplayName() + ChatColor.YELLOW + " is now " + (kit.isEditable() ? ChatColor.GREEN + "editable" : ChatColor.RED + "uneditable") + ChatColor.YELLOW + ".");
    }

    public void setRanked(Player p, String kitName)
    {
        if(!plugin.getKitManager().kitExists(kitName))
        {
            p.sendMessage(ChatColor.RED + "Kit " + ChatColor.YELLOW + kitName + ChatColor.RED + " doesn't exist.");
            return;
        }
        Kit kit = plugin.getKitManager().getKit(kitName);
        if(!kit.isRanked()) kit.setRanked(true);
        else kit.setRanked(false);
        p.sendMessage(ChatColor.YELLOW + "Kit " + ChatColor.DARK_GREEN + kit.getDisplayName() + ChatColor.YELLOW + " is now " + (kit.isRanked() ? ChatColor.GREEN + "ranked" : ChatColor.RED + "unranked") + ChatColor.YELLOW + ".");
    }

    public void loadKit(Player p, String kitName)
    {
        if(!plugin.getKitManager().kitExists(kitName))
        {
            p.sendMessage(ChatColor.RED + "Kit " + ChatColor.YELLOW + kitName + ChatColor.RED + " doesn't exist.");
            return;
        }
        Kit kit = plugin.getKitManager().getKit(kitName);
        applyInventory(p, kit.getInventory());
        p.sendMessage(ChatColor.YELLOW + "Loaded the default inventory for the kit " + ChatColor.GREEN + kit.getDisplayName() + ChatColor.YELLOW + ".");
    }

    public void showKits(Player p)
    {
        if(plugin.getKitManager().getKits().isEmpty())
        {
            p.sendMessage(ChatColor.RED + "There are currently no kits.");
            return;
        }
        Set<Kit> kits = plugin.getKitManager().getKits();
        int size = (kits.size() > 9 ? ((kits.size() / 9) + 1) * 9 : 9);
        Inventory menu = Bukkit.createInventory(null, size, ChatColor.BLUE + "Kits " + "(" + kits.size() + ")");
        for(Kit kit : kits)
        {
            ItemStack icon = kit.getIcon().clone();
            icon.setAmount(kit.getOrder());
            ItemMeta iconm = icon.getItemMeta();
            iconm.setDisplayName(ChatColor.BLUE + kit.getDisplayName());
            List<String> info = new ArrayList();
            info.add(ChatColor.YELLOW + "Usable: " + (kit.isUsable() ? ChatColor.GREEN + "true" : ChatColor.RED + "false"));
            info.add(ChatColor.YELLOW + "Editable: " + (kit.isEditable() ? ChatColor.GREEN + "true" : ChatColor.RED + "false"));
            info.add(ChatColor.YELLOW + "Ranked: " + (kit.isRanked() ? ChatColor.GREEN + "true" : ChatColor.RED + "false"));
            info.add(ChatColor.YELLOW + "Arenas: " + ChatColor.GREEN + kit.getAllArenas().size());
            iconm.setLore(info);
            icon.setItemMeta(iconm);
            menu.addItem(icon);
        }
        p.openInventory(menu);
    }

    public void openEditor(Player p)
    {
        Set<Kit> kits = plugin.getKitManager().getEditableKits();
        if(kits.isEmpty())
        {
            p.sendMessage(ChatColor.RED + "There are currently no editable kits.");
            return;
        }
        int size = (kits.size() > 9 ? ((kits.size() / 9) + 1) * 9 : 9);
        Inventory menu = Bukkit.createInventory(null, size, ChatColor.BLUE + "Select a Kit to Edit");
        for(Kit kit : kits)
        {
            ItemStack icon = kit.getIcon().clone();
            ItemMeta iconm = icon.getItemMeta();
            iconm.setDisplayName(ChatColor.BLUE + kit.getDisplayName());
            icon.setItemMeta(iconm);
            menu.addItem(icon);
        }
        p.openInventory(menu);
    }

    public void editKit(Player p, Kit kit)
    {
        if(!plugin.hasKitEditor())
        {
            p.sendMessage(ChatColor.RED + "Kit editor is currently not set up.");
            return;
        }
        plugin.getKitManager().editKit(p, kit);
        p.teleport(plugin.getKitEditor());
        applyInventory(p, kit.getInventory());
        p.sendMessage(ChatColor.YELLOW + "Now editing kit " + ChatColor.GREEN + kit.getDisplayName() + ChatColor.YELLOW + ".");
    }

    public void showCustomKits(Player p)
    {
        Kit kit = plugin.getKitManager().getKitPlayerIsEditing(p);
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(p);
        Inventory menu = Bukkit.createInventory(null, 36, ChatColor.BLUE + "Edit " + kit.getDisplayName() + " Kits");
        for(int i = 1; i < 4; i++)
        {
            String kitName = ChatColor.GREEN + kit.getDisplayName() + " #" + i;
            int slot = i * 2;
            ItemStack save = new ItemStack(playerData.hasCustomKit(kit, i) ? Material.ENCHANTED_BOOK : Material.BOOK);
            ItemMeta savem = save.getItemMeta();
            savem.setDisplayName(ChatColor.YELLOW + "Save " + kitName);
            save.setItemMeta(savem);
            menu.setItem(slot, save);
            if(playerData.hasCustomKit(kit, i))
            {
                ItemStack load = new ItemStack(Material.CHEST);
                ItemMeta loadm = load.getItemMeta();
                loadm.setDisplayName(ChatColor.YELLOW + "Load " + kitName);
                load.setItemMeta(loadm);

                ItemStack rename = new ItemStack(Material.NAME_TAG);
                ItemMeta renamem = rename.getItemMeta();
                renamem.setDisplayName(ChatColor.YELLOW + "Rename " + kitName);
                renamem.setLore(Arrays.asList(ChatColor.BLUE + "Current: " + playerData.getCustomKit(kit, i).getName()));
                rename.setItemMeta(renamem);

                ItemStack delete = new ItemStack(Material.FIRE);
                ItemMeta deletem = delete.getItemMeta();
                deletem.setDisplayName(ChatColor.YELLOW + "Delete " + kitName);
                delete.setItemMeta(deletem);

                menu.setItem(slot + 9, load);
                menu.setItem(slot + 18, rename);
                menu.setItem(slot + 27, delete);
            }
        }
        plugin.getKitManager().putInCustomKitMenu(p, menu);
        p.openInventory(menu);
    }

    public void refreshCustomKitsMenu(Player p, Kit kit, int customKit)
    {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(p);
        Inventory menu = plugin.getKitManager().getCustomKitMenu(p);
        String kitName = ChatColor.GREEN + kit.getDisplayName() + " #" + customKit;
        int slot = customKit * 2;
        if(playerData.hasCustomKit(kit, customKit))
        {
            if(menu.getItem(slot).getType().equals(Material.BOOK))
            {
                menu.getItem(slot).setType(Material.ENCHANTED_BOOK);

                ItemStack load = new ItemStack(Material.CHEST);
                ItemMeta loadm = load.getItemMeta();
                loadm.setDisplayName(ChatColor.YELLOW + "Load " + kitName);
                load.setItemMeta(loadm);

                ItemStack rename = new ItemStack(Material.NAME_TAG);
                ItemMeta renamem = rename.getItemMeta();
                renamem.setDisplayName(ChatColor.YELLOW + "Rename " + kitName);
                rename.setItemMeta(renamem);

                ItemStack delete = new ItemStack(Material.FIRE);
                ItemMeta deletem = delete.getItemMeta();
                deletem.setDisplayName(ChatColor.YELLOW + "Delete " + kitName);
                delete.setItemMeta(deletem);

                menu.setItem(slot + 9, load);
                menu.setItem(slot + 18, rename);
                menu.setItem(slot + 27, delete);

                p.updateInventory();
                return;
            }
        }
        else
        {
            if(menu.getItem(slot).getType().equals(Material.ENCHANTED_BOOK))
            {
                menu.getItem(slot).setType(Material.BOOK);
                menu.setItem(slot + 9, null);
                menu.setItem(slot + 18, null);
                menu.setItem(slot + 27, null);

                p.updateInventory();
                return;
            }
        }
    }

    public void saveCustomKit(Player p, Kit kit, int customKit)
    {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(p);
        if(playerData.hasCustomKit(kit, customKit)) playerData.setCustomKit(kit, customKit, playerData.getCustomKit(kit, customKit).getName(), generateInventory(p));
        else playerData.setCustomKit(kit, customKit, "Custom " + kit.getDisplayName() + " Kit #" + customKit, generateInventory(p));
        p.sendMessage(ChatColor.YELLOW + "Saved custom kit " + ChatColor.GREEN + kit.getDisplayName() + " #" + customKit + ChatColor.YELLOW + ".");
    }

    public void loadCustomKit(Player p, Kit kit, int customKit)
    {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(p);
        applyInventory(p, playerData.getCustomKit(kit, customKit).getInventory());
        p.closeInventory();
        p.sendMessage(ChatColor.YELLOW + "Loaded custom kit " + ChatColor.GREEN + kit.getDisplayName() + " #" + customKit + ChatColor.YELLOW + ".");
    }

    public void renameCustomKit(Player p, String name, Kit kit, int customKit)
    {
        plugin.getKitManager().finishRenamingCustomKit(p);
        if(name.length() > 24)
        {
            p.sendMessage(ChatColor.RED + "Custom kit name can't exceed 24 characters.");
            return;
        }
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(p);
        playerData.setCustomKit(kit, customKit, name, playerData.getCustomKit(kit, customKit).getInventory());
        p.sendMessage(ChatColor.YELLOW + "Renamed custom kit " + ChatColor.GREEN + kit.getDisplayName() + " #" + customKit + ChatColor.YELLOW + " to " + ChatColor.GREEN + name + ChatColor.YELLOW + ".");
    }

    public void deleteCustomKit(Player p, Kit kit, int customKit)
    {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(p);
        playerData.removeCustomKit(kit, customKit);
        p.sendMessage(ChatColor.YELLOW + "Deleted custom kit " + ChatColor.GREEN + kit.getDisplayName() + " #" + customKit + ChatColor.YELLOW + ".");
    }

    public void stopEditingKit(Player p)
    {
        Kit kit = plugin.getKitManager().getKitPlayerIsEditing(p);
        plugin.getKitManager().stopEditingKit(p);
        plugin.sendToSpawn(p);
        p.sendMessage(ChatColor.YELLOW + "No longer editing kit " + ChatColor.GREEN + kit.getDisplayName() + ChatColor.YELLOW + ".");
    }

    public void giveKits(Player p, Kit kit)
    {
        p.getInventory().setArmorContents(null);
        p.getInventory().clear();
        ItemStack defaultKit = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta defaultKitm = defaultKit.getItemMeta();
        defaultKitm.setDisplayName(ChatColor.YELLOW + "Default " + kit.getDisplayName() + " Kit");
        defaultKit.setItemMeta(defaultKitm);
        p.getInventory().setItem(0, defaultKit);
        if(kit.isEditable())
        {
            PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(p);
            for(int i = 1; i < 4; i++)
            {
                if(playerData.hasCustomKit(kit, i))
                {
                    ItemStack customKit = new ItemStack(Material.ENCHANTED_BOOK);
                    ItemMeta customKitm = customKit.getItemMeta();
                    customKitm.setDisplayName(ChatColor.YELLOW + playerData.getCustomKit(kit, i).getName());
                    customKitm.setLore(Arrays.asList(ChatColor.BLUE + "Custom Kit #" + i));
                    customKit.setItemMeta(customKitm);
                    for(int slot = 2; slot < 5; slot++)
                    {
                        if(p.getInventory().getItem((slot)) == null)
                        {
                            p.getInventory().setItem(slot, customKit);
                            break;
                        }
                    }
                }
            }
        }
        p.updateInventory();
    }

    public Inventory generateInventory(Player p)
    {
        Inventory inventory = Bukkit.createInventory(null, 45);
        for(int i = 4; i > 0; i--) inventory.setItem(39 - (i - 1), p.getInventory().getArmorContents()[i - 1]);
        for(int i = 0; i < 36; i++) inventory.setItem(i, p.getInventory().getItem(i));
        return inventory;
    }

    public void applyInventory(Player p, Inventory inventory)
    {
        p.getInventory().setHelmet(inventory.getItem(36));
        p.getInventory().setChestplate(inventory.getItem(37));
        p.getInventory().setLeggings(inventory.getItem(38));
        p.getInventory().setBoots(inventory.getItem(39));
        for(int i = 0; i < 36; i++) p.getInventory().setItem(i, inventory.getItem(i));
        p.updateInventory();
    }

    public void orderKits(Player p)
    {
        Set<Kit> kits = plugin.getKitManager().getKits();
        if(kits.isEmpty())
        {
            p.sendMessage(ChatColor.RED + "There are currently no kits.");
            return;
        }
        if(kits.size() == 1)
        {
            p.sendMessage(ChatColor.RED + "There is only 1 kit, this feature only sets the order of appearance for multiple kits in menus.");
            return;
        }
        int size = (kits.size() > 9 ? ((kits.size() / 9) + 1) * 9 : 9);
        Inventory menu = Bukkit.createInventory(null, size, ChatColor.BLUE + "Order Kits");
        for(Kit kit : kits)
        {
            ItemStack icon = kit.getIcon().clone();
            ItemMeta iconm = icon.getItemMeta();
            icon.setAmount(kit.getOrder());
            iconm.setDisplayName(ChatColor.BLUE + kit.getDisplayName());
            if(kit.getOrder() == 1 || kit.getOrder() == kits.size())
            {
                if(kit.getOrder() == 1) iconm.setLore(Arrays.asList(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "RMB" + ChatColor.DARK_GRAY + "]" + ChatColor.YELLOW + " to move right"));
                if(kit.getOrder() == kits.size()) iconm.setLore(Arrays.asList(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "LMB" + ChatColor.DARK_GRAY + "]" + ChatColor.YELLOW + " to move left"));
            }
            else iconm.setLore(Arrays.asList(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "LMB" + ChatColor.DARK_GRAY + "]" + ChatColor.YELLOW + " to move left", ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "RMB" + ChatColor.DARK_GRAY + "]" + ChatColor.YELLOW + " to move right"));
            icon.setItemMeta(iconm);
            menu.addItem(icon);
        }
        p.openInventory(menu);
    }
}
