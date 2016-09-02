/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magmaguy.supersmashmates;

import static com.magmaguy.supersmashmates.SuperSmashMates.playerHit;
import static com.magmaguy.supersmashmates.SuperSmashMates.playerHitCooldownConfirmation;
import static com.magmaguy.supersmashmates.SuperSmashMates.playerHitInitialCooldown;
import static com.magmaguy.supersmashmates.SuperSmashMates.playerLocation1;
import static com.magmaguy.supersmashmates.SuperSmashMates.playerLocation2;
import org.bukkit.entity.Entity;
import static org.bukkit.entity.EntityType.PLAYER;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author NaN
 */
public class ExplosionHandler implements Listener{
    
    private SuperSmashMates plugin;
    
    public ExplosionHandler(SuperSmashMates plugin){
        
        this.plugin = (SuperSmashMates) plugin;
        
    }
    
    
    @EventHandler
    public void onHit(EntityDamageByEntityEvent event){
        
        Entity potentialPlayer = event.getEntity();
        
       if (potentialPlayer.getType().equals(PLAYER))
       {
           
          Player victim = (Player) potentialPlayer;

          if (playerHit.get(victim) == null || playerHit.get(victim) == false)
          {
              
              playerHit.put(victim, true);
              playerLocation1.put(victim, victim.getLocation());
              
          }
          
       }
       
    }
    
    
    @EventHandler
    public void onMove(PlayerMoveEvent event){
        
        Player player = event.getPlayer();
        
        //ExplosionHandler
        if(playerHit.get(player) != null && playerHit.get(player) == true)
        {
            
            if(playerHitInitialCooldown.get(player) == null || playerHitInitialCooldown.get(player) != true){
                
                playerHitInitialCooldown.put(player, true);
                
                new BukkitRunnable(){
                    
                    @Override
                    public void run() {
                        
                        playerHitInitialCooldown.put(player, false);
                        
                        //check for speed as long as the confirmation is true
                
                        playerLocation2.put(player, player.getLocation());

                        Double location1X = playerLocation1.get(player).getX();
                        Double location1Z = playerLocation1.get(player).getZ();

                        Double location2X = playerLocation2.get(player).getX();
                        Double location2Z = playerLocation2.get(player).getZ();

                        Double distanceX = Math.abs(location2X - location1X);
                        Double distanceZ = Math.abs(location2Z - location1Z);

                        Double totalDistance = Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceZ, 2));

                        if (totalDistance < 0.6) //condition to blow up
                        {

                            float explosionAlgorithm = (float) Math.pow(0.1 * player.getLevel(), 2);
                            player.getWorld().createExplosion(player.getLocation(), explosionAlgorithm);

                            playerHitCooldownConfirmation.put(player, false);
                            playerHitInitialCooldown.put(player, false);
                            playerHit.put(player, false);

                        } else {

                            //getLogger().info("Too fast for explosion!");

                        }

                        playerLocation1.put(player, playerLocation2.get(player));
                        
                    }
                    
                }.runTaskLater(plugin, 3);
                
            }
            
        }
        
    }
    
}
