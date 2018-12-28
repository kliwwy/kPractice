package me.wilk3z.kpractice.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtil
{
    public String toString(Location loc)
    {
        return "[" + loc.getWorld().getName() + ", " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ", " + loc.getYaw() + ", " + loc.getPitch() + "]";
    }

    public Location toLocation(String s)
    {
        s = s.replaceAll("\\[", "");
        s = s.replaceAll("\\]", "");
        String[] info = s.split(", ");
        World w = Bukkit.getWorld(info[0]);
        double x = Double.parseDouble(info[1]);
        double y = Double.parseDouble(info[2]);
        double z = Double.parseDouble(info[3]);
        float yaw = Float.parseFloat(info[4]);
        float pitch = Float.parseFloat(info[5]);
        Location loc = new Location(w, x, y, z, yaw, pitch);
        return loc;
    }
}