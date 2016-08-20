package com.magmaguy.supersmashmates;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.broadcastMessage;
import org.bukkit.entity.Entity;
import static org.bukkit.entity.EntityType.PLAYER;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.BLOCK_EXPLOSION;
import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.FALL;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

//This whole plugin relies on a modded to shit CreeperHeal plugin which does not accept the BlockExplodeEvent
//creepeheal's dev has been contacted about it, check if he's responded later
public class SuperSmashMates extends JavaPlugin implements Listener{
    
    ArrayList<String> playerList = new ArrayList(175);
    ArrayList<String> dashCooldown = new ArrayList(175);
    
    //Determine behaviour on startup
    @Override
    public void onEnable(){
        getLogger().info("Super Smash Mates - Enabled!");
        this.getServer().getPluginManager().registerEvents(this, this);
    }
    
    //Determine behaviour on shutdown
    @Override
    public void onDisable(){
        getLogger().info("Super Smash Mates - Disabled!");
    }
    
    @EventHandler
    public void PlayerMaxHealth(PlayerLoginEvent event){
        
        Player player = event.getPlayer();
        Double healthDouble = 6.0;
        
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                
                broadcastMessage("Login test delayed");
                
                //Handle health
                player.setMaxHealth(healthDouble);
                player.setHealth(healthDouble);
                player.setHealthScale(healthDouble);
                broadcastMessage("Health set");
                
                broadcastMessage(player.getName() + " has logged in with " + player.getMaxHealth() + " hp");
                
                //Handle initial XP
                player.setLevel(0);
                
                //Handle initial foodbar
                player.setFoodLevel(20);
                
            }
        }, 20L);

    }
    
    //Eventually this should only be used to cancel damage
    @EventHandler
    public void SuperSmashMates(EntityDamageEvent event){
        
        //explosion damage canceller
        if(event.getCause() == BLOCK_EXPLOSION && event.getEntityType() == PLAYER)
        {
            getLogger().info("Block explosion damage");
            event.setCancelled(true);
        }
        
        //fall damage canceller
        if(event.getCause() == FALL && event.getEntityType() == PLAYER)
        {
            getLogger().info("Fall damage");
            event.setCancelled(true);
        }
        
    }
    
    
    @EventHandler
    public void HitHandler(EntityDamageByEntityEvent event){
        
        Entity potentialPlayer = event.getEntity();
        
       if (potentialPlayer.getType().equals(PLAYER))
       {
           
          Player victim = (Player) potentialPlayer;
          
          Entity potentialPlayerDamager = event.getDamager();
          
          if (potentialPlayerDamager.getType().equals(PLAYER)){
              
              event.setDamage(EntityDamageEvent.DamageModifier.BASE, 0);
            
              //increment level
              int currentLevel = victim.getLevel();
              int newLevel = currentLevel + 1;
              victim.setLevel(newLevel);
              
              //knockback handler
              Player victimizer = (Player) potentialPlayerDamager;
              broadcastMessage(victimizer.getName() + " has hit " + victim.getName());
              
              Vector knockbackDirection = victimizer.getEyeLocation().getDirection();
              //broadcastMessage("Pushback direction: " + knockbackDirection);
              
              double Formula1 = 0.1 * victim.getLevel();
              double Formula2 = 2.0;
              double Formula3 = Math.pow(Formula1, Formula2);
              
              knockbackDirection.multiply(Formula3);
              victim.setVelocity(knockbackDirection);
              
              //and now for the tricky part
              //explosion handler
              boolean loopOver = false;
              BukkitScheduler scheduler = getServer().getScheduler();
              scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
                  
                  @Override
                  public void run(){
                      
                      getLogger().info("looping");
                      
                      if(Math.abs(victim.getVelocity().getX()) < 0.000001 ||
                              Math.abs(victim.getVelocity().getZ()) < 0.000001) //victim.isOnGround()
                      {
                          
                          float Formula3Float = (float) Formula3;
                          
                          victim.getWorld().createExplosion(victim.getLocation(), Formula3Float);
                          
                          broadcastMessage("Explosion quota met");
                          
                          
                          scheduler.cancelAllTasks();
                          
                      }
                      
                  }
                  
              }, 5L, 1L);
              
          }
           
       } 
        
    }
    
    @EventHandler
    public void Dash(PlayerToggleSneakEvent event){
        
        Player player = event.getPlayer();
        
        if (playerList.contains(player.getName()))
        {
            
            Vector eyeVector = player.getEyeLocation().getDirection();
            Vector dashVector = eyeVector.multiply(2);
            player.setVelocity(dashVector);
            
            int playerIndex = playerList.indexOf(player.getName());
            playerList.remove(playerIndex);
            
            dashCooldown.add(player.getName());
            
            
            
        }
        
        else
        {
            
            playerList.add(player.getName());
            
        }
        
    }
    
    
    @EventHandler
    public void ScoreCounter(PlayerDeathEvent event){
        
        Player player = event.getEntity();
        
        if(player.getLastDamageCause().equals(DamageCause.VOID)){
            
            player.setHealth(player.getHealth() - 2.0);
            event.setDeathMessage(player.getName() + " has fallen. " + player.getHealth() + " lives left.");
            
            broadcastMessage(player.getName() + " has died!");
            
        }
        
    }
    
    @EventHandler
    public void FoodDeny(FoodLevelChangeEvent event){
        
        event.setCancelled(true);
        
    }
    
}
