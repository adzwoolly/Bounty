package uk.adamwoollen.mc.bounty;

import static uk.adamwoollen.mc.bounty.Bounty.economy;

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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * A class to store and manage bounties on players
 * @author Adzwoolly (Adam Woollen)
 *
 */
public class BountyManager extends BukkitRunnable implements Listener{
	
	Plugin plugin;
	HashMap<UUID, BountyData> bounties = new HashMap<UUID, BountyData>();
	
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
	 * Remove the bounty amount from the dead's account and deposit it in the killer's account.  Remove the bounty off the dead's head.
	 * @param killer The player who killed, and should receive the bounty.
	 * @param dead The player who had a bounty on them and has been killed.
	 */
	public void redeemBounty(Player killer, Player dead){
		UUID deadID = dead.getUniqueId();
		
		if(bounties.containsKey(deadID)){
			BountyData bounty = bounties.get(deadID);
			if(bounty != null){
				int adminBountyValue = bounty.getBountyOfType("admin");
				if(adminBountyValue != 0){
					economy.depositPlayer(killer, adminBountyValue);
				}
				//I wanted to reuse the above in variable but, I figured using a new one would be much easier to understand
				int normalBountyValue = bounty.getTotalBounty() - adminBountyValue;
				if(normalBountyValue != 0){
					economy.withdrawPlayer(dead, normalBountyValue);
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
	
	/* There is an event handler separate to addBounty() so that addBounty() can still
	* be used to add bounties when loading from file without other plugins intercepting
	* already existing bounties and stopping them.
	* May move this to where the event is called, so that it can run after highest
	* priority, in case someone decides they have to have highest
	*/
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerGetBounty(PlayerGetBountyEvent e){
		if(!e.isCancelled()){
			addBounty(e.getType(), e.getPlayer().getUniqueId(), e.getLocation());
		}
	}
	
	
	/**
	 * Increase the bounty on a player.
	 * The amount increased is dependent on the type of bounty and how it is configured in the "config.yml" file.
	 * This is private so that other classes must use the event system to place bounties.
	 * @param type The type of crime committed
	 * @param id The UUID of the player to place a bounty on.
	 * @param loc The location the crime was committed
	 */
	private void addBounty(String type, UUID id, Location loc){
		FileConfiguration config = plugin.getConfig();
		
		if(canGetBounty(id)){
			if(config.getBoolean(type + ".enabled") == true){
				if(bounties.containsKey(id)){ 
					BountyData bountyData = bounties.get(id);
					int value = bountyData.getBountyOfType(type);
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
	
	/**
	 * Sets the admin bounty on a given player
	 * @param id The UUID of the player we're putting the bounty on.
	 * @param value The size of the bounty to be placed on them.
	 * @param saveLoc Whether the player's location should be saved with the bounty.
	 */
	public void setAdminBounty(UUID id, int value, Boolean saveLoc){
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
	
	public boolean canGetBounty(UUID id){
		if(plugin.getConfig().getBoolean("opsGetBounties") || !Bukkit.getPlayer(id).isOp()){
			return true;
		}
		return false;
	}
	
	public boolean hasBounties(){
		if(bounties.isEmpty()){
			return false;
		}
		return true;
	}
	
	public String listBountiesAsString(){
		StringBuilder sb = new StringBuilder();
		bounties.forEach((key, value) -> {
			Location loc = value.getLocation();
			if(loc != null){
				sb.append(Bukkit.getOfflinePlayer(key).getName() + ": £" + value.getTotalBounty() + " (Last seen at: " + value.getLocation().getBlockX() + ", " + value.getLocation().getBlockY() + ", " + value.getLocation().getBlockZ() + ")\n");
			} else{
				sb.append(Bukkit.getOfflinePlayer(key).getName() + ": £" + value.getTotalBounty() + " (Location unknown)\n");
			}
			
		});
		return sb.toString();
	}
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/* Queue methods for reading and saving bounties to a text file! */
	
	
	private void loadBounties(){
		FileInterface.loadBounties(bounties);
	}
	
	public void saveBounties(){
		FileInterface.saveBounties(bounties);
	}
	
	//Autosave
	@Override
	public void run(){
		saveBounties();
		Bukkit.getLogger().info("Bounties saved.");
	}
	
	private abstract static class FileInterface {
		
		static boolean failedLoad = false;
		
		private static int getIntFromString(String text){
			try{
				return Integer.parseInt(text);
			} catch(NumberFormatException e){
				failedLoad = true;
				return 0;
			}
		}
		
		private static void loadBounties(HashMap<UUID, BountyData> bounties){
			
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
					failedLoad = false;
					
					//File structure-	UUID;type,value;type,value;locWorld,locX,locY,locZ
					//saveData {UUID	type,value	type,value	locWorld,locX,locY,locZ}
					String[] saveData = line.split(";");
					//bounty {type	value}
					String[] bounty = saveData[1].split(",");
					//loc {locWorld		locX	locY	locZ}
					String[] loc = saveData[saveData.length - 1].split(",");
					
					Location location = null;
					
					if(loc.length == 4){
						try{
							int x = Integer.parseInt(loc[1]);
							int y = Integer.parseInt(loc[2]);
							int z = Integer.parseInt(loc[3]);
							location = new Location(Bukkit.getWorld(loc[0]), x, y, z);
						} catch(NumberFormatException e){
							
						}
					}
					
					//Create a BountyData object
					BountyData bountyData = new BountyData(bounty[0],getIntFromString(bounty[1]), location);
					if(saveData.length > 3){
						for(int i = 2; i < saveData.length - 1; i++){
							bounty = saveData[i].split(",");
							bountyData.setBountyData(bounty[0], getIntFromString(bounty[1]), location);
						}
					}
					if(failedLoad){
						Bukkit.getLogger().severe("A record is corrupt, and was unable to be loaded.");
					} else{
						bounties.put(UUID.fromString(saveData[0]), bountyData);
					}
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
		
		private static void saveBounties(HashMap<UUID, BountyData> bounties){
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
						String locString = ",,,";
						if(loc != null){
							locString = loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
						}
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
	}
	
}
