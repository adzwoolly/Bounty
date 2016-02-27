package uk.adzwoolly.mc.bounty;

import java.util.HashMap;
import java.util.StringJoiner;

import org.bukkit.Location;

public class BountyData {
	
	private HashMap<String, Integer> values = new HashMap<String, Integer>();
	private Location lastCrimeLocation;
	
	public BountyData(String type, int value, Location loc){
		setBountyData(type, value, loc);
	}
	
	public int getTotalBounty(){
		int totalBounty = 0;
		for (int i : values.values()) {
		    totalBounty += i;
		}
		return totalBounty;
	}
	
	public int getBounty(String type){
		if(values.containsKey(type)){
			return values.get(type);
		}
		return 0;
	}
	
	public Location getLocation(){
		return lastCrimeLocation;
	}
	
	public void setBountyData(String type, int value, Location loc){
		values.put(type, value);
		if(loc != null){
			lastCrimeLocation = loc;
		}
	}
	
	protected String getSaveData(){
		/*  This works but, there is s better Java 8 feature below
		StringBuilder sb = new StringBuilder();
		values.forEach((key, value) -> sb.append(key + "," + value + ";"));
		
		//sb.deleteCharAt(sb.length() - 1); <-- Copies the array (expensive)
		sb.setLength(sb.length() - 1);// <-- Chops off the last char (not as expensive)
		*/
		
		StringJoiner sj = new StringJoiner(";");
		values.forEach((key, value) -> sj.add(key + "," + value));
		
		return sj.toString();
	}
}
