package uk.adzwoolly.mc.bounty;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

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
				bounties.addBounty(killer.getUniqueId());
				Bukkit.broadcastMessage(killer.getDisplayName() + " murdered " + dead.getDisplayName() + ".  There is now a £" + bounties.getBounty(killer.getUniqueId()) + " bounty on " + killer.getDisplayName() + ".");
			}
		}
	}
}
