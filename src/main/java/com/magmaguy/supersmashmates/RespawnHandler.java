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
import static com.magmaguy.supersmashmates.SuperSmashMates.currentLives;
import static com.magmaguy.supersmashmates.SuperSmashMates.desertIsland;
import static com.magmaguy.supersmashmates.SuperSmashMates.rockPark;
import static com.magmaguy.supersmashmates.SuperSmashMates.spawn;
import static com.magmaguy.supersmashmates.SuperSmashMates.sunny1000;
import static com.magmaguy.supersmashmates.SuperSmashMates.winner;
import org.bukkit.GameMode;
import static com.magmaguy.supersmashmates.SuperSmashMates.playerLostHashMap;
import static com.magmaguy.supersmashmates.SuperSmashMates.ongoingMatchBool;

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
                
                } else if (ongoingMatchBool == true){
                    
                    if (winner.equals("rockPark"))
                    {
                        
                        player.teleport(rockPark);
                        
                    } else if (winner.equals("sunny1000")) {
                        
                        player.teleport(sunny1000);
                        
                    } else if (winner.equals("desertIsland")) {
                        
                        player.teleport(desertIsland);
                        
                    }
                    
                } else {

                    player.teleport(spawn);
                    getLogger().log(Level.INFO, "Failed to find a previous location for {0}, respawning at the spawn location.", player.getName());

                }
            }
            
        }.runTask(plugin);

        if(!playerHP.containsKey(player)){
            
            playerHP.put(player, 2);
            
        }

        if(playerHP.get(player) != null)
        {
            
            new BukkitRunnable(){

                @Override
                public void run() {
                    
                    switch (playerHP.get(player)) {
                        
                        case 3:
                            player.setHealth(6.0);
                            playerHP.replace(player, 3, 2);
                            broadcastMessage(player.getName() + " has run out of lives!");
                            currentLives.put(player, 3.0);
                            
                            if (ongoingMatchBool == true)
                            {
                                
                                player.setGameMode(GameMode.SPECTATOR);
                                playerLostHashMap.put(player, true);
                                
                            }
                            break;
                        case 2:
                            player.setHealth(4.0);
                            playerHP.replace(player, 2, 1);
                            currentLives.put(player, 2.0);
                            getLogger().info("current lives: " + currentLives.get(player));
                            break;
                        case 1:
                            player.setHealth(2.0);
                            playerHP.put(player, 3);
                            playerHP.replace(player, 1, 3);
                            currentLives.put(player, 1.0);
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
