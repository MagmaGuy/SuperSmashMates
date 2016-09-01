package com.magmaguy.supersmashmates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import net.md_5.bungee.api.ChatColor;
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
    
    Location spawnLocationHack = new Location(Bukkit.getWorld("world"), 1002.0, 88.0, 25.0, -90, -13);
    
    HashMap<Player, Integer> deathCounter = new HashMap<>();
    ArrayList<String> dashCooldown = new ArrayList(175);
    HashMap<Player, Boolean> playerGroundTouch = new HashMap<>();
    HashMap<Player, Integer> playerHP = new HashMap<>();
    
    //Player hit lists
    HashMap<Player, Boolean> playerHit = new HashMap<>();
    HashMap<Player, Location> playerLocation1 = new HashMap<>();
    HashMap<Player, Location> playerLocation2;
    HashMap<Player, Boolean> playerHitInitialCooldown = new HashMap<>();
    HashMap<Player, Boolean> playerHitCooldownConfirmation = new HashMap<>();
    
    //Scoreboards
    Team team;
    Scoreboard board;
    Scoreboard levelBoard;
    
    //killCounter
    HashMap<Player, Player> whoHitWho = new HashMap<>();
    HashMap<Player, Integer> killCounter = new HashMap<>();

    //testing
    private mapSelection mapSelectionHack;
    
    public SuperSmashMates() {
        this.playerLocation2 = new HashMap<>();
    }
    
    //Determine behaviour on startup
    @Override
    public void onEnable(){
        
        getLogger().info("Super Smash Mates - Enabled!");        
        
                if(Bukkit.getServer().getOnlinePlayers() != null)
        {
            
            getLogger().info("Sounds like a reload. Applying reload fixer.");
            
            for (Player player : Bukkit.getServer().getOnlinePlayers()){

                deathCounter.put(player, 0);
                killCounter.put(player, 0);

            }
            
        }
                
        mapSelectionHack = new mapSelection();
        
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("fixspawn").setExecutor(new FixSpawn());
        this.getCommand("spawn").setExecutor(new mapSelection());
        this.getCommand("vote").setExecutor(new mapSelection());
        this.getCommand("go").setExecutor(new mapSelection());
        
    }
    
    
    //Determine behaviour on shutdown
    @Override
    public void onDisable(){
        
        getLogger().info("Super Smash Mates - Disabled!");
        
    }
    
    
    @EventHandler
    public void playerJoin(PlayerLoginEvent event){
        
        Player player = event.getPlayer();
        Double healthDouble = 6.0;
        
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            
            @Override
            public void run() {
                
                //Place the player
                player.teleport(new Location(Bukkit.getWorld("world"), 1002.0, 88.0, 25.0, -90, -13));
                
                player.setPlayerListName(player.getDisplayName());
                
                //Handle health
                player.setMaxHealth(healthDouble);
                player.setHealth(healthDouble);
                player.setHealthScale(healthDouble);
                
                player.setAllowFlight(true);
                
                //Handle score
                deathCounter.put(player, 0);
                killCounter.put(player, 0);
                
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
                
                //login location fixed
                player.teleport(player.getWorld().getSpawnLocation());
                
            }
            
        }, 20L);

    }
    
    
    @EventHandler
    public void PlayerLogOffHandler(PlayerQuitEvent event){
        
        Player player = event.getPlayer();
        
        deathCounter.remove(player);
        playerHP.remove(player);
        killCounter.remove(player);
        
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
          Player victimizer = (Player) potentialPlayerDamager;
          
          if (potentialPlayerDamager.getType().equals(PLAYER)){
              
              //increment level
              victim.setLevel(victim.getLevel() + 1);
              
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
                      broadcastMessage(victim.getDisplayName() + " is at level " + victim.getLevel() + "! (you can stop now");
                      break;
                  case 100:
                      broadcastMessage(victim.getDisplayName() + " is at level " + victim.getLevel() + "! (no seriously stop)");
                      break;
                  case 110:
                      broadcastMessage(victim.getDisplayName() + " is at level " + victim.getLevel() + "! (please)");
                      break;
                  case 120:
                      broadcastMessage(victim.getDisplayName() + " is at level " + victim.getLevel() + "! (think of the children");
                      break;
                  case 150:
                      broadcastMessage(victim.getDisplayName() + " is at level " + victim.getLevel() + "! (fine, be like that");
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
          
          if (playerHit.get(victim) == null || playerHit.get(victim) == false){
              
              playerHit.put(victim, true);
              playerLocation1.put(victim, victim.getLocation());
              
          }
          
          //killCounter
          whoHitWho.put(victim, victimizer);
          
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

                            playerHitCooldownConfirmation.put(player, false);
                            playerHitInitialCooldown.put(player, false);
                            playerHit.put(player, false);

                        } else {

                            //getLogger().info("Too fast for explosion!");

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
    
    
    @EventHandler
    public void livesLeft (PlayerRespawnEvent event){
        
        Player player = event.getPlayer();
        
        player.setLevel(0);
        player.teleport(player.getWorld().getSpawnLocation());
        
        if(mapSelectionHack.lastArenaLocation(player) != null)
        {
            
            player.teleport(mapSelectionHack.lastArenaLocation(player));
            getLogger().log(Level.INFO, "Teleporting {0} to {1}", new Object[]{player.getName(), mapSelectionHack.lastArenaLocation(player)});
            
        } else {
            
            getLogger().log(Level.INFO, "Failed to find a previous location for {0}, respawning at the spawn location.", player.getName());
            
        }

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
    public void foodDeny(FoodLevelChangeEvent event){
        
        event.setCancelled(true);
        
    }
    
    
    @EventHandler
    public void healthDeny(EntityRegainHealthEvent event){
        
        event.setCancelled(true);
        
    }
    
}
