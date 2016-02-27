package uk.adzwoolly.mc.bounty.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import uk.adzwoolly.mc.bounty.BountyManager;

public class AdminBountyCommand implements CommandExecutor{
	
	BountyManager bounties;
	Plugin plugin;
	
	public AdminBountyCommand(Plugin plugin, BountyManager bounties){
		this.plugin = plugin;
		this.bounties = bounties;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
		if(cmd.getName().equalsIgnoreCase("AdminBounty")){
			if(sender.isOp()){
				if(args.length >= 2){
					Player p = Bukkit.getPlayer(args[0]);
					int bounty = Integer.parseInt(args[1]);
					if(p != null){
						if(args.length >= 3){
							bounties.addAdminBounty(p.getUniqueId(), bounty, Boolean.parseBoolean(args[2]));
						} else{
							bounties.addAdminBounty(p.getUniqueId(), bounty, null);
						}
						sender.sendMessage("[Bounty] Bounty sucessfully added.");
					} else{
						sender.sendMessage("[Bounty] Players must be online to add an admin bounty.");
					}
					return true;
				}
			} else{
				sender.sendMessage("[Bounty] You do not have the required permissions to run this command.");
			}
		}
		return false;
	}

}
