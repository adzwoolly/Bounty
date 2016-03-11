package uk.adzwoolly.mc.bounty;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;

public class Bounty extends JavaPlugin{
	
	public static Economy economy = null;
	public static final String msgPrefix = ChatColor.DARK_AQUA + "[Bounty] " + ChatColor.RESET;
	public static final String msgNoPermission = "You do not have the required permissions to run this command.";
	private int saveInterval = 30;
	private BountyManager bountyManager;
	
	File bountyRecords = new File("plugins/Bounty/bountyRecords.txt");
	
	//Fired when plugin is first enabled
	@Override
	public void onEnable(){
		bountyManager = new BountyManager(this);
		
		//register listener and commands
		getServer().getPluginManager().registerEvents(new MyListener(this, bountyManager), this);
		getServer().getPluginManager().registerEvents(bountyManager, this);
		registerCommands();
		
		//Setup economy with Vault
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
			//Runs bountyManager.run() (saves bounty data) every 'saveInterval' minutes
			@SuppressWarnings("unused")
			BukkitTask task = bountyManager.runTaskTimer(this, 20*60*saveInterval, 20*60*saveInterval);
		}
		
	}
	
	//Fired when plugin is disabled
	@Override
	public void onDisable(){
		bountyManager.saveBounties();
	}
	
	/**
	 * Method - Tells Bukkit what classes should be used to execute commands
	 * @author Adzwoolly (Adam Woollen)
	 */
	private void registerCommands(){
		getCommand("bounty").setExecutor(new uk.adzwoolly.mc.bounty.commands.BountyCommand(this, bountyManager));
		getCommand("rmbounty").setExecutor(new uk.adzwoolly.mc.bounty.commands.RMBountyCommand(this, bountyManager));
		getCommand("adminbounty").setExecutor(new uk.adzwoolly.mc.bounty.commands.AdminBountyCommand(this, bountyManager));
		getCommand("bountyreload").setExecutor(new uk.adzwoolly.mc.bounty.commands.BountyReloadCommand(this));
	}
	
	/**
	 * Checks if Vault is enabled on the server and that there is an economy plugin installed.  Saves the economy to field "economy".
	 * @author Adzwoolly (Adam Woollen)
	 * @return Whether the economy was successfully set up.
	 */
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
