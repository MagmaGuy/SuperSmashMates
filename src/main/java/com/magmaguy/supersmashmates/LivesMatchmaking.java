package com.magmaguy.supersmashmates;

import static com.magmaguy.supersmashmates.SuperSmashMates.desertIsland;
import static com.magmaguy.supersmashmates.SuperSmashMates.mapEnum.desertIslandE;
import static com.magmaguy.supersmashmates.SuperSmashMates.mapEnum.rockParkE;
import static com.magmaguy.supersmashmates.SuperSmashMates.mapEnum.sunny1000E;
import static com.magmaguy.supersmashmates.SuperSmashMates.rockPark;
import static com.magmaguy.supersmashmates.SuperSmashMates.spawn;
import static com.magmaguy.supersmashmates.SuperSmashMates.sunny1000;
import static com.magmaguy.supersmashmates.SuperSmashMates.voteAmount;
import static com.magmaguy.supersmashmates.SuperSmashMates.winner;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import static com.magmaguy.supersmashmates.SuperSmashMates.activeMatchPlayersList;
import static com.magmaguy.supersmashmates.SuperSmashMates.rockParkVotes;
import static com.magmaguy.supersmashmates.SuperSmashMates.sunny1000Votes;
import static com.magmaguy.supersmashmates.SuperSmashMates.desertIslandVotes;
import static com.magmaguy.supersmashmates.SuperSmashMates.mapVotes;
import static com.magmaguy.supersmashmates.SuperSmashMates.playerLostHashMap;
import static com.magmaguy.supersmashmates.SuperSmashMates.ongoingMatchBool;

public class LivesMatchmaking implements Listener{
    
    private SuperSmashMates plugin;
    private int secondCounter;
    
    public LivesMatchmaking(SuperSmashMates plugin){
        
        this.plugin = (SuperSmashMates) plugin;
        
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
            
            @Override
            public void run() {
                
                if(ongoingMatchBool == false){
                    
                    Bukkit.broadcastMessage("Please use /vote [mapname] to pick the next map!");
                    
                }
                
            }
            
        }, 1200L, 1200L);
        
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
            
            @Override
            public void run() {
                
                if (ongoingMatchBool == false)
                {
                    
                    if (mapVotes != null)
                    {
                        
                        for (Player player : Bukkit.getOnlinePlayers())
                        {
                            
                            if (mapVotes.get(player) != null)
                            {
                                
                                if (mapVotes.get(player).equals(rockParkE))
                                {

                                    rockParkVotes ++;
                                    voteAmount ++;

                                    plugin.getLogger().info("Current rockPark votes: " + rockParkVotes);

                                }

                                if (mapVotes.get(player).equals(sunny1000E))
                                {

                                    sunny1000Votes ++;
                                    voteAmount ++;

                                    plugin.getLogger().info("Current 1000sunny votes: " + sunny1000Votes);

                                }

                                if (mapVotes.get(player).equals(desertIslandE))
                                {

                                    desertIslandVotes ++;
                                    voteAmount ++;

                                    plugin.getLogger().info("Current desertIsland votes: " + desertIslandVotes);

                                }
                                
                            }
                            
                        }
                        
                    }
                    
                    if (voteAmount >= Bukkit.getOnlinePlayers().size() / 2 && Bukkit.getOnlinePlayers().size() > 1)
                    {
                        if(rockParkVotes > sunny1000Votes && rockParkVotes > desertIslandVotes)
                        {
                            
                            winner = "rockPark";
                            
                        } else if (sunny1000Votes > rockParkVotes && sunny1000Votes > desertIslandVotes) {
                            
                            winner = "sunny1000";
                            
                        } else if (desertIslandVotes > rockParkVotes && desertIslandVotes > sunny1000Votes) {
                            
                            winner = "desertIsland";
                            
                        }
                        
                        ongoingMatchBool = true;
                        match();
                        
                    }
                    
                    voteAmount = 0;
                    rockParkVotes = 0;
                    sunny1000Votes = 0;
                    desertIslandVotes = 0;
                
                }
                
            }
            
        }, 20L, 20L);
        
    }
    
    public void match() {
        
        if (ongoingMatchBool == true)
        {
            
            secondCounter = 0;
            
            for(Player player : Bukkit.getOnlinePlayers())
            {
                
                if (winner.equals("rockPark"))
                {
                    
                    player.teleport(rockPark);
                    
                }
                
                if (winner.equals("sunny1000"))
                {
                    
                    player.teleport(sunny1000);
                    
                }
                
                if (winner.equals("desertIsland"))
                {
                    
                    player.teleport(desertIsland);
                    
                }
                
                playerLostHashMap.put(player, false);
                activeMatchPlayersList.add(player);
                
            }
            
            
            //Second counter, to be used in order to force matches to end
            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
                
                @Override
                public void run() {

                    secondCounter++;
                    
                    if (secondCounter == 299 || ongoingMatchBool == true)
                    {
                        
                        endMatch();
                        
                    }

                }

            }, 6000L, 6000L);
            
            matchStatusChecker();
            
        }
        
    }
    
    
    public void matchStatusChecker(){
        
        {
            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
                
                @Override
                public void run() {
                    
                    if (ongoingMatchBool == true)
                    {

                        for (Player player : Bukkit.getOnlinePlayers())
                        {

                            if (activeMatchPlayersList.contains(player))
                            {
                                
                                if (playerLostHashMap.get(player) == true)
                                {
                                    
                                    playerLostHashMap.remove(player);
                                    Bukkit.broadcastMessage("Player " + activeMatchPlayersList.indexOf(player) + " has died!");
                                    activeMatchPlayersList.remove(activeMatchPlayersList.indexOf(player));

                                }

                                if (activeMatchPlayersList.size() == 1)
                                {

                                    endMatch();

                                }

                            }

                        }
                        
                    }
                    
                }
                
            }, 1L, 1L);
            
            
        }
        
    }
    
    public void endMatch(){
        
        ongoingMatchBool = false;

        String victor = "";
        
        for (Player player : Bukkit.getOnlinePlayers())
        {
            
            if (player != null)
            {
                
                victor = player.getDisplayName();
                
            }
            
        }
        
        Bukkit.broadcastMessage(victor  + " has won this match!");
        
        //wipe the previous vote data
        voteAmount = 0;
        rockParkVotes = 0;
        sunny1000Votes = 0;
        desertIslandVotes = 0;
        mapVotes.clear();
        activeMatchPlayersList.clear();
        playerLostHashMap.clear();

        //move the players back to spawn
        for (Player player : Bukkit.getOnlinePlayers())
        {

            player.teleport(spawn);
            player.setGameMode(GameMode.ADVENTURE);

        }
        
        plugin.getLogger().info("Match ended.");
        
    }
    
    @EventHandler
    public void onQuit (PlayerQuitEvent event){
        
        Player player = event.getPlayer();
        
        if (activeMatchPlayersList != null && activeMatchPlayersList.contains(player))
        {
            
            int index  = activeMatchPlayersList.indexOf(player);
            activeMatchPlayersList.remove(index);
            
            plugin.getLogger().info("Cleared " + player.getDisplayName() + " from the active player list.");
            
            
        }
        
    }
    
}
