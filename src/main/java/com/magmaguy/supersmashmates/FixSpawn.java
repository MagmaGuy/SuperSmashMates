
package com.magmaguy.supersmashmates;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class FixSpawn implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String arg2, String[] arg3) {
        
        if(sender instanceof Player){
            Player player = (Player) sender;
            int intX = player.getLocation().getBlockX();
            int intY = player.getLocation().getBlockY();
            int intZ = player.getLocation().getBlockZ();
            player.getWorld().setSpawnLocation(intX, intY, intZ);
            
        }
        
        return true;
    }
    
}
