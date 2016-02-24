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
import org.bukkit.OfflinePlayer;
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
		BountyData bounty = bounties.get(id);
		if(bounty != null){
			return bounty.getBountyAmount();
		}
		return 0;
	}
	
	/**
	 * @description Remove the bounty amount from the dead's account and deposit it in the killer's account.
	 * @param killer The player who killed, and should receive the bounty.
	 * @param dead The player who had a bounty on them and has been killed.
	 */
	public void redeemBounty(Player killer, Player dead){
		int bounty = getBounty(dead.getUniqueId());
		if(bounty != 0){
			economy.withdrawPlayer(dead, bounty);
			economy.depositPlayer(killer, bounty);
			bounties.remove(dead.getUniqueId());
		}
	}
	
	/**
	 * @description Increase the bounty on a player.  Starting at user defined amount (default 10), multiply by config amount (default 2) every time.
	 * @param id The UUID of the player to place a bounty on.
	 */
	public void addBounty(String type, UUID id, Location loc){
		if(bounties.containsKey(id)){ 
			
		} else{
			bounties.put(id, new BountyData(Bounty.START_BOUNTY, loc));
		}
		
		if(plugin.getConfig().getString(type + ".increaseType").equals("multiply")){
			
		}
		
		
		
		
			
		
	}

	private void addPVPBounty(){

		////////////////////////////////////////////////BountyData bountyData = bounties.get(id);
		//////////////////////////////////////////////bountyData.setBountyData((int) (bountyData.getBountyAmount() * Bounty.BOUNTY_MULTIPLIER), loc);
	}
	
	public boolean hasBounties(){
		if(bounties.isEmpty()){
			return false;
		}
		return true;
	}
	
	public String listBounties(){
		StringBuilder sb = new StringBuilder();
		bounties.forEach((key, value) -> sb.append(getNameFromUUID(key) + ": £" + value + "\n"));
		
		return sb.toString();
	}
	
	//I know, it doesn't really belong here...
	private String getNameFromUUID(UUID id){
		OfflinePlayer player = Bukkit.getOfflinePlayer(id);
		if (player.hasPlayedBefore()) {
		    return player.getName();
		} else {
		    Bukkit.getLogger().warning("[Bounty] BountyManager is asking for players it shouldn't!");
		    return "Internal error!";
		}
	}	
	
	
	
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
                System.out.println(line);
                String[] bounty = line.split(";");
                String[] loc = bounty[2].split(",");
                BountyData bountyData = new BountyData(Integer.parseInt(bounty[1]), new Location(Bukkit.getWorld(loc[0]), Double.parseDouble(loc[1]), Double.parseDouble(loc[2]), Double.parseDouble(loc[3])));
                bounties.put(UUID.fromString(bounty[0]), bountyData);
            }
            
            // Always close files.
            bufferedReader.close();         
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + fileName + "'");                  
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
            		bufferedWriter.write(key + ";" + value.getBountyAmount() + ";" + value.getLocation().getWorld().getName() + "," + value.getLocation().getX() + "," + value.getLocation().getY() + "," + value.getLocation().getZ());
            		bufferedWriter.newLine();
            	} catch(IOException e){
            		
            	}
            });

            // Always close files.
            bufferedWriter.close();
        }
        catch(IOException ex) {
            System.out.println(
                "Error writing to file '"
                + fileName + "'");
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
