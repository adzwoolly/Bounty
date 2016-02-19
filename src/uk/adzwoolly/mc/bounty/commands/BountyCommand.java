package uk.adzwoolly.mc.bounty.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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
	public boolean onCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
		if(cmd.getName().equalsIgnoreCase("bounty")){
			if(args.length == 0){
					sender.sendMessage("------------------");
					if(sender instanceof Player){
						int senderBounty = bounties.getBounty(((Player) sender).getUniqueId());
						sender.sendMessage("Your bounty: £" + senderBounty);
					}
					if(bounties.hasBounties()){
						sender.sendMessage("\nBounties:");
						sender.sendMessage(bounties.listBounties());
					} else{
						sender.sendMessage("There are no bounties right now");
					}
					sender.sendMessage("------------------");
					return true;
				
			} else if(args.length == 1){
				Player hunter = (Player) sender;
				Player target = Bukkit.getPlayer(args[0]);
				if(target != null){
					int targetBounty = bounties.getBounty(target.getUniqueId());
					if(targetBounty != 0){
						hunter.sendMessage(target.getName() + " has a £" + targetBounty + " on them!");
						
						if(sender instanceof Player){
							if(target.isOnline()){
								if(hunter.getInventory().contains(Material.COMPASS)){
									hunter.setCompassTarget(target.getLocation());
									hunter.sendMessage("[Bounty] Your compass now points to " + target.getName());
									return true;
								}
							} else{
								hunter.sendMessage("[Bounty] This player currently isn't online.");
							}
						} else{
							sender.sendMessage("[Bounty] Running this command as a player points a compass to the target, too");
						}
					} else{
						hunter.sendMessage("[Bounty] This player doesn't have a bounty on them.");
					}
				} else{
					hunter.sendMessage("[Bounty] This player currently isn't online.");
				}
			}
		}
		return false;
	}
	
}
