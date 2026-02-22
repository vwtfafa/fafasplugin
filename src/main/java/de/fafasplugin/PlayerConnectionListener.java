package de.fafasplugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {
    private final ScoreboardService scoreboardService;
    private final ModerationService moderationService;

    public PlayerConnectionListener(ScoreboardService scoreboardService, ModerationService moderationService) {
        this.scoreboardService = scoreboardService;
        this.moderationService = moderationService;
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (moderationService.isTempBanned(event.getUniqueId())) {
            long mins = Math.max(1, moderationService.remainingBanMs(event.getUniqueId()) / 60_000L);
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, "Tempban aktiv. Restzeit: " + mins + " min.");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.joinMessage(Component.text("+ ", NamedTextColor.GREEN).append(Component.text(event.getPlayer().getName(), NamedTextColor.YELLOW)));
        scoreboardService.apply(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.quitMessage(Component.text("- ", NamedTextColor.RED).append(Component.text(event.getPlayer().getName(), NamedTextColor.YELLOW)));
    }
}
