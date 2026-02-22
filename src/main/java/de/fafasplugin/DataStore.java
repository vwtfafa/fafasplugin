package de.fafasplugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class DataStore {
    private final JavaPlugin plugin;
    private final File file;
    private YamlConfiguration data;

    public DataStore(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "data.yml");
        this.data = new YamlConfiguration();
    }

    public void load() {
        if (!file.exists()) {
            plugin.saveResource("data.yml", false);
        }
        data = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            data.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save data.yml: " + e.getMessage());
        }
    }

    public void setHome(UUID playerId, String name, Location location) {
        data.set("homes." + playerId + "." + name, location);
    }

    public Location getHome(UUID playerId, String name) {
        return data.getLocation("homes." + playerId + "." + name);
    }

    public void setBalance(UUID playerId, double value) {
        data.set("money." + playerId, value);
    }

    public double getBalance(UUID playerId) {
        return data.getDouble("money." + playerId, 100.0);
    }

    public void setMuteUntil(UUID playerId, long epochMs) {
        data.set("mutes." + playerId, epochMs);
    }

    public long getMuteUntil(UUID playerId) {
        return data.getLong("mutes." + playerId, 0L);
    }

    public void setBanUntil(UUID playerId, long epochMs) {
        data.set("tempbans." + playerId, epochMs);
    }

    public long getBanUntil(UUID playerId) {
        return data.getLong("tempbans." + playerId, 0L);
    }

    public void clearMute(UUID playerId) {
        data.set("mutes." + playerId, null);
    }

    public void clearBan(UUID playerId) {
        data.set("tempbans." + playerId, null);
    }

    public YamlConfiguration raw() {
        return data;
    }
}
