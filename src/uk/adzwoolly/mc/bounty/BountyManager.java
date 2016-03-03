package uk.adzwoolly.mc.bounty;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import static uk.adzwoolly.mc.bounty.Bounty.economy;

public class BountyManager extends BukkitRunnable{
	
	HashMap<UUID, BountyData> bounties = new HashMap<UUID, BountyData>();
	Plugin plugin;
	
	public BountyManager(Plugin plugin){
		this.plugin = plugin;
		loadBounties();
	}
	
	public int getBounty(UUID id){
		if(bounties.containsKey(id)){
			BountyData bounty = bounties.get(id);
			if(bounty != null){
				return bounty.getTotalBounty();
			}
		}
		return 0;
	}
	
	/**
	 * @description Remove the bounty amount from the dead's account and deposit it in the killer's account.  Remove the bounty off the dead's head.
	 * @param killer The player who killed, and should receive the bounty.
	 * @param dead The player who had a bounty on them and has been killed.
	 */
	public void redeemBounty(Player killer, Player dead){
		/* Old method of redeeming a bounty.  This was before admin bounties.
		int bounty = getBounty(dead.getUniqueId());
		if(bounty != 0){
			economy.withdrawPlayer(dead, bounty);
			economy.depositPlayer(killer, bounty);
			bounties.remove(dead.getUniqueId());
		}*/
		
		UUID deadID = dead.getUniqueId();
		
		if(bounties.containsKey(deadID)){
			BountyData bounty = bounties.get(deadID);
			if(bounty != null){
				int adminBountyValue = bounty.getBounty("admin");
				if(adminBountyValue != 0){
					Bukkit.broadcastMessage("Oh, wait.  I shouldn't be here...");
					economy.depositPlayer(killer, adminBountyValue);
				}
				//I wanted to reuse the above in variable but, I figured using a new one would be much easier to understand
				int normalBountyValue = bounty.getTotalBounty() - adminBountyValue;
				if(normalBountyValue != 0){
					Bukkit.broadcastMessage("Trying to take a normal (non-admin) bounty of £" + normalBountyValue);
					if(economy.withdrawPlayer(dead, normalBountyValue).transactionSuccess()){
						Bukkit.broadcastMessage("Transaction successful!");
					}
					economy.depositPlayer(killer, normalBountyValue);
				}
				bounties.remove(dead.getUniqueId());
			}
		}		
	}
	
	public boolean removeBounty(UUID id){
		if(bounties.remove(id) != null){
			return true;
		}
		return false;
	}
	
	/**
	 * @description Increase the bounty on a player.  Starting at user defined amount (default 10), multiply by config amount (default 2) every time.
	 * @param type The type of crime committed
	 * @param id The UUID of the player to place a bounty on.
	 * @param loc The location the crime was committed
	 */
	public void addBounty(String type, UUID id, Location loc){
		
		if(!Bukkit.getPlayer(id).isOp()){
		
			FileConfiguration config = plugin.getConfig();
			
			if(config.getBoolean(type + ".enabled") == true){
				if(bounties.containsKey(id)){ 
					BountyData bountyData = bounties.get(id);
					int value = bountyData.getBounty(type);
					if(value != 0){
						if(config.getString(type + ".increaseType").equalsIgnoreCase("multiply")){
							value = (int) (value * config.getDouble(type + ".increaseValue"));
						} else if(config.getString(type + ".increaseType").equalsIgnoreCase("add")){
							value = (int) (value + config.getDouble(type + ".increaseValue"));
						} else{
							plugin.getLogger().severe("The config is not set up properly, and bounties are therefore not being increased.");
						}
						bountyData.setBountyData(type, value, loc);
					} else{
						bountyData.setBountyData(type, config.getInt(type + ".startingBounty"), loc);
					}
				} else{
					bounties.put(id, new BountyData(type, config.getInt(type + ".startingBounty"), loc));
				}
			}
		}
	}
	
	public void addAdminBounty(UUID id, int value, Boolean saveLoc){
		String type = "admin";
		Location loc = null;
		if(saveLoc){
			 loc = Bukkit.getPlayer(id).getLocation();
		}
		if(bounties.containsKey(id)){
			BountyData bountyData = bounties.get(id);
			bountyData.setBountyData(type, value, loc);
		} else{
			bounties.put(id, new BountyData(type, value, loc));
		}
	}
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public boolean hasBounties(){
		if(bounties.isEmpty()){
			return false;
		}
		return true;
	}
	
	public String listBounties(){
		StringBuilder sb = new StringBuilder();
		bounties.forEach((key, value) -> sb.append(Bukkit.getOfflinePlayer(key).getName() + ": Â£" + value.getTotalBounty() + " (Last seen at: " + value.getLocation().getBlockX() + ", " + value.getLocation().getBlockY() + ", " + value.getLocation().getBlockZ() + ")\n"));
		
		return sb.toString();
	}
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/* Queue methods for reading and saving bounties to a text file! */
	
	
	private void loadBounties(){
		
		String fileName = "plugins/Bounty/bountyRecords.txt";
		
		try {
			
			// FileReader reads text files in the default encoding.
			FileReader fileReader = 
			new FileReader(fileName);

			// Always wrap FileReader in BufferedReader.
			BufferedReader bufferedReader = 
			new BufferedReader(fileReader);
			
			String line;
			
			while((line = bufferedReader.readLine()) != null) {
				//File structure-	UUID;type,value;type,value;locWorld,locX,locY,locZ
				//saveData {UUID	type,value	type,value	locWorld,locX,locY,locZ}
				String[] saveData = line.split(";");
				//bounty {type	value}
				String[] bounty = saveData[1].split(",");
				//loc {locWorld		locX	locY	locZ}
				String[] loc = saveData[saveData.length - 1].split(",");
				Location location = new Location(Bukkit.getWorld(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]), Integer.parseInt(loc[3]));
				//Create a BountyData object
				BountyData bountyData = new BountyData(bounty[0],Integer.parseInt(bounty[1]), location);
				if(saveData.length > 3){
					for(int i = 2; i < saveData.length - 1; i++){
						bounty = saveData[i].split(",");
						bountyData.setBountyData(bounty[0], Integer.parseInt(bounty[1]), location);
					}
				}
				bounties.put(UUID.fromString(saveData[0]), bountyData);
			}
			
			// Always close files.
			bufferedReader.close();
		}
		catch(FileNotFoundException ex) {
			System.out.println("Unable to open file '" + fileName + "'");
		}
		catch(IOException ex) {
			System.out.println("Error reading file '" + fileName + "'");
			// Or we could just do this: 
			// ex.printStackTrace();
		}
	}
	
	public void saveBounties(){
		// The name of the file to open.
		String fileName = "plugins/Bounty/bountyRecords.txt";
		
		try {
			// Assume default encoding.
			FileWriter fileWriter = new FileWriter(fileName);
			
			// Always wrap FileWriter in BufferedWriter.
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			
			// Note that write() does not automatically
			// append a newline character.
			
			bounties.forEach((key, value) -> {
				try{
					//File structure-	UUID;type,value;type,value;locWorld,locX,locY,locZ
					Location loc = value.getLocation();
					String locString = loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
					bufferedWriter.write(key + ";" + value.getSaveData() + ";" + locString);
					bufferedWriter.newLine();
				} catch(IOException e){
					
				}
			});
			
			// Always close files.
			bufferedWriter.close();
		}
		catch(IOException ex) {
			System.out.println("Error writing to file '" + fileName + "'");
			// Or we could just do this:
			// ex.printStackTrace();
		}
	}
	
	//Autosave
	@Override
	public void run() {
		saveBounties();
		Bukkit.getLogger().info("Bounties saved.");
	}
}
