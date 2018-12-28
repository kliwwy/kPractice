package me.wilk3z.kpractice.vanish;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.wilk3z.kpractice.Practice;
import org.apache.logging.log4j.core.net.Protocol;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class VanishPackets
{
    public Practice plugin;
    public VanishManager vanishManager;

    public VanishPackets(Practice plugin)
    {
        this.plugin = plugin;
        vanishManager = plugin.getVanishManager();
        projectiles();
        itemDrops();
        potionSplashes();
    }

    public void projectiles()
    {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.SPAWN_ENTITY)
        {
            public void onPacketSending(PacketEvent e)
            {
                if(e.getPacket().getType().equals(PacketType.Play.Server.SPAWN_ENTITY))
                {
                    PacketContainer packet = e.getPacket();
                    int type = packet.getIntegers().read(9);
                    if(type == 60 || type == 61 || type == 62 || type == 65 || type == 73 || type == 75 || type == 90)
                    {
                        int id = packet.getIntegers().read(0);
                        Player source = Bukkit.getServer().getPlayer(vanishManager.getSource(id));
                        if(source != null)
                        {
                            Player all = e.getPlayer();
                            if(!all.getUniqueId().equals(source.getUniqueId())) if(!all.canSee(source)) e.setCancelled(true);
                        }
                    }
                }
            }
        });
    }

    public void itemDrops()
    {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.SPAWN_ENTITY)
        {
            public void onPacketSending(PacketEvent e)
            {
                if(e.getPacket().getType().equals(PacketType.Play.Server.SPAWN_ENTITY))
                {
                    PacketContainer packet = e.getPacket();
                    int type = packet.getIntegers().read(9);
                    if(type == 2)
                    {
                        int id = packet.getIntegers().read(0);
                        Player source = Bukkit.getServer().getPlayer(vanishManager.getSource(id));
                        if(source != null)
                        {
                            Player all = e.getPlayer();
                            if(!all.getUniqueId().equals(source.getUniqueId())) if(!all.canSee(source)) e.setCancelled(true);
                        }
                    }
                }
            }
        });
    }

    public void potionSplashes()
    {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.WORLD_EVENT)
        {
            public void onPacketSending(PacketEvent e)
            {
                if(e.getPacket().getType().equals(PacketType.Play.Server.WORLD_EVENT))
                {
                    PacketContainer packet = e.getPacket();
                    int type = packet.getIntegers().read(0);
                    if(type == 2002)
                    {
                        Player all = e.getPlayer();
                        World world = all.getWorld();
                        int x = packet.getIntegers().read(2);
                        int y = packet.getIntegers().read(3);
                        int z = packet.getIntegers().read(4);
                        Location location = new Location(world, x, y, z);
                        if(!vanishManager.hasParticle(all, location)) e.setCancelled(true);
                    }
                }
            }
        });
    }
}
