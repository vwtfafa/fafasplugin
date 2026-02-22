package de.fafasplugin;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class EconomyService {
    private final JavaPlugin plugin;
    private final DataStore dataStore;

    public EconomyService(JavaPlugin plugin, DataStore dataStore) {
        this.plugin = plugin;
        this.dataStore = dataStore;
    }

    public double balance(Player player) {
        return dataStore.getBalance(player.getUniqueId());
    }

    public void setBalance(Player player, double value) {
        dataStore.setBalance(player.getUniqueId(), value);
        dataStore.save();
    }

    public boolean pay(Player from, Player to, double amount) {
        if (amount <= 0) {
            return false;
        }
        double fromBal = balance(from);
        if (fromBal < amount) {
            return false;
        }
        setBalance(from, fromBal - amount);
        setBalance(to, balance(to) + amount);
        return true;
    }

    public boolean buy(Player player, double price) {
        double bal = balance(player);
        if (bal < price) {
            return false;
        }
        setBalance(player, bal - price);
        return true;
    }
}
