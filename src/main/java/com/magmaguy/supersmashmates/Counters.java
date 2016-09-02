/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magmaguy.supersmashmates;

import static com.magmaguy.supersmashmates.SuperSmashMates.deathCounter;
import static com.magmaguy.supersmashmates.SuperSmashMates.killCounter;
import static com.magmaguy.supersmashmates.SuperSmashMates.whoHitWho;
import net.md_5.bungee.api.ChatColor;
import static org.bukkit.Bukkit.broadcastMessage;
import org.bukkit.entity.Entity;
import static org.bukkit.entity.EntityType.PLAYER;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 *
 * @author NaN
 */
public class Counters implements Listener{
    
    private SuperSmashMates plugin;
    
    public Counters(SuperSmashMates plugin){
        
        this.plugin = (SuperSmashMates) plugin;
        
    }
    
    @EventHandler
    public void onHit(EntityDamageByEntityEvent event){
        
        Entity potentialPlayer = event.getEntity();
        
       if (potentialPlayer.getType().equals(PLAYER))
       {
           
          Player victim = (Player) potentialPlayer;
          
          Entity potentialPlayerDamager = event.getDamager();
          Player victimizer = (Player) potentialPlayerDamager;
          
          //killCounter
          whoHitWho.put(victim, victimizer);
          
       }
        
    }
    
    
    @EventHandler
    public void scoreCounter(PlayerDeathEvent event){
        
        Player player = event.getEntity();
        
        if (deathCounter.get(player) != null)
        {
                int newScore = deathCounter.get(player) + 1;
            deathCounter.put(player, newScore);

            event.setDeathMessage(player.getName() + " has fallen. Deathcount: " + deathCounter.get(player));

        }
        
        if(whoHitWho.get(player) != null && killCounter != null)
        {
            
            killCounter.put(whoHitWho.get(player), killCounter.get(whoHitWho.get(player)) + 1);
            
        }
        
        if (whoHitWho.get(player) == null)
        {
            
            broadcastMessage(player.getDisplayName() + " has defeated his true enemy - himself. A round of applause for falling on his own.");
            
        } else {
            
            broadcastMessage("" + ChatColor.DARK_PURPLE + ChatColor.BOLD + ChatColor.STRIKETHROUGH +"+---------------------------------+");
            broadcastMessage("  " + whoHitWho.get(player).getDisplayName() + ChatColor.GOLD +" killed " + player.getDisplayName() + ChatColor.GOLD + " and now has "+ ChatColor.YELLOW + ChatColor.BOLD+ killCounter.get(whoHitWho.get(player)) + ChatColor.GOLD + " kills!");
            broadcastMessage("" + ChatColor.DARK_PURPLE + ChatColor.BOLD + ChatColor.STRIKETHROUGH +"+---------------------------------+");
            
        }
        
        whoHitWho.put(player, null);
        
    }
    
}
