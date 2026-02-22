package de.fafasplugin;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.plugin.java.JavaPlugin;

public class ModerationService {
    private static final Set<String> BLOCKED_WORDS = Set.of("idiot", "noob");

    private final JavaPlugin plugin;
    private final DataStore dataStore;
    private BukkitTask cleanupTask;

    public ModerationService(JavaPlugin plugin, DataStore dataStore) {
        this.plugin = plugin;
        this.dataStore = dataStore;
    }

    public void mute(Player target, int minutes) {
        long until = System.currentTimeMillis() + minutes * 60_000L;
        dataStore.setMuteUntil(target.getUniqueId(), until);
        dataStore.save();
    }

    public void tempban(Player target, int minutes) {
        long until = System.currentTimeMillis() + minutes * 60_000L;
        dataStore.setBanUntil(target.getUniqueId(), until);
        dataStore.save();
        target.kickPlayer("Du bist temporär gebannt für " + minutes + " Minuten.");
    }

    public boolean isMuted(UUID playerId) {
        return dataStore.getMuteUntil(playerId) > System.currentTimeMillis();
    }

    public boolean isTempBanned(UUID playerId) {
        return dataStore.getBanUntil(playerId) > System.currentTimeMillis();
    }

    public long remainingBanMs(UUID playerId) {
        return Math.max(0, dataStore.getBanUntil(playerId) - System.currentTimeMillis());
    }

    public boolean containsBlockedWord(String message) {
        return containsBlockedWordStatic(message);
    }

    public static boolean containsBlockedWordStatic(String message) {
        String normalized = message.toLowerCase(Locale.ROOT);
        return BLOCKED_WORDS.stream().anyMatch(normalized::contains);
    }

    public void startCleanupTask() {
        cleanupTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!isMuted(player.getUniqueId()) && dataStore.getMuteUntil(player.getUniqueId()) > 0) {
                    dataStore.clearMute(player.getUniqueId());
                }
                if (!isTempBanned(player.getUniqueId()) && dataStore.getBanUntil(player.getUniqueId()) > 0) {
                    dataStore.clearBan(player.getUniqueId());
                }
            }
            dataStore.save();
        }, 20L * 60L, 20L * 60L);
    }

    public void stopTasks() {
        if (cleanupTask != null) {
            cleanupTask.cancel();
        }
    }
}
