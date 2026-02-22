package de.fafasplugin;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatFilterListener implements Listener {
    private final JavaPlugin plugin;
    private final ModerationService moderationService;

    public ChatFilterListener(JavaPlugin plugin, ModerationService moderationService) {
        this.plugin = plugin;
        this.moderationService = moderationService;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (moderationService.isMuted(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cDu bist gemutet.");
            return;
        }

        if (moderationService.containsBlockedWord(event.getMessage())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cBitte freundliche Sprache verwenden.");
            plugin.getServer().broadcast(Component.text("§eChatfilter hat eine Nachricht blockiert."));
        }
    }
}
