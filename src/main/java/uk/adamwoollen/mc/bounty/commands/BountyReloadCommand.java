package uk.adamwoollen.mc.bounty.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import uk.adamwoollen.mc.bounty.Bounty;

public class BountyReloadCommand implements CommandExecutor{
	
	Plugin plugin;

	public BountyReloadCommand(Plugin bounty) {
		this.plugin = bounty;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if(sender.isOp()){
			plugin.reloadConfig();
			sender.sendMessage(Bounty.msgPrefix + "Config reloaded.");
		} else{
			sender.sendMessage(Bounty.msgPrefix + Bounty.msgNoPermission);
		}
		return true;
	}

}
