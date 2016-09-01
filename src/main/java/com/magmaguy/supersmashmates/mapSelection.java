package com.magmaguy.supersmashmates;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class mapSelection implements CommandExecutor {
    
    public HashMap <Player, Location> lastArena = new HashMap<>();
    
    public Location spawn = new Location(Bukkit.getWorld("world"), 1002.0, 88.0, 25.0, -90, -13);
    public Location rockPark = new Location(Bukkit.getWorld("world"), 8.0, 45.0, -1.0);
    public Location sunny1000 = new Location(Bukkit.getWorld("world"), 473.0, 153.0, 35.0);
    public Location desertIsland = new Location(Bukkit.getWorld("world"), 361.0, 85.0, -316.0);
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if(sender instanceof Player){
            
            Player player = (Player) sender;
            
            if (label.equals("spawn"))
            {
                
                player.teleport(spawn);
                lastArena.put(player, spawn);
                return true;
                
            }
            
            if (command.getName().equalsIgnoreCase("vote"))
            {
                
                if( args[0] != null )
                {
                    
                    if (args[0].equalsIgnoreCase("rockpark"))
                    {
                        
                        player.teleport(rockPark);
                        lastArena.put(player, spawn);
                        return true;
                        
                    }
                    
                    if ( args[0] != null && args[0].equalsIgnoreCase("1000sunny"))
                    {

                        player.teleport(sunny1000);
                        lastArena.put(player, sunny1000);
                        return true;

                    }
                    
                    if (args[0] != null && args[0].equalsIgnoreCase("desertIsland"))
                    {
                        
                        player.teleport(desertIsland);
                        lastArena.put(player, desertIsland);
                        return true;
                        
                    }
                    
                }
                
            }
            
            if (command.getName().equalsIgnoreCase("go"))
            {
                if ( args[0] != null && args[0].equalsIgnoreCase("spawn"))
                {
                    
                    player.teleport(spawn);
                    lastArena.put(player, spawn);
                    return true;
                    
                }
                
                if ( args[0] != null && args[0].equalsIgnoreCase("rockPark"))
                {
                    player.teleport(rockPark);
                    lastArena.put(player, rockPark);
                    return true;
                    
                }
                
                if ( args[0] != null && args[0].equalsIgnoreCase("1000sunny"))
                {
                    
                        player.teleport(sunny1000);
                        lastArena.put(player, sunny1000);
                        return true;
                    
                }
                
                if ( args[0].equalsIgnoreCase("desertIsland"))
                {

                    player.teleport(desertIsland);
                    lastArena.put(player, desertIsland);
                    return true;
                    
                }
                
            }
            
        }
        
        return false;
    }
    
    public Location lastArenaLocation (Player player){
        
        return lastArena.get(player);
        
    }
    
}
