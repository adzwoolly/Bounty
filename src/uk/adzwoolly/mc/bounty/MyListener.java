package uk.adzwoolly.mc.bounty;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class MyListener implements Listener{
	
	Plugin plugin;
	BountyManager bounties;
	
	public MyListener(Plugin plugin, BountyManager bountyManager){
		this.plugin = plugin;
		bounties = bountyManager;
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e){
		Player dead = e.getEntity();
		Player killer = dead.getKiller();
		if(killer != null){
			int bounty = bounties.getBounty(e.getEntity().getUniqueId());
			if(bounty != 0){
				bounties.redeemBounty(killer, dead);
				killer.sendMessage("You claim the £" + bounty + " bounty on " + dead.getDisplayName() + ".");
				Bukkit.broadcastMessage("The bounty on " + dead.getDisplayName() + " has been claimed.");
			} else{
				bounties.addBounty("PVP", killer.getUniqueId(), killer.getLocation());
				Bukkit.broadcastMessage(killer.getDisplayName() + " murdered " + dead.getDisplayName() + ".  There is now a £" + bounties.getBounty(killer.getUniqueId()) + " bounty on " + killer.getDisplayName() + ".");
			}
		}
	}
	
	private HashMap<UUID, Long> hotPlayers = new HashMap<UUID, Long>();
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e){
		if(e.getBlockPlaced().getType() == Material.TNT){
			Player p = e.getPlayer();
			bounties.addBounty("TNT", p.getUniqueId(), p.getLocation());
			
			hotPlayers.put(p.getUniqueId(), System.currentTimeMillis());
			
			new BukkitRunnable() {
		        @Override
	            public void run() {
	                // What you want to schedule goes here
	            	if(hotPlayers.containsKey(p.getUniqueId())){
	            		//if last block placed was longer ago than 30 seconds
	        			if(hotPlayers.get(p.getUniqueId()) <= System.currentTimeMillis() - 1000*29.95){
	        				Bukkit.broadcastMessage(p.getDisplayName() + " has placed TNT.  There is now a £" + bounties.getBounty(p.getUniqueId()) + " bounty on " + p.getDisplayName() + ".");
	        			}
	    			}
	            }
	        }.runTaskLater(this.plugin, 20*30);
		}
	}
}
