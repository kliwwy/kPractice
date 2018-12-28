package me.wilk3z.kpractice.playerdata;

import me.wilk3z.kpractice.Practice;
import me.wilk3z.kpractice.kits.CustomKit;
import me.wilk3z.kpractice.kits.Kit;
import me.wilk3z.kpractice.utils.ItemUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class PlayerData
{
    public Practice plugin;
    public UUID uuid;
    public Map<String, int[]> wins;
    public Map<String, int[]> losses;
    public Map<String, Integer> elo;
    public Map<String, Map<Integer, CustomKit>> customKits;

    public PlayerData(Practice plugin, UUID uuid)
    {
        this.plugin = plugin;
        this.uuid = uuid;
        wins = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        losses = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        elo = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        customKits = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        load();
    }

    public void load()
    {
        ItemUtil itemUtil = new ItemUtil();
        File playerDataFile = new File(plugin.getDataFolder() + File.separator + "Player Data", uuid.toString() + ".yml");
        int[] preset = new int[] { 0, 0 };
        Map<Integer, CustomKit> customKits = new HashMap();
        for(int i = 1; i < 4; i++) customKits.put(i, null);
        if(playerDataFile.exists())
        {
            YamlConfiguration playerDataYaml = YamlConfiguration.loadConfiguration(playerDataFile);
            for(Kit kit : plugin.getKitManager().getKits())
            {
                if(playerDataYaml.isSet(kit.getName() + ".elo"))
                {
                    wins.put(kit.getName(), new int[] { playerDataYaml.getInt(kit.getName() + ".wins.un-ranked"), playerDataYaml.getInt(kit.getName() + ".wins.ranked") });
                    losses.put(kit.getName(), new int[] { playerDataYaml.getInt(kit.getName() + ".losses.un-ranked"), playerDataYaml.getInt(kit.getName() + ".losses.ranked") });
                    elo.put(kit.getName(), playerDataYaml.getInt(kit.getName() + ".elo"));
                    for(int i = 1; i < 4; i++) if(playerDataYaml.getBoolean(kit.getName() + ".custom kits." + i + ".usable")) customKits.put(i, new CustomKit(playerDataYaml.getString(kit.getName() + ".custom kits." + i + ".name"), itemUtil.toInventory(playerDataYaml.getString(kit.getName() + ".custom kits." + i + ".inventory"))));
                    this.customKits.put(kit.getName(), customKits);
                }
                else
                {
                    wins.put(kit.getName(), preset);
                    losses.put(kit.getName(), preset);
                    elo.put(kit.getName(), 1000);
                    this.customKits.put(kit.getName(), customKits);
                }
            }
        }
        else
        {
            for(Kit kit : plugin.getKitManager().getKits())
            {
                wins.put(kit.getName(), preset);
                losses.put(kit.getName(), preset);
                elo.put(kit.getName(), 1000);
                this.customKits.put(kit.getName(), customKits);
            }
        }
    }

    public void reload()
    {
        int[] preset = new int[] { 0, 0 };
        Map<Integer, CustomKit> customKits = new HashMap();
        for(Kit kit : plugin.getKitManager().getKits())
        {
            if(!elo.containsKey(kit.getName()))
            {
                wins.put(kit.getName(), preset);
                losses.put(kit.getName(), preset);
                elo.put(kit.getName(), 1000);
                this.customKits.put(kit.getName(), customKits);
            }
        }
    }

    public void save()
    {
        ItemUtil itemUtil = new ItemUtil();
        File playerDataFile = new File(plugin.getDataFolder() + File.separator + "Player Data", uuid.toString() + ".yml");
        YamlConfiguration playerDataYaml = YamlConfiguration.loadConfiguration(playerDataFile);
        for(Kit kit : plugin.getKitManager().getKits())
        {
            playerDataYaml.set(kit.getName() + ".wins.un-ranked", getWins(kit, false));
            playerDataYaml.set(kit.getName() + ".wins.ranked", getWins(kit, true));
            playerDataYaml.set(kit.getName() + ".losses.un-ranked", getLosses(kit, false));
            playerDataYaml.set(kit.getName() + ".losses.ranked", getLosses(kit, true));
            playerDataYaml.set(kit.getName() + ".elo", getElo(kit));
            for(int i = 1; i < 4; i++)
            {
                if(hasCustomKit(kit, i))
                {
                    playerDataYaml.set(kit.getName() + ".custom kits." + i + ".usable", true);
                    playerDataYaml.set(kit.getName() + ".custom kits." + i + ".name", getCustomKit(kit, i).getName());
                    playerDataYaml.set(kit.getName() + ".custom kits." + i + ".inventory", itemUtil.toString(getCustomKit(kit, i).getInventory()));
                }
                else
                {
                    playerDataYaml.set(kit.getName() + ".custom kits." + i + ".usable", false);
                    playerDataYaml.set(kit.getName() + ".custom kits." + i + ".name", null);
                    playerDataYaml.set(kit.getName() + ".custom kits." + i + ".inventory", null);
                }
            }
        }
        try
        {
            playerDataYaml.save(playerDataFile);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setWins(Kit kit, int wins, boolean ranked)
    {
        this.wins.put(kit.getName(), ranked ? new int[] { getWins(kit, !ranked), wins } : new int[] { wins, getWins(kit, !ranked) });
    }

    public void setLosses(Kit kit, int losses, boolean ranked)
    {
        this.wins.put(kit.getName(), ranked ? new int[] { getLosses(kit, !ranked), losses } : new int[] { losses, getLosses(kit, !ranked) });
    }

    public void setElo(Kit kit, int elo)
    {
        this.elo.put(kit.getName(), elo);
    }

    public int getWins(Kit kit, boolean ranked)
    {
        return ranked ? wins.get(kit.getName())[1] : wins.get(kit.getName())[0];
    }

    public int getTotalWins(boolean ranked)
    {
        int wins = 0;
        for(Kit kit : plugin.getKitManager().getKits()) wins += getWins(kit, ranked);
        return wins;
    }

    public int getLosses(Kit kit, boolean ranked)
    {
        return ranked ? losses.get(kit.getName())[1] : losses.get(kit.getName())[0];
    }

    public int getTotalLosses(boolean ranked)
    {
        int losses = 0;
        for(Kit kit : plugin.getKitManager().getKits()) losses += getLosses(kit, ranked);
        return losses;
    }

    public int getElo(Kit kit)
    {
        return elo.get(kit.getName());
    }

    public int getGlobalElo()
    {
        int elo = 0;
        for(Kit kit : plugin.getKitManager().getKits())
        {
            elo += getElo(kit) - 1000;
        }
        elo /= 3;
        elo += 1000;
        return elo;
    }

    public void setCustomKit(Kit kit, int customKit, String name, Inventory inventory)
    {
        Map<Integer, CustomKit> customKits = this.customKits.get(kit.getName());
        customKits.put(customKit, new CustomKit(name, inventory));
        this.customKits.put(kit.getName(), customKits);
    }

    public void removeCustomKit(Kit kit, int customKit)
    {
        Map<Integer, CustomKit> customKits = this.customKits.get(kit.getName());
        customKits.put(customKit, null);
        this.customKits.put(kit.getName(), customKits);
    }

    public boolean hasCustomKit(Kit kit, int customKit)
    {
        if(customKits.get(kit.getName()).get(customKit) != null) return true;
        else return false;
    }

    public CustomKit getCustomKit(Kit kit, int customKit)
    {
        return customKits.get(kit.getName()).get(customKit);
    }
}
