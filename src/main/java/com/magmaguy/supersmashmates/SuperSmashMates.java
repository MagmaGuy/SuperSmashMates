package com.magmaguy.supersmashmates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.broadcastMessage;
import org.bukkit.Effect;
import static org.bukkit.GameMode.CREATIVE;
import org.bukkit.entity.Entity;
import static org.bukkit.entity.EntityType.PLAYER;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.BLOCK_EXPLOSION;
import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.FALL;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

//This whole plugin relies on a modded to shit CreeperHeal plugin which does not accept the BlockExplodeEvent
//creepeheal's dev has been contacted about it, check if he's responded later
public class SuperSmashMates extends JavaPlugin implements Listener{
    
    ArrayList<String> playerLives = new ArrayList(175);
    ArrayList<Integer> playerScore = new ArrayList(175);
    ArrayList<String> dashCooldown = new ArrayList(175);
    HashMap<Player, Boolean> playerGroundTouch = new HashMap<Player, Boolean>();
    HashMap<Player, Integer> playerHP = new HashMap<Player, Integer>();
    
    Team team;
    Scoreboard board;
    Scoreboard levelBoard;
    
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
                
                //Handle health
                player.setMaxHealth(healthDouble);
                player.setHealth(healthDouble);
                player.setHealthScale(healthDouble);
                
                player.setAllowFlight(true);
                
                //Handle lives
                playerLives.add(player.getName());
                int playerIndex = playerLives.indexOf(player.getName());
                playerScore.add(playerIndex, 0);
                
                //Handle initial XP
                player.setLevel(0);
                
                //Handle initial foodbar
                player.setFoodLevel(20);
                
                //Handle scoreboard
                ScoreboardManager manager = Bukkit.getScoreboardManager();
                levelBoard = manager.getNewScoreboard();
                
                Objective objective = levelBoard.registerNewObjective("showlevel","level");
                objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
                objective.setDisplayName(" XP");
                
                for(Player online : Bukkit.getOnlinePlayers()){
                    
                    online.setScoreboard(levelBoard);
                    online.setLevel(online.getLevel());
                    
                }
                
            }
        }, 20L);

    }
    
    @EventHandler
    public void PlayerLogOffHandler(PlayerQuitEvent event){
        
        Player player = event.getPlayer();
        
        if (playerLives.contains(player.getName())){
            
            int playerIndex = playerLives.indexOf(player.getName());
            playerLives.remove(playerIndex);
            playerScore.remove(playerIndex);
            
            playerHP.remove(player);
            
        }
        
    }
    
    
    //Eventually this should only be used to cancel damage
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
              
              Vector knockbackDirection = victimizer.getEyeLocation().getDirection();
              //broadcastMessage("Pushback direction: " + knockbackDirection);
              
              double Formula1 = 0.1 * victim.getLevel();
              double Formula2 = 2.0;
              double Formula3 = Math.pow(Formula1, Formula2);
              
              knockbackDirection.multiply(Formula3 * 3);
              victim.setVelocity(knockbackDirection);
              
              //and now for the tricky part
              //explosion handler

              new BukkitRunnable(){
                  
                  @Override
                  public void run() {
                      
                      if(Math.abs(victim.getVelocity().getX()) < 0.000001 &&
                              Math.abs(victim.getVelocity().getZ()) < 0.000001) //victim.isOnGround()
                      {
                          
                          float Formula3Float = (float) Formula3;
                          
                          victim.getWorld().createExplosion(victim.getLocation(), Formula3Float);
                          
                          cancel();
                          
                      }
                  }
                  
              }.runTaskLater(this, 10L);
              
          }
           
       } 
        
    }
    
    
    //Dash move
    @EventHandler
    public void Dash(PlayerToggleSneakEvent event){
        
        Player player = event.getPlayer();
        
        if (player.getGameMode().equals(CREATIVE)) return;
        
        if (!dashCooldown.contains(player.getName()))
        {
            
            Vector eyeVector = player.getEyeLocation().getDirection();
            Vector dashVector = eyeVector.multiply(3);
            player.setVelocity(dashVector);
            
            int playerIndex = playerLives.indexOf(player.getName());
            
            dashCooldown.add(player.getName());
            
            new BukkitRunnable()
            {
                
                @Override
                public void run()
                {
                    
                    dashCooldown.remove(dashCooldown.indexOf(player.getName()));
                    
                }
                
            }.runTaskLater(this, 100L);
            
        }
        
    }
    
        
    @EventHandler
    public void onMove(PlayerMoveEvent event){
        
        Player player = event.getPlayer();
        
        if(player.getGameMode().equals(CREATIVE)) return;
        
        if (player.isOnGround())
        {
            
            playerGroundTouch.put(player, true);
            
        }
        
        if (playerGroundTouch.get(player) == true){

            player.setAllowFlight(true);

        }else{

            player.setAllowFlight(false);

        }
        
        if (playerGroundTouch.get(player) == false)
        {
            
            Bukkit.getOnlinePlayers().stream().forEach((plyr) -> {
                plyr.playEffect(player.getLocation(), Effect.SMOKE, 2004);
            });
            
        }
        
    }
    
    //Powerjump move
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
    
    @EventHandler
    public void ScoreCounter(PlayerDeathEvent event){
        
        Player player = event.getEntity();
        
            if(playerLives.contains(player.getName()))
            {
            int playerIndex = playerLives.indexOf(player.getName());
            int score = playerScore.get(playerIndex);
            int newScore = score + 1;
            playerScore.add(playerIndex, newScore);

            event.setDeathMessage(player.getName() + " has fallen. Deathcount: " + playerScore.get(playerIndex));
            
        }
        
    }
    @EventHandler
    public void LivesLeft (PlayerRespawnEvent event){
        
        Player player = event.getPlayer();
        
        player.setLevel(0);

        if(!playerHP.containsKey(player)){
            
            playerHP.put(player, 2);
            
        }

        if(null != playerHP.get(player))
        {
            
            new BukkitRunnable(){

                @Override
                public void run() {
                    switch (playerHP.get(player)) {
                        case 3:
                            player.setHealth(6.0);
                            playerHP.replace(player, 3, 2);
                            broadcastMessage(player.getName() + " has run out of lives!");
                            break;
                        case 2:
                            player.setHealth(4.0);
                            playerHP.replace(player, 2, 1);
                            break;
                        case 1:
                            player.setHealth(2.0);
                            playerHP.put(player, 3);
                            playerHP.replace(player, 1, 3);
                            break;
                        default:
                            player.setHealth(6.0);
                            playerHP.put(player, 3);
                            break;
                        }
                }
                
            }.runTask(this);
            
        }
        
    }
    
    @EventHandler
    public void FoodDeny(FoodLevelChangeEvent event){
        
        event.setCancelled(true);
        
    }
    
    @EventHandler
    public void HealthDeny(EntityRegainHealthEvent event){
        
        event.setCancelled(true);
        
    }
    
}
