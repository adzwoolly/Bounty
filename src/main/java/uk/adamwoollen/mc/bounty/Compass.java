package uk.adamwoollen.mc.bounty;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Compass extends BukkitRunnable{
	
	private BountyManager bounties;
	private Player hunter;
	private Player target;
	
	public Compass(Player hunter, Player target, BountyManager bounties){
		this.bounties = bounties;
		this.hunter = hunter;
		this.target = target;
	}
	
	@Override
	public void run() {
		if(bounties.getBounty(target.getUniqueId()) != 0){
			if(target.isOnline()){
				hunter.setCompassTarget(target.getLocation());
				return;
			} else{
				hunter.sendMessage(Bounty.msgPrefix + "The player you were tracking is no longer online.");
			}
		} else{
			hunter.sendMessage(Bounty.msgPrefix + "The player you were tracking no longer has a bounty.");
		}
		
		Location newTarget = hunter.getBedSpawnLocation();
		if(newTarget == null){
			newTarget = hunter.getWorld().getSpawnLocation();
		}
		hunter.setCompassTarget(newTarget);
		this.cancel();
	}
	
}
