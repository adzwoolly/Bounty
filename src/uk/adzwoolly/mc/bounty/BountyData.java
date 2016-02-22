package uk.adzwoolly.mc.bounty;

import org.bukkit.Location;

public class BountyData {
	
	private int bountyAmount;
	private Location lastKilledLoc;
	
	public BountyData(int bountyAmount, Location loc){
		this.bountyAmount = bountyAmount;
		this.lastKilledLoc = loc;
	}
	
	public int getBountyAmount(){
		return bountyAmount;
	}
	
	public Location getLocation(){
		return lastKilledLoc;
	}
	
	public void setBountyData(int bountyAmount, Location loc){
		this.bountyAmount = bountyAmount;
		lastKilledLoc = loc;
	}
}
