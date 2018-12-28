package me.wilk3z.kpractice.kits;

import org.bukkit.inventory.Inventory;

public class CustomKit
{
    public String name;
    public Inventory inventory;

    public CustomKit(String name, Inventory inventory)
    {
        this.name = name;
        this.inventory = inventory;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean hasName()
    {
        if(name != null) return true;
        else return false;
    }

    public String getName()
    {
        return name;
    }

    public void setInventory(Inventory inventory)
    {
        this.inventory = inventory;
    }

    public boolean hasInventory()
    {
        if(inventory != null) return true;
        else return false;
    }

    public Inventory getInventory()
    {
        return inventory;
    }
}
