package com.magmaguy.supersmashmates;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.broadcastMessage;
import org.bukkit.Effect;
import static org.bukkit.GameMode.CREATIVE;
import org.bukkit.Location;
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
    
    HashMap<Player, Integer> playerScore = new HashMap<Player, Integer>();
    ArrayList<String> dashCooldown = new ArrayList(175);
    HashMap<Player, Boolean> playerGroundTouch = new HashMap<Player, Boolean>();
    HashMap<Player, Integer> playerHP = new HashMap<Player, Integer>();
    
    //Player hit lists
    HashMap<Player, Boolean> playerHit = new HashMap<Player, Boolean>();
    HashMap<Player, Location> playerLocation1 = new HashMap<Player, Location>();
    HashMap<Player, Location> playerLocation2 = new HashMap<Player, Location>();
    HashMap<Player, Boolean> playerHitInitialCooldown = new HashMap<Player, Boolean>();
    HashMap<Player, Boolean> playerHitCooldownConfirmation = new HashMap<Player, Boolean>();
    
    //Scoreboards
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
                
                //Handle deathScore
                playerScore.put(player, 0);
                
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
        
        playerScore.remove(player);
        playerHP.remove(player);
        
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
    public void onHit(EntityDamageByEntityEvent event){
        
        Entity potentialPlayer = event.getEntity();
        
       if (potentialPlayer.getType().equals(PLAYER))
       {
           
          Player victim = (Player) potentialPlayer;
          
          Entity potentialPlayerDamager = event.getDamager();
          
          if (potentialPlayerDamager.getType().equals(PLAYER)){
              
              //increment level
              victim.setLevel(victim.getLevel() + 1);
              
              //knockback handler
              Player victimizer = (Player) potentialPlayerDamager;
              
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
          
          if (playerHit.get(victim) == null || playerHit.get(victim) == false){
              
              playerHit.put(victim, true);
              playerLocation1.put(victim, victim.getLocation());
              
          }
          
       }
        
    }
    
    
    //Dash move
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
                
            }.runTaskLater(this, 100L);
            
        }
        
    }
    
        
    @EventHandler
    public void onMove(PlayerMoveEvent event){
        
        Player player = event.getPlayer();
        
        //Creative bypass
        if(player.getGameMode().equals(CREATIVE)) return;
        
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

                            getLogger().info("Explosion code running");

                            playerHitCooldownConfirmation.put(player, false);
                            playerHitInitialCooldown.put(player, false);
                            playerHit.put(player, false);

                        } else {

                            getLogger().info("Too fast for explosion!");

                        }

                        playerLocation1.put(player, playerLocation2.get(player));
                        
                    }
                    
                }.runTaskLater(this, 3);
                
            }
            
        }
        
        
        //Powerjump
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
        
        int newScore = playerScore.get(player) + 1;
        playerScore.put(player, newScore);
        
        event.setDeathMessage(player.getName() + " has fallen. Deathcount: " + playerScore.get(player));
        
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
