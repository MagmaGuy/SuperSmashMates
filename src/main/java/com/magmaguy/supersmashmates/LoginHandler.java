package com.magmaguy.supersmashmates;

import static com.magmaguy.supersmashmates.SuperSmashMates.deathCounter;
import static com.magmaguy.supersmashmates.SuperSmashMates.desertIsland;
import static com.magmaguy.supersmashmates.SuperSmashMates.killCounter;
import static com.magmaguy.supersmashmates.SuperSmashMates.levelBoard;
import static com.magmaguy.supersmashmates.SuperSmashMates.rockPark;
import static com.magmaguy.supersmashmates.SuperSmashMates.spawn;
import static com.magmaguy.supersmashmates.SuperSmashMates.sunny1000;
import static com.magmaguy.supersmashmates.SuperSmashMates.winner;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.getServer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.ScoreboardManager;
import static com.magmaguy.supersmashmates.SuperSmashMates.ongoingMatchBool;


public class LoginHandler implements Listener{
    
    private SuperSmashMates plugin;
    
    public LoginHandler(SuperSmashMates plugin){
        
        this.plugin = (SuperSmashMates) plugin;
        
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event){
        
        Player player = event.getPlayer();
        Double healthDouble = 6.0;
        
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            
            @Override
            public void run() {
                
                //Place the player
                player.teleport(spawn);
                
                player.setPlayerListName(player.getDisplayName());
                
                //Set the player to the right gamemode ? 
                player.setGameMode(GameMode.ADVENTURE);
                if (ongoingMatchBool == true){
                    
                    if (winner.equals("rockPark"))
                    {
                        
                        player.teleport(rockPark);
                        
                    } else if (winner.equals("sunny1000")) {
                        
                        player.teleport(sunny1000);
                        
                    } else if (winner.equals("desertIsland")) {
                        
                        player.teleport(desertIsland);
                        
                    } else {
                        
                        plugin.getLogger().info("Couldn't find where the match is being held, teleporting player to spawn.");
                        player.teleport(spawn);
                        player.setGameMode(GameMode.ADVENTURE);
                        
                    }
                    
                }
                
                //Handle health
                player.setMaxHealth(healthDouble);
                player.setHealth(healthDouble);
                player.setHealthScale(healthDouble);
                
                player.setAllowFlight(true);
                
                //Handle score
                deathCounter.put(player, 0);
                killCounter.put(player, 0);
                
                //Handle initial XP
                player.setLevel(0);
                
                //Handle initial foodbar
                player.setFoodLevel(20);
                
                //Handle scoreboard
                //Under name scoreboard
                ScoreboardManager manager = Bukkit.getScoreboardManager();
                levelBoard = manager.getNewScoreboard();
                
                Objective objective = levelBoard.registerNewObjective("showlevel","level");
                objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
                objective.setDisplayName(" XP");
                
                for(Player online : Bukkit.getOnlinePlayers()){
                    
                    online.setScoreboard(levelBoard);
                    online.setLevel(online.getLevel());
                    
                }
                
                //Sidebar scoreboard
                
                
            }
            
        }, 1L);

    }
    
}
