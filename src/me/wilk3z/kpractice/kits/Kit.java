package me.wilk3z.kpractice.kits;

import me.wilk3z.kpractice.Practice;
import me.wilk3z.kpractice.arenas.Arena;
import me.wilk3z.kpractice.utils.FileUtil;
import me.wilk3z.kpractice.utils.ItemUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class Kit
{
    public Practice plugin;
    public String name;
    public ItemStack icon;
    public boolean usable;
    public boolean editable;
    public boolean ranked;
    public Inventory inventory;
    public Inventory editorInventory;
    public Set<Arena> arenas;
    public int order;

    public Kit(Practice plugin, String name)
    {
        this.plugin = plugin;
        this.name = name;
    }

    public void load()
    {
        ItemUtil itemUtil = new ItemUtil();
        YamlConfiguration kitYaml = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + File.separator + "Kits", name + ".yml"));
        icon = itemUtil.toItemStack(kitYaml.getString("icon"));
        usable = kitYaml.getBoolean("usable");
        editable = kitYaml.getBoolean("editable");
        ranked = kitYaml.getBoolean("ranked");
        inventory = itemUtil.toInventory(kitYaml.getString("inventory.default"));
        editorInventory = itemUtil.toInventory(kitYaml.getString("inventory.editor"));
        arenas = convertArenas(kitYaml.getStringList("arenas"));
        if(kitYaml.isSet("order")) order = kitYaml.getInt("order");
        else order = plugin.getKitManager().getKits().size() + 1;
    }

    public long save()
    {
        ItemUtil itemUtil = new ItemUtil();
        long before = System.currentTimeMillis();
        long after = System.currentTimeMillis();
        File kitFile = new File(plugin.getDataFolder() + File.separator + "Kits", name + ".yml");
        YamlConfiguration kitYaml = YamlConfiguration.loadConfiguration(kitFile);
        kitYaml.set("icon", itemUtil.toString(new ItemStack(icon.getType(), 1, icon.getDurability())));
        kitYaml.set("usable", usable);
        kitYaml.set("editable", editable);
        kitYaml.set("ranked", ranked);
        kitYaml.set("inventory.default", itemUtil.toString(inventory));
        kitYaml.set("inventory.editor", itemUtil.toString(editorInventory));
        kitYaml.set("arenas", convertArenas());
        kitYaml.set("order", order);
        try
        {
            kitYaml.save(kitFile);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        long time = after - before;
        return time;
    }

    public void delete()
    {
        FileUtil fileUtil = new FileUtil();
        File kitFile = new File(plugin.getDataFolder() + File.separator + "Kits", getName() + ".yml");
        fileUtil.delete(kitFile);
    }

    public String getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return name.replaceAll("_", " ");
    }

    public void setIcon(ItemStack icon)
    {
        this.icon = new ItemStack(icon.getType(), 1, icon.getDurability());
    }

    public ItemStack getIcon()
    {
        return icon;
    }

    public void setUsable(boolean usable)
    {
        this.usable = usable;
    }

    public boolean isUsable()
    {
        return usable;
    }

    public void setEditable(boolean editable)
    {
        this.editable = editable;
    }

    public boolean isEditable()
    {
        return editable;
    }

    public void setRanked(boolean ranked)
    {
        this.ranked = ranked;
    }

    public boolean isRanked()
    {
        return ranked;
    }

    public void setInventory(Inventory inventory)
    {
        this.inventory = inventory;
    }

    public Inventory getInventory()
    {
        return inventory;
    }

    public void applyInventory(Player p)
    {
        ItemStack[] armor = new ItemStack[4];
        for(int i = 0; i < 3; i++) armor[i] = inventory.getItem(i);
        p.getInventory().setArmorContents(armor);

    }

    public void setEditorInventory(Inventory editorInventory)
    {
        this.editorInventory = editorInventory;
    }

    public Inventory getEditorInventory()
    {
        return editorInventory;
    }

    public void setArenas(Set<Arena> arenas)
    {
        this.arenas = arenas;
    }

    public Set<Arena> getAllArenas()
    {
        return arenas;
    }

    public List<Arena> getAvailableArenas()
    {
        List<Arena> arenas = new ArrayList();
        for(Arena arena : this.arenas) if(arena.isUsable()) arenas.add(arena);
        return arenas;
    }

    public Arena getRandomArena()
    {
        int random = new Random().nextInt(getAvailableArenas().size());
        return getAvailableArenas().get(random);
    }

    public List<String> convertArenas()
    {
        List<String> arenaNames = new ArrayList();
        for(Arena arena : arenas) arenaNames.add(arena.getName());
        return arenaNames;
    }

    public Set<Arena> convertArenas(List<String> arenaNames)
    {
        Set<Arena> arenas = new HashSet();
        for(String arenaName : arenaNames) arenas.add(plugin.getArenaManager().getArena(arenaName));
        return arenas;
    }

    public boolean hasArena(Arena arena)
    {
        boolean check = false;
        for(Arena arenas : this.arenas)
        {
            if(arena.getName().equals(arenas.getName()))
            {
                check = true;
                break;
            }
        }
        return check;
    }

    public void setOrder(int order)
    {
        this.order = order;
    }

    public int getOrder()
    {
        return order;
    }
}
