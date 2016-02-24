package uk.adzwoolly.mc.bounty;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;

public class MyListener implements Listener{
	
	BountyManager bounties;
	
	public MyListener(BountyManager bountyManager){
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
				///////////////////////////////////////////////////////////////////////////////////////////////////sbounties.addBounty(killer.getUniqueId(), killer.getLocation());
				Bukkit.broadcastMessage(killer.getDisplayName() + " murdered " + dead.getDisplayName() + ".  There is now a £" + bounties.getBounty(killer.getUniqueId()) + " bounty on " + killer.getDisplayName() + ".");
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e){
		if(e.getBlockPlaced().getType() == Material.TNT){
			Player p = e.getPlayer();
			/////////////////////////////////////////////////////////bounties.addBounty(p.getUniqueId(), p.getLocation());
		}
	}
	
	@EventHandler
	public void brandino(PlayerAchievementAwardedEvent e){
		if(e.getPlayer().getName().equalsIgnoreCase("brandino")){
			Bukkit.broadcastMessage("Congrats!");
		}
	}
}
