package com.magmaguy.supersmashmates;

import static org.bukkit.entity.EntityType.PLAYER;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.BLOCK_EXPLOSION;
import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.FALL;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class AntiMinecraftBehaviourPatrol implements Listener{
    
    private SuperSmashMates plugin;
    
    public AntiMinecraftBehaviourPatrol(SuperSmashMates plugin){
        
        this.plugin = (SuperSmashMates) plugin;
        
    }
    
    //prevent food from running out
    @EventHandler
    public void foodDeny(FoodLevelChangeEvent event){
        
        event.setCancelled(true);
        
    }
    
    //prevent health regen
    @EventHandler
    public void healthDeny(EntityRegainHealthEvent event){
        
        event.setCancelled(true);
        
    }
    
    @EventHandler
    public void SuperSmashMates(EntityDamageEvent event){
        
        //explosion damage canceller
        if(event.getCause() == BLOCK_EXPLOSION && event.getEntityType() == PLAYER)
        {
            
            event.setCancelled(true);
            
        }
        
        //fall damage canceller
        if(event.getCause() == FALL && event.getEntityType() == PLAYER)
        {
            
            event.setCancelled(true);
            
        }
        
    }
    
}
