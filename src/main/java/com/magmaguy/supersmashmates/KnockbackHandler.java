/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magmaguy.supersmashmates;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import static org.bukkit.entity.EntityType.PLAYER;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

/**
 *
 * @author NaN
 */
public class KnockbackHandler implements Listener{
    
    private SuperSmashMates plugin;
    
    public KnockbackHandler(SuperSmashMates plugin){
        
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
          
          if (potentialPlayerDamager.getType().equals(PLAYER)){
              
              //increment level
              victim.setLevel(victim.getLevel() + 1);
              
              //knockback handler
              Location victimizerLocation = victimizer.getLocation();
              Location victimLocation = victim.getLocation();
              
              Double lengthX = victimLocation.getX() - victimizerLocation.getX();
              Double lengthY = victimLocation.getY() - victimizerLocation.getY();
              Double lengthZ = victimLocation.getZ() - victimizerLocation.getZ();
              
              Double magnitude = Math.sqrt(Math.pow(lengthX, 2) + Math.pow(lengthY, 2) + Math.pow(lengthZ, 2));
              
              Double normalizedX = lengthX / Math.abs(magnitude);
              Double normalizedY = lengthY / Math.abs(magnitude);
              Double normalizedZ = lengthZ / Math.abs(magnitude);
              
              Vector normalizedVector = new Vector(normalizedX, normalizedY, normalizedZ);
              double algorithm = Math.pow(0.1 * victim.getLevel(), 2);
              
              victim.setVelocity(normalizedVector.multiply(algorithm));
              
              event.setDamage(EntityDamageEvent.DamageModifier.BASE, 0);
              
          }
          
       }
       
    }
    
}
