package com.magmaguy.supersmashmates;

import static com.magmaguy.supersmashmates.SuperSmashMates.lastArenaLocation;
import static com.magmaguy.supersmashmates.SuperSmashMates.playerHP;
import java.util.logging.Level;
import static org.bukkit.Bukkit.broadcastMessage;
import static org.bukkit.Bukkit.getLogger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class RespawnHandler implements Listener{
    
    private SuperSmashMates plugin;
    
    public RespawnHandler(SuperSmashMates plugin){
        
        this.plugin = (SuperSmashMates) plugin;
        
    }
    
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event){
        
        Player player = event.getPlayer();
        
        player.setLevel(0);
        
        new BukkitRunnable(){
            @Override
            public void run() {
                if(lastArenaLocation.get(player) != null)
                {

                    player.teleport(lastArenaLocation.get(player));
                    getLogger().log(Level.INFO, "Teleporting {0} to {1}", new Object[]{player.getName(), lastArenaLocation.get(player)});
                    
                } else {

                    getLogger().log(Level.INFO, "Failed to find a previous location for {0}, respawning at the spawn location.", player.getName());

                }
            }
            
        }.runTask(plugin);

        if(!playerHP.containsKey(player)){
            
            playerHP.put(player, 2);
            
        }

        if(null != playerHP.get(player))
        {
            
            new BukkitRunnable(){

                @Override
                public void run() {
                    
                    switch (playerHP.get(player)) {
                        
                        case 3:
                            player.setHealth(6.0);
                            playerHP.replace(player, 3, 2);
                            broadcastMessage(player.getName() + " has run out of lives!");
                            break;
                        case 2:
                            player.setHealth(4.0);
                            playerHP.replace(player, 2, 1);
                            break;
                        case 1:
                            player.setHealth(2.0);
                            playerHP.put(player, 3);
                            playerHP.replace(player, 1, 3);
                            break;
                        default:
                            player.setHealth(6.0);
                            playerHP.put(player, 3);
                            break;
                            
                        }
                }
                
            }.runTask(plugin);
            
        }
        
    }
    
}
