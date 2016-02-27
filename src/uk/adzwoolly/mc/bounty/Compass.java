package uk.adzwoolly.mc.bounty;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Compass extends BukkitRunnable{
	
	private Player hunter;
	private Player target;
	
	public Compass(Player hunter, Player target){
		this.hunter = hunter;
		this.target = target;
	}
	
	@Override
	public void run() {
		hunter.setCompassTarget(target.getLocation());
	}
	
}
