package me.wilk3z.kpractice;

import me.wilk3z.kpractice.arenas.ArenaManager;
import me.wilk3z.kpractice.builders.BuildManager;
import me.wilk3z.kpractice.commands.*;
import me.wilk3z.kpractice.duels.DuelListener;
import me.wilk3z.kpractice.duels.DuelManager;
import me.wilk3z.kpractice.enderpearls.EnderpearlListener;
import me.wilk3z.kpractice.enderpearls.EnderpearlManager;
import me.wilk3z.kpractice.kits.Kit;
import me.wilk3z.kpractice.kits.KitListener;
import me.wilk3z.kpractice.kits.KitManager;
import me.wilk3z.kpractice.listeners.BasicListener;
import me.wilk3z.kpractice.playerdata.PlayerDataManager;
import me.wilk3z.kpractice.queues.QueueListener;
import me.wilk3z.kpractice.queues.QueueManager;
import me.wilk3z.kpractice.spectators.SpectatorListener;
import me.wilk3z.kpractice.spectators.SpectatorManager;
import me.wilk3z.kpractice.teams.TeamListener;
import me.wilk3z.kpractice.teams.TeamManager;
import me.wilk3z.kpractice.utils.LocationUtil;
import me.wilk3z.kpractice.utils.Logit;
import me.wilk3z.kpractice.vanish.VanishListener;
import me.wilk3z.kpractice.vanish.VanishManager;
import me.wilk3z.kpractice.vanish.VanishPackets;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.io.File;

public class Practice extends JavaPlugin
{
    public Logit logit;
    public ArenaManager arenaManager;
    public KitManager kitManager;
    public PlayerDataManager playerDataManager;
    public TeamManager teamManager;
    public DuelManager duelManager;
    public EnderpearlManager enderpearlManager;
    public QueueManager queueManager;
    public SpectatorManager spectatorManager;
    public BuildManager buildManager;
    public VanishManager vanishManager;
    public Location spawn;
    public Location kitEditor;

    public void onEnable()
    {
        logit = new Logit(ChatColor.YELLOW + "[" + ChatColor.GREEN + "Practice" + ChatColor.YELLOW + "]");
        logit.output(ChatColor.YELLOW + "Loading...");
        load();
        logit.output(ChatColor.GREEN + "Successfully loaded.");
    }

    public void onDisable()
    {
        for(Player all : Bukkit.getServer().getOnlinePlayers()) all.kickPlayer(ChatColor.RED + "Server is restarting.");
        save();
    }

    public void load()
    {
        registerCommands();
        loadManagers();
        registerListeners();
        arenaManager.loadArenas();
        kitManager.loadKits();
        playerDataManager.loadPlayerData();
        loadSpawns();
        removeAllEntities();
    }

    public boolean loadSpawns()
    {
        LocationUtil locationUtil = new LocationUtil();
        File spawnsFile = new File(getDataFolder(), "spawns.yml");
        YamlConfiguration spawnsYaml = YamlConfiguration.loadConfiguration(spawnsFile);
        if(!spawnsFile.exists())
        {
            spawnsYaml.set("spawn.spawn", "none");
            spawnsYaml.set("spawn.editor", "none");
            try
            {
                spawnsYaml.save(spawnsFile);
            }
            catch(Exception e)
            {
                e.printStackTrace();
                return false;
            }
        }
        if(!spawnsYaml.getString("spawn.spawn").equals("none")) spawn = locationUtil.toLocation(spawnsYaml.getString("spawn.spawn"));
        if(!spawnsYaml.getString("spawn.editor").equals("none")) kitEditor = locationUtil.toLocation(spawnsYaml.getString("spawn.editor"));
        return true;
    }

    public void save()
    {
        playerDataManager.savePlayerData();
        arenaManager.saveArenas();
        kitManager.saveKits();
        saveSpawns();
    }

    public void saveSpawns()
    {
        LocationUtil locationUtil = new LocationUtil();
        File spawnsFile = new File(getDataFolder(), "spawns.yml");
        YamlConfiguration spawnsYaml = YamlConfiguration.loadConfiguration(spawnsFile);
        if(hasSpawn()) spawnsYaml.set("spawn.spawn", locationUtil.toString(spawn));
        if(hasKitEditor()) spawnsYaml.set("spawn.editor", locationUtil.toString(kitEditor));
        try
        {
            spawnsYaml.save(spawnsFile);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void registerCommands()
    {
        getCommand("setspawn").setExecutor(new cmdSetspawn(this));
        getCommand("arena").setExecutor(new cmdArena(this));
        getCommand("kit").setExecutor(new cmdKit(this));
        getCommand("team").setExecutor(new cmdTeam(this));
        getCommand("duel").setExecutor(new cmdDuel(this));
        getCommand("accept").setExecutor(new cmdAccept(this));
        getCommand("spectate").setExecutor(new cmdSpectate(this));
        getCommand("_").setExecutor(new cmdInventory(this));
        getCommand("build").setExecutor(new cmdBuild(this));
        getCommand("spawn").setExecutor(new cmdSpawn(this));
    }

    public void loadManagers()
    {
        arenaManager = new ArenaManager(this);
        kitManager = new KitManager(this);
        playerDataManager = new PlayerDataManager(this);
        teamManager = new TeamManager();
        duelManager = new DuelManager();
        enderpearlManager = new EnderpearlManager();
        queueManager = new QueueManager();
        spectatorManager = new SpectatorManager();
        buildManager = new BuildManager();
        vanishManager = new VanishManager();
    }

    public void registerListeners()
    {
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        pluginManager.registerEvents(new BasicListener(this), this);
        pluginManager.registerEvents(new KitListener(this), this);
        pluginManager.registerEvents(new DuelListener(this), this);
        pluginManager.registerEvents(new TeamListener(this), this);
        pluginManager.registerEvents(new EnderpearlListener(this), this);
        pluginManager.registerEvents(new QueueListener(this), this);
        pluginManager.registerEvents(new SpectatorListener(this), this);
        pluginManager.registerEvents(new VanishListener(this), this);
        VanishPackets vanishPackets = new VanishPackets(this);
    }

    public Logit getLogit()
    {
        return logit;
    }

    public ArenaManager getArenaManager()
    {
        return arenaManager;
    }

    public KitManager getKitManager()
    {
        return kitManager;
    }

    public PlayerDataManager getPlayerDataManager()
    {
        return playerDataManager;
    }

    public TeamManager getTeamManager()
    {
        return teamManager;
    }

    public DuelManager getDuelManager()
    {
        return duelManager;
    }

    public EnderpearlManager getEnderpearlManager()
    {
        return enderpearlManager;
    }

    public QueueManager getQueueManager()
    {
        return queueManager;
    }

    public SpectatorManager getSpectatorManager()
    {
        return spectatorManager;
    }

    public BuildManager getBuildManager()
    {
        return buildManager;
    }

    public VanishManager getVanishManager()
    {
        return vanishManager;
    }

    public void removeAllEntities()
    {
        for(World world : Bukkit.getServer().getWorlds())
        {
            for(Entity entity : world.getEntities())
            {
                entity.remove();
            }
        }
    }

    public boolean hasSpawn()
    {
        if(spawn != null) return true;
        else return false;
    }

    public void setSpawn(Location spawn)
    {
        this.spawn = spawn;
    }

    public void teleportToSpawn(Player p)
    {
        if(hasSpawn()) p.teleport(spawn);
        else p.teleport(new Location(p.getWorld(), 0, p.getWorld().getHighestBlockYAt(0, 0), 0));
    }

    public void giveSpawnItems(Player p)
    {
        p.getInventory().setArmorContents(null);
        p.getInventory().clear();

        ItemStack editKits = new ItemStack(Material.BOOK);
        ItemMeta editKitsm = editKits.getItemMeta();
        editKitsm.setDisplayName(ChatColor.GOLD + "Edit Kits");
        editKits.setItemMeta(editKitsm);

        ItemStack createTeam = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
        ItemMeta createTeamm = createTeam.getItemMeta();
        createTeamm.setDisplayName(ChatColor.YELLOW + "Create Team");
        createTeam.setItemMeta(createTeamm);

        ItemStack unrankedQueue = new ItemStack(Material.IRON_SWORD);
        ItemMeta unrankedQueuem = unrankedQueue.getItemMeta();
        unrankedQueuem.setDisplayName(ChatColor.BLUE + "Un-Ranked Queue");
        unrankedQueue.setItemMeta(unrankedQueuem);

        ItemStack rankedQueue = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta rankedQueuem = rankedQueue.getItemMeta();
        rankedQueuem.setDisplayName(ChatColor.GREEN + "Ranked Queue");
        rankedQueue.setItemMeta(rankedQueuem);

        p.getInventory().setItem(0, editKits);
        p.getInventory().setItem(4, createTeam);
        p.getInventory().setItem(7, unrankedQueue);
        p.getInventory().setItem(8, rankedQueue);
        p.updateInventory();
    }

    public void sendToSpawn(Player p)
    {
        for(Player all : Bukkit.getServer().getOnlinePlayers())
        {
            all.hidePlayer(p);
            p.hidePlayer(all);
        }
        if(!p.getGameMode().equals(GameMode.SURVIVAL)) p.setGameMode(GameMode.SURVIVAL);
        p.setHealth(20.0);
        p.setFoodLevel(20);
        p.setSaturation(20);
        p.setLevel(0);
        p.setExp(0);
        p.setFireTicks(0);
        for(PotionEffect potionEffect : p.getActivePotionEffects()) p.removePotionEffect(potionEffect.getType());
        giveSpawnItems(p);
        teleportToSpawn(p);
    }

    public boolean hasKitEditor()
    {
        if(kitEditor != null) return true;
        else return false;
    }

    public void setKitEditor(Location kitEditor)
    {
        this.kitEditor = kitEditor;
    }

    public Location getKitEditor()
    {
        return kitEditor;
    }
}
