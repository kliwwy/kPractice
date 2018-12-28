package me.wilk3z.kpractice.utils;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemUtil
{
    public String toString(ItemStack item)
    {
        if(item == null) return "[AIR, 1, 0]";
        String s = "[" + item.getType() + ", " + item.getAmount() + ", " + item.getDurability() + "]";
        if(item.hasItemMeta())
        {
            if(item.getItemMeta().hasDisplayName())
            {
                s = s + "|";
                s = s + "[display name: " + item.getItemMeta().getDisplayName() + "]";
            }
            if(item.getItemMeta().hasEnchants())
            {
                s = s + "|";
                s = s + "[enchants: ";
                int size = 0;
                for(Enchantment enchantment : item.getEnchantments().keySet())
                {
                    int level = item.getEnchantments().get(enchantment);
                    if(size == item.getEnchantments().size() - 1) s = s + enchantment.getName() + ":" + level;
                    else s = s + enchantment.getName() + ":" + level + ", ";
                    size++;
                }
                s = s + "]";
            }
            if(item.getItemMeta().hasLore())
            {
                s = s + "|";
                s = s + "[lore: ";
                for(int i = 0; i < item.getItemMeta().getLore().size(); i++)
                {
                    if(i == item.getItemMeta().getLore().size() - 1) s = s + item.getItemMeta().getLore().get(i);
                    else  s = s + item.getItemMeta().getLore().get(i) + ", ";
                }
                s = s + "]";
            }
        }
        return s;
    }

    public ItemStack toItemStack(String s)
    {
        ItemStack item;
        ItemMeta itemm;
        if(s.contains("|"))
        {
            String[] meta = s.split("\\|");
            String itemString = meta[0];
            itemString = itemString.replace("[", "");
            itemString = itemString.replace("]", "");
            String[] itemArray = itemString.split(", ");
            Material material = Material.getMaterial(itemArray[0]);
            int amount = Integer.parseInt(itemArray[1]);
            short durability = Short.parseShort(itemArray[2]);
            item = new ItemStack(material, amount, durability);
            itemm = item.getItemMeta();
            if(s.contains("display name"))
            {
                for(int i = 1; i < meta.length; i++)
                {
                    if(meta[i].contains("display name"))
                    {
                        meta[i] = meta[i].replace("[", "");
                        meta[i] = meta[i].replace("]", "");
                        meta[i] = meta[i].replace("display name: ", "");
                        String displayName = meta[i];
                        itemm.setDisplayName(displayName);
                        break;
                    }
                }
            }
            if(s.contains("enchants"))
            {
                for(int i = 1; i < meta.length; i++)
                {
                    if(meta[i].contains("enchants"))
                    {
                        meta[i] = meta[i].replace("[", "");
                        meta[i] = meta[i].replace("]", "");
                        meta[i] = meta[i].replace("enchants: ", "");
                        if(meta[i].contains(","))
                        {
                            String[] enchantArray = meta[i].split(", ");
                            for(String enchantString : enchantArray)
                            {
                                String[] enchantInfoArray = enchantString.split(":");
                                Enchantment enchantment = Enchantment.getByName(enchantInfoArray[0]);
                                int level = Integer.parseInt(enchantInfoArray[1]);
                                itemm.addEnchant(enchantment, level, true);
                            }
                        }
                        else
                        {
                            String enchantString = meta[i];
                            String[] enchantInfoArray = enchantString.split(":");
                            Enchantment enchantment = Enchantment.getByName(enchantInfoArray[0]);
                            int level = Integer.parseInt(enchantInfoArray[1]);
                            itemm.addEnchant(enchantment, level, true);
                        }
                        break;
                    }
                }
            }
            if(s.contains("lore"))
            {
                for(int i = 1; i < meta.length; i++)
                {
                    if(meta[i].contains("lore"))
                    {
                        meta[i] = meta[i].replace("[", "");
                        meta[i] = meta[i].replace("]", "");
                        meta[i] = meta[i].replace("lore: ", "");
                        List<String> lore = new ArrayList();
                        String[] loreArray = meta[i].split(", ");
                        for(String loreString : loreArray) lore.add(loreString);
                        itemm.setLore(lore);
                        break;
                    }
                }
            }
        }
        else
        {
            String itemString = s;
            itemString = itemString.replace("[", "");
            itemString = itemString.replace("]", "");
            String[] itemArray = itemString.split(", ");
            Material material = Material.getMaterial(itemArray[0]);
            int amount = Integer.parseInt(itemArray[1]);
            short durability = Short.parseShort(itemArray[2]);
            item = new ItemStack(material, amount, durability);
            itemm = item.getItemMeta();
        }
        item.setItemMeta(itemm);
        return item;
    }

    public String toString(Inventory inventory)
    {
        StringBuilder build = new StringBuilder();
        build.append(inventory.getSize() + "&");
        for(int i = 0; i < inventory.getSize(); i++)
        {
            if(i == inventory.getSize() - 1) build.append(toString(inventory.getItem(i)));
            else build.append(toString(inventory.getItem(i)) + "&");
        }
        return build.toString();
    }

    public Inventory toInventory(String s)
    {
        String data[] = s.split("&");
        Inventory inventory = Bukkit.createInventory(null, Integer.parseInt(data[0]));
        for(int i = 1; i < data.length; i++)
        {
            ItemStack item = toItemStack(data[i]);
            inventory.setItem(i - 1, item);
        }
        return inventory;
    }

    public boolean addItem(Player p, ItemStack item)
    {
        int emptySlots = 0;
        for(int i = 0; i < p.getInventory().getSize(); i++) if(p.getInventory().getItem(i) == null) emptySlots++;
        if(emptySlots > 0)
        {
            p.getInventory().addItem(item);
            p.updateInventory();
            return true;
        }
        else
        {
            for(int i = 0; i < p.getInventory().getSize(); i++)
            {
                ItemStack invItem = p.getInventory().getItem(i);
                if(invItem != null)
                {
                    if(item.hasItemMeta())
                    {
                        if(invItem.hasItemMeta())
                        {
                            if(invItem.getType().equals(item.getType()) && invItem.getDurability() == item.getDurability())
                            {
                                if(invItem.getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName()))
                                {
                                    if(invItem.getMaxStackSize() > invItem.getAmount() + 1)
                                    {
                                        p.getInventory().addItem(item);
                                        p.updateInventory();
                                        return true;
                                    }
                                    return false;
                                }
                                return false;
                            }
                            return false;
                        }
                        return false;
                    }
                    if(!invItem.hasItemMeta())
                    {
                        if(invItem.getType().equals(item.getType()) && invItem.getDurability() == item.getDurability())
                        {
                            if(invItem.getMaxStackSize() > invItem.getAmount() + 1)
                            {
                                p.getInventory().addItem(item);
                                p.updateInventory();
                                return true;
                            }
                            return false;
                        }
                        return false;
                    }
                    return false;
                }
                p.getInventory().addItem(item);
                p.updateInventory();
                return true;
            }
            return false;
        }
    }

    public String getItemName(ItemStack item)
    {
        if(item == null || item.getType().equals(Material.AIR)) return "air";
        if(item.getItemMeta().hasDisplayName()) return "[" + item.getItemMeta().getDisplayName() + "]";
        Material material = item.getType();
        String name = material.toString();
        name = name.replaceAll("_", " ");
        name = WordUtils.capitalizeFully(name);
        return name;
    }

    public Enchantment getEnchantment(String enchant)
    {
        enchant = enchant.replaceAll("_", " ").toLowerCase().trim();
        Enchantment enchantment = null;
        if(enchant.equalsIgnoreCase("unbreaking")) enchantment = Enchantment.DURABILITY;
        if(enchant.equalsIgnoreCase("efficiency") || enchant.equalsIgnoreCase("dig speed")) enchantment = Enchantment.DIG_SPEED;
        if(enchant.equalsIgnoreCase("fortune") || enchant.equalsIgnoreCase("loot bonus blocks")) enchantment = Enchantment.LOOT_BONUS_BLOCKS;
        if(enchant.equalsIgnoreCase("silk touch")) enchantment = Enchantment.SILK_TOUCH;
        if(enchant.equalsIgnoreCase("sharpness") || enchant.equalsIgnoreCase("damage all")) enchantment = Enchantment.DAMAGE_ALL;
        if(enchant.equalsIgnoreCase("smite") || enchant.equalsIgnoreCase("damage undead")) enchantment = Enchantment.DAMAGE_UNDEAD;
        if(enchant.equalsIgnoreCase("bane of arthropods") || enchant.equalsIgnoreCase("damage arthropods")) enchantment = Enchantment.DAMAGE_ARTHROPODS;
        if(enchant.equalsIgnoreCase("fire aspect")) enchantment = Enchantment.FIRE_ASPECT;
        if(enchant.equalsIgnoreCase("looting") || enchant.equalsIgnoreCase("loot bonus mobs")) enchantment = Enchantment.LOOT_BONUS_MOBS;
        if(enchant.equalsIgnoreCase("knockback")) enchantment = Enchantment.KNOCKBACK;
        if(enchant.equalsIgnoreCase("power") || enchant.equalsIgnoreCase("arrow damage")) enchantment = Enchantment.ARROW_DAMAGE;
        if(enchant.equalsIgnoreCase("flame") || enchant.equalsIgnoreCase("arrow fire")) enchantment = Enchantment.ARROW_FIRE;
        if(enchant.equalsIgnoreCase("punch") || enchant.equalsIgnoreCase("arrow knockback")) enchantment = Enchantment.ARROW_KNOCKBACK;
        if(enchant.equalsIgnoreCase("infinity") || enchant.equalsIgnoreCase("arrow infinite")) enchantment = Enchantment.ARROW_INFINITE;
        if(enchant.equalsIgnoreCase("protection") || enchant.equalsIgnoreCase("protection environmental")) enchantment = Enchantment.PROTECTION_ENVIRONMENTAL;
        if(enchant.equalsIgnoreCase("fire protection") || enchant.equalsIgnoreCase("protection fire")) enchantment = Enchantment.PROTECTION_FIRE;
        if(enchant.equalsIgnoreCase("projectile protection") || enchant.equalsIgnoreCase("protection projectile")) enchantment = Enchantment.PROTECTION_PROJECTILE;
        if(enchant.equalsIgnoreCase("blast protection") || enchant.equalsIgnoreCase("protection explosions")) enchantment = Enchantment.PROTECTION_EXPLOSIONS;
        if(enchant.equalsIgnoreCase("feather falling") || enchant.equalsIgnoreCase("protection fall")) enchantment = Enchantment.PROTECTION_FALL;
        if(enchant.equalsIgnoreCase("thorns")) enchantment = Enchantment.THORNS;
        if(enchant.equalsIgnoreCase("respiration") || enchant.equalsIgnoreCase("oxygen")) enchantment = Enchantment.OXYGEN;
        if(enchant.equalsIgnoreCase("aqua affinity") || enchant.equalsIgnoreCase("water worker")) enchantment = Enchantment.WATER_WORKER;
        if(enchant.equalsIgnoreCase("lure")) enchantment = Enchantment.LURE;
        if(enchant.equalsIgnoreCase("luck of the sea") || enchant.equalsIgnoreCase("luck")) enchantment = Enchantment.LUCK;
        return enchantment;
    }
}
