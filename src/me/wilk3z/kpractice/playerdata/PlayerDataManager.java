package me.wilk3z.kpractice.playerdata;

import me.wilk3z.kpractice.Practice;
import me.wilk3z.kpractice.kits.Kit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager implements Listener
{
    public Practice plugin;
    public Map<UUID, PlayerData> playerData;

    public PlayerDataManager(Practice plugin)
    {
        this.plugin = plugin;
        playerData = new HashMap();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void loadPlayerData()
    {
        File playerDatasFile = new File(plugin.getDataFolder(), "Player Data");
        if(!playerDatasFile.exists()) playerDatasFile.mkdirs();
        if(playerDatasFile.listFiles().length == 0) return;
        for(File playerDataFile : playerDatasFile.listFiles())
        {
            UUID uuid = UUID.fromString(playerDataFile.getName().replace(".yml", ""));
            PlayerData playerData = new PlayerData(plugin, uuid);
            this.playerData.put(uuid, playerData);
        }
    }

    public long savePlayerData()
    {
        long before = System.currentTimeMillis();
        for(PlayerData playerData : this.playerData.values()) playerData.save();
        long after = System.currentTimeMillis();
        long time = after - before;
        return time;
    }

    public void create(Player p)
    {
        PlayerData playerData = new PlayerData(plugin, p.getUniqueId());
        this.playerData.put(p.getUniqueId(), playerData);
    }

    public boolean hasPlayerData(Player p)
    {
        return playerData.containsKey(p.getUniqueId());
    }

    public PlayerData getPlayerData(Player p)
    {
        return playerData.get(p.getUniqueId());
    }

    public int getEloChange(Player winner, Player loser, Kit kit)
    {
        int change = 0;
        double thingy = 1.0 / (1.0 + Math.pow(10.0, (getPlayerData(winner).getElo(kit) - getPlayerData(loser).getElo(kit)) / 400.0));
        change = (int) (thingy * 32.0);
        if(change <= 0) change = 1;
        int eloChange = ((change > 30) ? 30 : change);
        return eloChange;
    }

    @EventHandler
    public void checkPlayer(PlayerJoinEvent e)
    {
        Player p = e.getPlayer();
        if(!hasPlayerData(p)) create(p);
        getPlayerData(p).reload();
    }
}
