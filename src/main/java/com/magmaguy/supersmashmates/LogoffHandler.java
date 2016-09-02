/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magmaguy.supersmashmates;

import static com.magmaguy.supersmashmates.SuperSmashMates.deathCounter;
import static com.magmaguy.supersmashmates.SuperSmashMates.killCounter;
import static com.magmaguy.supersmashmates.SuperSmashMates.playerHP;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author NaN
 */
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
