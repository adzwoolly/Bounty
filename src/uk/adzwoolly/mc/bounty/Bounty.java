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
	private int saveInterval = 30;
	private BountyManager bountyManager;
	
	File bountyRecords = new File("plugins/Bounty/bountyRecords.txt");
	
	//Fired when plugin is first enabled
	@Override
	public void onEnable(){
		bountyManager = new BountyManager(this);
		
		getServer().getPluginManager().registerEvents(new MyListener(this, bountyManager), this);
		getCommand("bounty").setExecutor(new uk.adzwoolly.mc.bounty.commands.BountyCommand(this, bountyManager));
		
		if(!setupEconomy()){
			getLogger().severe("The vault integration is broken");
		}
		
		//If there is no config file, create a new default one
		this.saveDefaultConfig();
		//Load values from config
		FileConfiguration config = getConfig();
		saveInterval = config.getInt("saveInterval");
		
		//If there is no bounty save file, create a new empty one
		try {
			bountyRecords.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//If no save interval, warn user.  Otherwise, save on interval.
		if(saveInterval == 0){
			getLogger().warning("saveInterval (in config) is 0.  Bounties will NOT save automatically (only when using command 'stop')");
		} else{
			//Runs bountyManager.run() (saves bounty data) every saveInterval minutes
			@SuppressWarnings("unused")
			BukkitTask task = bountyManager.runTaskTimer(this, 20*60*saveInterval, 20*60*saveInterval);
		}
		
	}
	
	//Fired when plugin is disabled
	@Override
	public void onDisable(){
		bountyManager.saveBounties();
	}
	
	private boolean setupEconomy(){
		//Check if vault is on server
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			getLogger().severe("[Bounty] Could not find Vault!");
			return false;
		}
		//Make sure there is an economy provider, such as Essentials or CraftConomy3
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		} else{
			getLogger().severe("There is no economy plugin installed.");
		}
		
		return (economy != null);
	}
	
}
