package uk.adzwoolly.mc.bounty;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class Bounty extends JavaPlugin{
	
	public static Economy economy = null;
	public static int START_BOUNTY = 10;
	public static double BOUNTY_MULTIPLIER;
	private BountyManager bountyManager;
	
	//Fired when plugin is first enabled
	@Override
	public void onEnable(){
		bountyManager = new BountyManager();
		getServer().getPluginManager().registerEvents(new MyListener(bountyManager), this);
		getCommand("bounty").setExecutor(new uk.adzwoolly.mc.bounty.commands.BountyCommand(bountyManager));
		if(!setupEconomy()){
			getLogger().severe("[Bounty] The vault integration is broken");
		}
		
    	FileConfiguration config = getConfig();
    	
    	config.addDefault("startingBounty", 10);
    	config.addDefault("bountyMultiplier", 2.0);
    	
    	config.options().copyDefaults(true);
    	saveConfig();
    	
    	START_BOUNTY = config.getInt("startingBounty");
    	BOUNTY_MULTIPLIER = config.getDouble("bountyMultiplier");
	}
    
    //Fired when plugin is disabled
    @Override
    public void onDisable(){
    	//
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
