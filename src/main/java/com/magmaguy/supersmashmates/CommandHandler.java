package com.magmaguy.supersmashmates;

import static com.magmaguy.supersmashmates.SuperSmashMates.desertIsland;
import static com.magmaguy.supersmashmates.SuperSmashMates.lastArenaLocation;
import static com.magmaguy.supersmashmates.SuperSmashMates.rockPark;
import static com.magmaguy.supersmashmates.SuperSmashMates.spawn;
import static com.magmaguy.supersmashmates.SuperSmashMates.sunny1000;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor{
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if(sender instanceof Player){
            
            Player player = (Player) sender;
            
            if (label.equals("spawn"))
            {
                
                player.teleport(spawn);
                lastArenaLocation.put(player, spawn);
                return true;
                
            }
            
            if (command.getName().equalsIgnoreCase("vote"))
            {
                
                if( args[0] != null )
                {
                    
                    if (args[0].equalsIgnoreCase("rockpark"))
                    {
                        
                        player.teleport(rockPark);
                        lastArenaLocation.put(player, rockPark);
                        return true;
                        
                    }
                    
                    if ( args[0] != null && args[0].equalsIgnoreCase("1000sunny"))
                    {

                        player.teleport(sunny1000);
                        lastArenaLocation.put(player, sunny1000);
                        return true;

                    }
                    
                    if (args[0] != null && args[0].equalsIgnoreCase("desertIsland"))
                    {
                        
                        player.teleport(desertIsland);
                        lastArenaLocation.put(player, desertIsland);
                        return true;
                        
                    }
                    
                }
                
            }
            
            if (command.getName().equalsIgnoreCase("go"))
            {
                if ( args[0] != null && args[0].equalsIgnoreCase("spawn"))
                {
                    
                    player.teleport(spawn);
                    lastArenaLocation.put(player, spawn);
                    return true;
                    
                }
                
                if ( args[0] != null && args[0].equalsIgnoreCase("rockPark"))
                {
                    player.teleport(rockPark);
                    lastArenaLocation.put(player, rockPark);
                    return true;
                    
                }
                
                if ( args[0] != null && args[0].equalsIgnoreCase("1000sunny"))
                {
                    
                    player.teleport(sunny1000);
                    lastArenaLocation.put(player, sunny1000);
                    return true;
                    
                }
                
                if ( args[0].equalsIgnoreCase("desertIsland"))
                {

                    player.teleport(desertIsland);
                    lastArenaLocation.put(player, desertIsland);
                    return true;
                    
                }
                
            }
            
        }
        
        return false;
        
    }
    
}
