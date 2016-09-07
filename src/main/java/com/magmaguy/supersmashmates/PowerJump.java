package com.magmaguy.supersmashmates;

import static com.magmaguy.supersmashmates.SuperSmashMates.playerGroundTouch;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import static org.bukkit.GameMode.CREATIVE;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class PowerJump implements Listener{
    
    private SuperSmashMates plugin;
    
    public PowerJump(SuperSmashMates plugin){
        
        this.plugin = (SuperSmashMates) plugin;
        
    }
    
    @EventHandler
    public void onMove(PlayerMoveEvent event){
        
        Player player = event.getPlayer();

        
        if (player.isOnGround())
        {
            
            playerGroundTouch.put(player, true);
            
        }
        
        if (playerGroundTouch.get(player) != null &&playerGroundTouch.get(player) == true){

            player.setAllowFlight(true);

        }else{

            player.setAllowFlight(false);

        }
        
        if (playerGroundTouch.get(player) != null && playerGroundTouch.get(player) == false)
        {
            
            Bukkit.getOnlinePlayers().stream().forEach((plyr) -> {
                plyr.playEffect(player.getLocation(), Effect.SMOKE, 2004);
            });
            
        }
        
    }

    
    @EventHandler
    public void PowerJump(PlayerToggleFlightEvent event){
        
        Player player = event.getPlayer();
        
        if(player.getGameMode().equals(CREATIVE)) return;
        
        if(playerGroundTouch.get(player) == true)
        {
            
            event.setCancelled(true);
            playerGroundTouch.put(player, Boolean.FALSE);

            player.setVelocity(player.getLocation().getDirection().multiply(1.6D).setY(2.0D));

            player.setAllowFlight(false);
            
            
        }
        
    }
    
}
