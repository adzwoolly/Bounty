package uk.adzwoolly.mc.bounty.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import uk.adzwoolly.mc.bounty.Bounty;
import uk.adzwoolly.mc.bounty.BountyManager;

public class RMBountyCommand implements CommandExecutor{
	
	BountyManager bounties;
	Plugin plugin;
	
	public RMBountyCommand(Plugin plugin, BountyManager bounties){
		this.plugin = plugin;
		this.bounties = bounties;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
		if(cmd.getName().equalsIgnoreCase("rmBounty")){
			if(sender.isOp()){
				if(args.length >= 1){
					Player p = Bukkit.getPlayer(args[0]);
					if(p != null){
						if(bounties.removeBounty(p.getUniqueId())){
							sender.sendMessage(Bounty.msgPrefix + "Bounty successfully removed.");
						} else{
							sender.sendMessage(Bounty.msgPrefix + "Did not remove a bounty. Does the player have a bounty?");
						}
					} else{
						sender.sendMessage(Bounty.msgPrefix + "Did not remove a bounty. Did you type the name correctly?");
					}
				} else{
					return false;
				}
			} else{
				sender.sendMessage(Bounty.msgPrefix + "You do not have the required permissions to run this command.");
			}
			return true;
		}
		return false;
	}

}
