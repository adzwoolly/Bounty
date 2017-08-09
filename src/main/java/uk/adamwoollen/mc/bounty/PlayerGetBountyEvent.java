package uk.adamwoollen.mc.bounty;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class PlayerGetBountyEvent extends Event implements Cancellable{
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	
	private String type;
	//A defendant is a person or entity accused of a crime in criminal prosecution
	private Player defendant;
	private Location loc;
	
	public PlayerGetBountyEvent(String type, Player defendant, Location loc){
		cancelled = false;
		this.type = type;
		this.defendant = defendant;
		this.loc = loc;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }
	
	/**
	 * Accessor method:
	 * @return The type of bounty being placed on the player. E.g. "PVP", "TNT", "admin"
	 */
	public String getType(){
		return type;
	}
	
	/**
	 * Accessor method:
	 * @return The player who committed the crime and is set to get a bounty
	 */
	public Player getPlayer(){
		return defendant;
	}
	
	/**
	 * Returns the location of the crime committed
	 * It is possible for this to be null
	 * @return The location of the player who is getting a bounty
	 */
	public Location getLocation(){
		return loc;
		
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}
	
}
