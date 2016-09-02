package com.magmaguy.supersmashmates;

import static com.magmaguy.supersmashmates.SuperSmashMates.deathCounter;
import static com.magmaguy.supersmashmates.SuperSmashMates.killCounter;
import static com.magmaguy.supersmashmates.SuperSmashMates.levelBoard;
import static com.magmaguy.supersmashmates.SuperSmashMates.spawn;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.ScoreboardManager;


public class LoginHandler implements Listener{
    
    private SuperSmashMates plugin;
    
    public LoginHandler(SuperSmashMates plugin){
        
        this.plugin = (SuperSmashMates) plugin;
        getLogger().info("test");
        
    }

    @EventHandler
    public void playerJoin(PlayerLoginEvent event){
        
        Player player = event.getPlayer();
        Double healthDouble = 6.0;
        
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            
            @Override
            public void run() {
                
                //Place the player
                player.teleport(new Location(Bukkit.getWorld("world"), 1002.0, 88.0, 25.0, -90, -13));
                
                player.setPlayerListName(player.getDisplayName());
                
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
                ScoreboardManager manager = Bukkit.getScoreboardManager();
                levelBoard = manager.getNewScoreboard();
                
                Objective objective = levelBoard.registerNewObjective("showlevel","level");
                objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
                objective.setDisplayName(" XP");
                
                for(Player online : Bukkit.getOnlinePlayers()){
                    
                    online.setScoreboard(levelBoard);
                    online.setLevel(online.getLevel());
                    
                }
                
                //login location fixed
                player.teleport(spawn);
                
            }
            
        }, 1L);

    }
    
}
