package uk.adzwoolly.mc.bounty;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import net.milkbowl.vault.economy.Economy;

public class Bounty extends JavaPlugin{
	
	public static Economy economy = null;
	public static int START_BOUNTY = 10;
	public static double BOUNTY_MULTIPLIER = 2;
	public static int SAVE_INTERVAL = 30;
	private BountyManager bountyManager;
	
	File bountyRecords = new File("plugins/Bounty/bountyRecords.txt");
	
	//Fired when plugin is first enabled
	@Override
	public void onEnable(){
		bountyManager = new BountyManager(this);
		getServer().getPluginManager().registerEvents(new MyListener(bountyManager), this);
		getCommand("bounty").setExecutor(new uk.adzwoolly.mc.bounty.commands.BountyCommand(this, bountyManager));
		if(!setupEconomy()){
			getLogger().severe("The vault integration is broken");
		}
		
    	FileConfiguration config = getConfig();
    	
    	config.addDefault("startingBounty", 10);
    	config.addDefault("bountyMultiplier", 2.0);
    	config.addDefault("saveInterval", 30);
    	
    	config.options().copyDefaults(true);
    	saveConfig();
    	
    	START_BOUNTY = config.getInt("startingBounty");
    	BOUNTY_MULTIPLIER = config.getDouble("bountyMultiplier");
    	SAVE_INTERVAL = config.getInt("saveInterval");
    	
    	try {
			bountyRecords.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	if(SAVE_INTERVAL == 0){
    		getLogger().warning("saveInterval (in config) is 0.  Bounties will NOT save automatically (only when using /stop)");
    	} else{
    		@SuppressWarnings("unused")
    		BukkitTask task = bountyManager.runTaskTimer(this, 20*60*SAVE_INTERVAL, 20*60*SAVE_INTERVAL);
    	}
    	
	}
    
    //Fired when plugin is disabled
    @Override
    public void onDisable(){
    	bountyManager.saveBounties();
	}
    
	private boolean setupEconomy(){
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			getLogger().severe("[Bounty] Could not find Vault!");
			return false;
		}
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		} else{
			getLogger().severe("There is no economy plugin installed.");
		}
	    
		return (economy != null);
    }
	
}
