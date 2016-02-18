package uk.adzwoolly.mc.bounty.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.adzwoolly.mc.bounty.BountyManager;

//import static uk.adzwoolly.mc.bounty.Bounty.economy;;

public class BountyCommand implements CommandExecutor{
	
	BountyManager bounties;
	
	public BountyCommand(BountyManager bounties){
		this.bounties = bounties;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String arg2, String[] arg3) {
		if(command.getName().equalsIgnoreCase("bounty")){
			if(sender instanceof Player){
				sender.sendMessage("------------------");
				int senderBounty = bounties.getBounty(((Player) sender).getUniqueId());
				//if(senderBounty != 0){
					sender.sendMessage("Your bounty: £" + senderBounty);
				//}
				if(bounties.hasBounties()){
					sender.sendMessage("\nBounties:");
					sender.sendMessage(bounties.listBounties());
				} else{
					sender.sendMessage("There are no bounties right now");
				}
				sender.sendMessage("------------------");
				return true;
			}
		}
		return false;
	}
	
}
