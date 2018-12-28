package me.wilk3z.kpractice.kits;

import me.wilk3z.kpractice.Practice;
import me.wilk3z.kpractice.arenas.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class KitManager
{
    public Practice plugin;
    public Map<String, Kit> kits;
    public Map<UUID, Kit> editKit;
    public Map<UUID, Inventory> customKitMenu;
    public Map<UUID, Integer> renameCustomKit;

    public KitManager(Practice plugin)
    {
        this.plugin = plugin;
        kits = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        editKit = new HashMap();
        customKitMenu = new HashMap();
        renameCustomKit = new HashMap();
    }

    public void loadKits()
    {
        File kitsFile = new File(plugin.getDataFolder(), "Kits");
        if(!kitsFile.exists()) kitsFile.mkdirs();
        if(kitsFile.listFiles().length == 0) return;
        for(File kitFile : kitsFile.listFiles())
        {
            String kitName = kitFile.getName().replace(".yml", "");
            Kit kit = new Kit(plugin, kitName);
            kit.load();
            addKit(kit);
        }
    }

    public long saveKits()
    {
        long before = System.currentTimeMillis();
        for(Kit kit : kits.values()) kit.save();
        long after = System.currentTimeMillis();
        long time = after - before;
        return time;
    }

    public void addKit(Kit kit)
    {
        kits.put(kit.getName(), kit);
        if(kit.getOrder() == 0) kit.setOrder(kits.size());
    }

    public void createKit(String kitName, Inventory inventory)
    {
        Kit kit = new Kit(plugin, kitName);
        kit.setIcon(new ItemStack(Material.DIAMOND_SWORD));
        kit.setUsable(false);
        kit.setEditable(false);
        kit.setRanked(false);
        kit.setInventory(inventory);
        kit.setEditorInventory(Bukkit.createInventory(null, 54));
        kit.setArenas(plugin.getArenaManager().getArenas());
        kit.setOrder(kits.size() + 1);
        kits.put(kitName, kit);
    }

    public void deleteKit(String kitName)
    {
        Kit kit = getKit(kitName);
        kits.remove(kitName);
        kit.delete();
    }

    public boolean kitExists(String kitName)
    {
        return kits.containsKey(kitName);
    }

    public Kit getKit(String kitName)
    {
        return kits.get(kitName);
    }

    public LinkedHashSet<Kit> getKits()
    {
        LinkedHashSet<Kit> kits = new LinkedHashSet();
        for(int order = 0; order < this.kits.size(); order++)
        {
            for(Kit kit : this.kits.values())
            {
                if(kit.getOrder() == order + 1)
                {
                    kits.add(kit);
                    break;
                }
            }
        }
        return kits;
    }

    public LinkedHashSet<Kit> getAvailableKits()
    {
        LinkedHashSet<Kit> kits = new LinkedHashSet();
        for(Kit kit : getKits()) if(kit.isUsable()) kits.add(kit);
        return kits;
    }

    public LinkedHashSet<Kit> getEditableKits()
    {
        LinkedHashSet<Kit> kits = new LinkedHashSet();
        for(Kit kit : getAvailableKits()) if(kit.isEditable()) kits.add(kit);
        return kits;
    }

    public LinkedHashSet<Kit> getRankedKits()
    {
        LinkedHashSet<Kit> kits = new LinkedHashSet();
        for(Kit kit : getAvailableKits()) if(kit.isRanked()) kits.add(kit);
        return kits;
    }

    public void editKit(Player p, Kit kit)
    {
        editKit.put(p.getUniqueId(), kit);
    }

    public void stopEditingKit(Player p)
    {
        editKit.remove(p.getUniqueId());
        if(isInCustomKitMenu(p)) customKitMenu.remove(p.getUniqueId());
        if(isRenamingCustomKit(p)) renameCustomKit.remove(p.getUniqueId());
        if(plugin.getEnderpearlManager().isOnCooldown(p)) plugin.getEnderpearlManager().removeFromCooldown(p);
    }

    public boolean isEditingKit(Player p)
    {
        return editKit.containsKey(p.getUniqueId());
    }

    public Kit getKitPlayerIsEditing(Player p)
    {
        return editKit.get(p.getUniqueId());
    }

    public void putInCustomKitMenu(Player p, Inventory inventory)
    {
        customKitMenu.put(p.getUniqueId(), inventory);
    }

    public void removeFromCustomKitMenu(Player p)
    {
        customKitMenu.remove(p.getUniqueId());
    }

    public boolean isInCustomKitMenu(Player p)
    {
        return customKitMenu.containsKey(p.getUniqueId());
    }

    public Inventory getCustomKitMenu(Player p)
    {
        return customKitMenu.get(p.getUniqueId());
    }

    public void renameCustomKit(Player p, int customKit)
    {
        renameCustomKit.put(p.getUniqueId(), customKit);
    }

    public void finishRenamingCustomKit(Player p)
    {
        renameCustomKit.remove(p.getUniqueId());
    }

    public boolean isRenamingCustomKit(Player p)
    {
        return renameCustomKit.containsKey(p.getUniqueId());
    }

    public int getCustomKitBeingRenamed(Player p)
    {
        return renameCustomKit.get(p.getUniqueId());
    }
}
