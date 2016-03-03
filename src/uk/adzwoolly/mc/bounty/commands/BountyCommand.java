package uk.adzwoolly.mc.bounty.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import uk.adzwoolly.mc.bounty.Bounty;
import uk.adzwoolly.mc.bounty.BountyManager;
import uk.adzwoolly.mc.bounty.Compass;

public class BountyCommand implements CommandExecutor{
	
	BountyManager bounties;
	Plugin plugin;
	
	public BountyCommand(Plugin plugin, BountyManager bounties){
		this.plugin = plugin;
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
					sender.sendMessage(bounties.listBountiesAsString());
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
						hunter.sendMessage(Bounty.msgPrefix + target.getName() + " has a £" + targetBounty + " on them!");
						
						if(sender instanceof Player){
							if(target.isOnline()){
								if(hunter.getInventory().contains(Material.COMPASS)){
									@SuppressWarnings("unused")
									BukkitTask task =  new Compass(hunter, target).runTaskTimer(plugin, 0, 20*60);
									
									hunter.sendMessage(Bounty.msgPrefix + "Your compass now points to " + target.getName());
									hunter.sendMessage(Bounty.msgPrefix + "It will update every minute");
									return true;
								} else{
									hunter.sendMessage(Bounty.msgPrefix + "If run this command with a compass it will point to " + target.getName());
								}
							} else{
								hunter.sendMessage(Bounty.msgPrefix + "This player currently isn't online.");
							}
						} else{
							sender.sendMessage(Bounty.msgPrefix + "Running this command as a player points a compass to the target, too");
						}
					} else{
						hunter.sendMessage(Bounty.msgPrefix + "This player doesn't have a bounty on them.");
					}
				} else{
					hunter.sendMessage(Bounty.msgPrefix + "Player could not be found.");
				}
				
			}
			return true;
		}
		return false;
	}
	
}
