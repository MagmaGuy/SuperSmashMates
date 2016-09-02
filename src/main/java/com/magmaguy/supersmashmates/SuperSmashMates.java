package com.magmaguy.supersmashmates;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

//This whole plugin relies on a modded to shit CreeperHeal plugin which does not accept the BlockExplodeEvent
//creepeheal's dev has been contacted about it, check if he's responded later
public class SuperSmashMates extends JavaPlugin implements Listener{
    
    public static HashMap<Player, Integer> playerHP = new HashMap<>();
    
    //These handle game mechanics i.e. register different things about players
    //getting hit
    public static HashMap<Player, Boolean> playerHit = new HashMap<>();
    public static HashMap<Player, Location> playerLocation1 = new HashMap<>();
    public static HashMap<Player, Location> playerLocation2 = new HashMap<>();
    public static HashMap<Player, Boolean> playerHitInitialCooldown = new HashMap<>();
    public static HashMap<Player, Boolean> playerHitCooldownConfirmation = new HashMap<>();
    
    //Scoreboards
    Team team;
    Scoreboard board;
    public static Scoreboard levelBoard;
    
    //Counters
    public static HashMap<Player, Player> whoHitWho = new HashMap<>();
    public static HashMap<Player, Integer> killCounter = new HashMap<>();
    public static HashMap<Player, Integer> deathCounter = new HashMap<>();

    //Maps
    public static Location spawn = new Location(Bukkit.getWorld("world"), 1002.0, 88.0, 25.0, -90, -13);
    public static Location rockPark = new Location(Bukkit.getWorld("world"), 8.0, 45.0, -1.0);
    public static Location sunny1000 = new Location(Bukkit.getWorld("world"), 473.0, 153.0, 35.0);
    public static Location desertIsland = new Location(Bukkit.getWorld("world"), 361.0, 85.0, -316.0);
    
    public static HashMap<Player, Location> lastArenaLocation = new HashMap<>();
    
    //Power Moves
    public static ArrayList<String> dashCooldown = new ArrayList(175);
    public static HashMap<Player, Boolean> playerGroundTouch = new HashMap<>();
    
    
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
        
        
        //Register events in other classes
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getPluginManager().registerEvents(new LoginHandler(this), this);
        this.getServer().getPluginManager().registerEvents(new LogoffHandler(this), this);
        this.getServer().getPluginManager().registerEvents(new AntiMinecraftBehaviourPatrol(this), this);
        this.getServer().getPluginManager().registerEvents(new LevelAlert(this), this);
        this.getServer().getPluginManager().registerEvents(new KnockbackHandler(this), this);
        this.getServer().getPluginManager().registerEvents(new ExplosionHandler(this), this);
        this.getServer().getPluginManager().registerEvents(new PowerJump(this), this);
        this.getServer().getPluginManager().registerEvents(new DashMove(this), this);
        this.getServer().getPluginManager().registerEvents(new Counters(this), this);
        this.getServer().getPluginManager().registerEvents(new RespawnHandler(this), this);
        
        
        //Register commands in other classes
        this.getCommand("fixspawn").setExecutor(new FixSpawn());
        this.getCommand("spawn").setExecutor(new CommandHandler());
        this.getCommand("vote").setExecutor(new CommandHandler());
        this.getCommand("go").setExecutor(new CommandHandler());

    }
    
    
    //Determine behaviour on shutdown
    @Override
    public void onDisable(){
        
        getLogger().info("Super Smash Mates - Disabled!");
        
    }
    
}