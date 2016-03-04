package uk.adzwoolly.mc.bounty;

import org.bukkit.OfflinePlayer;

/**
 * Created by Aaron on 04/03/2016.
 */
public class Debt {

    private int amount;

    public Debt(int x){
        amount = x;
    }

    public void addDebt(int x){
        amount+= x;
    }
}
