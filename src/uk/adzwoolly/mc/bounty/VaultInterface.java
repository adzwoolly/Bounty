package uk.adzwoolly.mc.bounty;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Created by Aaron on 03/03/2016.
 */
public class VaultInterface {

    private Bounty bounty;
    private RegisteredServiceProvider<Economy> economyProvider;
    private Economy economy;

    public VaultInterface(Bounty bounty){
        bounty = this.bounty;
        RegisteredServiceProvider<Economy> economyProvider = bounty.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        } else{
            bounty.getLogger().severe("There is no economy plugin installed.");
        }

    }
}
