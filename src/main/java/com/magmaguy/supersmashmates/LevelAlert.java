package com.magmaguy.supersmashmates;

import static org.bukkit.Bukkit.broadcastMessage;
import org.bukkit.entity.Entity;
import static org.bukkit.entity.EntityType.PLAYER;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class LevelAlert implements Listener{
    
    private SuperSmashMates plugin;
    
    public LevelAlert(SuperSmashMates plugin){
        
        this.plugin = (SuperSmashMates) plugin;
        
    }
    
    @EventHandler
    public void onHit(EntityDamageByEntityEvent event){
        
        Entity potentialPlayer = event.getEntity();
        
       if (potentialPlayer.getType().equals(PLAYER))
       {
           
          Player victim = (Player) potentialPlayer;
          
          Entity potentialPlayerDamager = event.getDamager();
          
          if (potentialPlayerDamager.getType().equals(PLAYER)){
              
              switch(victim.getLevel())
              {
                  case 10:
                      broadcastMessage(victim.getDisplayName() + " is at level " + victim.getLevel() + "!");
                      break;
                  case 20:
                      broadcastMessage(victim.getDisplayName() + " is at level " + victim.getLevel() + "!");
                      break;
                  case 30:
                      broadcastMessage(victim.getDisplayName() + " is at level " + victim.getLevel() + "!");
                      break;
                  case 40:
                      broadcastMessage(victim.getDisplayName() + " is at level " + victim.getLevel() + "!");
                      break;
                  case 50:
                      broadcastMessage(victim.getDisplayName() + " is at level " + victim.getLevel() + "!");
                      break;
                  case 60:
                      broadcastMessage(victim.getDisplayName() + " is at level " + victim.getLevel() + "!");
                      break;
                  case 70:
                      broadcastMessage(victim.getDisplayName() + " is at level " + victim.getLevel() + "!");
                      break;
                  case 80:
                      broadcastMessage(victim.getDisplayName() + " is showing off at level " + victim.getLevel() + "!");
                      break;
                  case 90:
                      broadcastMessage(victim.getDisplayName() + " is at level " + victim.getLevel() + "! (you can stop now)");
                      break;
                  case 100:
                      broadcastMessage(victim.getDisplayName() + " is at level " + victim.getLevel() + "! (no seriously stop)");
                      break;
                  case 110:
                      broadcastMessage(victim.getDisplayName() + " is at level " + victim.getLevel() + "! (please)");
                      break;
                  case 120:
                      broadcastMessage(victim.getDisplayName() + " is at level " + victim.getLevel() + "! (think of the children)");
                      break;
                  case 150:
                      broadcastMessage(victim.getDisplayName() + " is at level " + victim.getLevel() + "! (fine, be like that)");
                      break;
                  case 200:
                      broadcastMessage(victim.getDisplayName() + " is at level " + victim.getLevel() + "! (you're not supposed to see this)");
                      break;
                  case 250:
                      broadcastMessage(victim.getDisplayName() + " probably just crashed the server at level " + victim.getLevel() + "!");
                      break;
                  case 300:
                      broadcastMessage(victim.getDisplayName() + " is at level " + victim.getLevel() + "! Literally impossible.");
                      break;
                  
              }
              
          }
          
       }
       
    }
    
}
