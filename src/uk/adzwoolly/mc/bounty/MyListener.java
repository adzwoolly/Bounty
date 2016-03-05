package uk.adzwoolly.mc.bounty;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 
 * @author Adzwoolly (Adam Woollen)
 * @author Aaron Tello-Wharton
 *
 */
public final class MyListener implements Listener{
	
	Plugin plugin;
	BountyManager bounties;

	public MyListener(Plugin plugin, BountyManager bountyManager){
		this.plugin = plugin;
		bounties = bountyManager;
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e){
		Player dead = e.getEntity().getPlayer();
		Player killer = dead.getKiller();
		if(killer != null){
			int bounty = bounties.getBounty(e.getEntity().getUniqueId());
			if(bounty != 0){
				bounties.redeemBounty(killer, dead);
				killer.sendMessage(Bounty.msgPrefix + "You claim the £" + bounty + " bounty on " + dead.getDisplayName() + ".");
				Bukkit.broadcastMessage(Bounty.msgPrefix + "The bounty on " + dead.getDisplayName() + " has been claimed.");
			} else{
				// Create the event here
				PlayerGetBountyEvent event = new PlayerGetBountyEvent("PVP", killer, killer.getLocation());
				// Call the event
				Bukkit.getServer().getPluginManager().callEvent(event);
				
				Bukkit.broadcastMessage(Bounty.msgPrefix + killer.getDisplayName() + " murdered " + dead.getDisplayName() + ".  There is now a " + ChatColor.DARK_RED + "£" + bounties.getBounty(killer.getUniqueId()) + ChatColor.RESET + " bounty on " + ChatColor.DARK_RED + killer.getDisplayName() + ChatColor.RESET + ".");
			}
		}
	}
	
	private HashMap<UUID, Long> hotPlayers = new HashMap<UUID, Long>();
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e){
		if(e.getBlockPlaced().getType() == Material.TNT){
			Player p = e.getPlayer();
			
			//I know this doesn't seem good, as I check OP to add bounty but, I also need to not do the message as well
			if(bounties.canGetBounty(p.getUniqueId())){
				
				// Create the event here
				PlayerGetBountyEvent event = new PlayerGetBountyEvent("TNT", p, p.getLocation());
				// Call the event
				Bukkit.getServer().getPluginManager().callEvent(event);
				
				if(!event.isCancelled()){
					p.sendMessage(Bounty.msgPrefix + "Placing TNT gives you a bounty!  You now have a bounty of £" + bounties.getBounty(p.getUniqueId()));
					
					hotPlayers.put(p.getUniqueId(), System.currentTimeMillis());

					new BukkitRunnable() {
						@Override
						public void run() {
							// What you want to schedule goes here
							if(hotPlayers.containsKey(p.getUniqueId())){
								//if last block placed was longer ago than 30 seconds
								if(hotPlayers.get(p.getUniqueId()) <= System.currentTimeMillis() - 1000*29.93){
									Bukkit.broadcastMessage(Bounty.msgPrefix + p.getDisplayName() + " has placed TNT.  There is now a " + ChatColor.DARK_RED + "£" + bounties.getBounty(p.getUniqueId()) + ChatColor.RESET + " bounty on " + ChatColor.DARK_RED + p.getDisplayName() + ChatColor.RESET + ".");
								}
							}
						}
					}.runTaskLater(this.plugin, 20*30);
				}
			}
		}
	}
	
	/**
	 * 
	 * @author Aaron Tello-Wharton
	 * @param e
	 */
	@EventHandler
	public void interruptCommand(PlayerCommandPreprocessEvent e) {
		String command = e.getMessage();
		Player player = e.getPlayer();
		
		if (command.equalsIgnoreCase("/spawn")) {
			if(bounties.getBounty(player.getUniqueId()) != 0) {
				e.setCancelled(true);
				new BukkitRunnable() {
					@Override
					public void run() {
						player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "OOPS!   " + ChatColor.RESET + ChatColor.ITALIC +  "Only good boys and girls get to teleport here :3");
					}
				}.runTaskLater(plugin, 20);

			}
		}
	}
}
