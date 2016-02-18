package uk.adzwoolly.mc.bounty;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static uk.adzwoolly.mc.bounty.Bounty.economy;

public class BountyManager {
	
	HashMap<UUID, Integer> bounties = new HashMap<UUID, Integer>();
	
	public BountyManager(){
		loadBounties();
	}
	
	public int getBounty(UUID id){
		Integer bounty = bounties.get(id);
		if(bounty != null){
			return bounty;
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
	 * @description Increase the bounty on a player.  Starting at 10, double every time.
	 * @param id The UUID of the player to place a bounty on.
	 */
	public void addBounty(UUID id){
		if(bounties.containsKey(id)){
			bounties.put(id, (int)(bounties.get(id) * Bounty.BOUNTY_MULTIPLIER));
		} else{
			bounties.put(id, Bounty.START_BOUNTY);
		}
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
		    return "ERROR!";
		}
	}
	
	
	
	
	
	/* Queue methods for reading and saving bounties to a text file! */
	
	
	public void loadBounties(){
		
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
                String[] bounty = line.split(",");
                bounties.put(UUID.fromString(bounty[0]), Integer.parseInt(bounty[1]));
                System.out.println(bounty[1]);
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
}
