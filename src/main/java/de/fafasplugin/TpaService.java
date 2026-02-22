package de.fafasplugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TpaService {
    private final JavaPlugin plugin;
    private final Map<UUID, UUID> pendingRequests = new HashMap<>();

    public TpaService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void request(Player from, Player to) {
        pendingRequests.put(to.getUniqueId(), from.getUniqueId());
    }

    public Player accept(Player target) {
        UUID requester = pendingRequests.remove(target.getUniqueId());
        if (requester == null) {
            return null;
        }
        Player player = plugin.getServer().getPlayer(requester);
        if (player != null) {
            player.teleport(target.getLocation());
        }
        return player;
    }

    public Player deny(Player target) {
        UUID requester = pendingRequests.remove(target.getUniqueId());
        if (requester == null) {
            return null;
        }
        return plugin.getServer().getPlayer(requester);
    }
}
