package com.magmaguy.supersmashmates;

import static com.magmaguy.supersmashmates.SuperSmashMates.deathCounter;
import static com.magmaguy.supersmashmates.SuperSmashMates.killCounter;
import static com.magmaguy.supersmashmates.SuperSmashMates.playerHP;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LogoffHandler implements Listener{
    
    private SuperSmashMates plugin;
    
    public LogoffHandler(SuperSmashMates plugin){
        
        this.plugin = (SuperSmashMates) plugin;
        
    }
    
    @EventHandler
    public void PlayerLogOffHandler(PlayerQuitEvent event){
        
        Player player = event.getPlayer();
        
        deathCounter.remove(player);
        playerHP.remove(player);
        killCounter.remove(player);
        
    }
    
}
