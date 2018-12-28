package me.wilk3z.kpractice.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Logit
{
    public String prefix;

    public Logit(String prefix)
    {
        this.prefix = prefix;
    }

    public void output(String msg)
    {
        Bukkit.getConsoleSender().sendMessage(prefix + " " + ChatColor.RESET + msg);
    }
}