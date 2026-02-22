package de.fafasplugin;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class HomeService {
    private final JavaPlugin plugin;
    private final DataStore dataStore;

    public HomeService(JavaPlugin plugin, DataStore dataStore) {
        this.plugin = plugin;
        this.dataStore = dataStore;
    }

    public void setHome(Player player, String name) {
        dataStore.setHome(player.getUniqueId(), name, player.getLocation());
        dataStore.save();
    }

    public boolean teleportHome(Player player, String name) {
        Location home = dataStore.getHome(player.getUniqueId(), name);
        if (home == null || home.getWorld() == null) {
            return false;
        }
        return player.teleport(home);
    }

    public void teleportSpawn(Player player) {
        player.teleport(plugin.getServer().getWorlds().getFirst().getSpawnLocation());
    }
}
