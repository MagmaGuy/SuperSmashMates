package com.magmaguy.supersmashmates;

import static com.magmaguy.supersmashmates.SuperSmashMates.activeMatchPlayersList;
import static com.magmaguy.supersmashmates.SuperSmashMates.livesScoreboard;
import static com.magmaguy.supersmashmates.SuperSmashMates.team;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.getServer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import static com.magmaguy.supersmashmates.SuperSmashMates.currentLives;
import static com.magmaguy.supersmashmates.SuperSmashMates.livesObjective;
import static org.bukkit.Bukkit.getLogger;
import org.bukkit.event.player.PlayerQuitEvent;


public class LivesScoreboardHandler implements Listener{
    
    private SuperSmashMates plugin;
    
    public LivesScoreboardHandler(SuperSmashMates plugin){
        
        this.plugin = (SuperSmashMates) plugin;
        
        livesScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        livesObjective = livesScoreboard.registerNewObjective("Test", "Test2");
        livesObjective.setDisplayName(ChatColor.GREEN + "          Lives Left          ");
        livesObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        
        team = livesScoreboard.registerNewTeam("team");
        
        refresh();
        
    }
    
    public void refresh(){
        
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
            
            @Override
            public void run() {
            
                for(Player players : activeMatchPlayersList)
                {
                    
                    players.setScoreboard(livesScoreboard);
                    livesObjective.getScore(players.getDisplayName()).setScore(currentLives.get(players).byteValue());
                    
                }
            
            }
            
        }, 20L, 20L);
        
    }
    
    
    private void addScore(String player, byte count, String id, boolean exact) {
        for (String entry : livesScoreboard.getEntries()) {
            if ((entry.contains(id) && !exact) || (exact && entry.equals(id))) {
                if (!entry.equals(player)) {
                    // clear old information
                    livesScoreboard.resetScores(entry);
                    // update information
                    livesObjective.getScore(player).setScore(count);
                    getLogger().info("score");
                }
                return;
            }
        }
        // add new information
        livesObjective.getScore(player).setScore(count);
    }
    
    
    public void scoreboard(Player player){
        
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                
                team.addPlayer(player);
                currentLives.put(player, 3.0);
                
                addScore(player.getDisplayName(), currentLives.get(player).byteValue(), player.getDisplayName(), true);
                
            }
            
        }, 1L);
        
    }
    
    
    @EventHandler
    public void onLogin(PlayerLoginEvent event){
        
        Player player = event.getPlayer();
        
        scoreboard(player);
        
    }
    
    
    @EventHandler
    public void onLogout(PlayerQuitEvent event){
        
        Player player = event.getPlayer();
        
        livesScoreboard.resetScores(livesObjective.getScore(player).getPlayer());
        
    }
    
    
}
