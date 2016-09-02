package com.magmaguy.supersmashmates;

import static com.magmaguy.supersmashmates.SuperSmashMates.dashCooldown;
import static org.bukkit.GameMode.CREATIVE;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class DashMove implements Listener{
    
    private SuperSmashMates plugin;
    
    public DashMove(SuperSmashMates plugin){
        
        this.plugin = (SuperSmashMates) plugin;
        
    }
    
    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event){
        
        Player player = event.getPlayer();
        
        if (player.getGameMode().equals(CREATIVE)) return;
        
        if (!dashCooldown.contains(player.getName()))
        {
            
            Vector eyeVector = player.getEyeLocation().getDirection();
            Vector dashVector = eyeVector.multiply(3);
            player.setVelocity(dashVector);
            
            dashCooldown.add(player.getName());
            
            new BukkitRunnable()
            {
                
                @Override
                public void run()
                {
                    
                    dashCooldown.remove(dashCooldown.indexOf(player.getName()));
                    
                }
                
            }.runTaskLater(plugin, 100L);
            
        }
        
    }
    
}
