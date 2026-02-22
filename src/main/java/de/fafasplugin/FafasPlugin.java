package de.fafasplugin;

import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class FafasPlugin extends JavaPlugin {
    private DataStore dataStore;
    private HomeService homeService;
    private EconomyService economyService;
    private TpaService tpaService;
    private ModerationService moderationService;
    private PartyService partyService;
    private ScoreboardService scoreboardService;
    private EventService eventService;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        dataStore = new DataStore(this);
        dataStore.load();

        homeService = new HomeService(this, dataStore);
        economyService = new EconomyService(this, dataStore);
        tpaService = new TpaService(this);
        moderationService = new ModerationService(this, dataStore);
        partyService = new PartyService();
        scoreboardService = new ScoreboardService(this, economyService);
        eventService = new EventService(this);

        var commandHandler = new PluginCommandHandler(this, homeService, economyService, tpaService, moderationService, partyService, eventService);
        for (String cmd : PluginCommandHandler.HANDLED_COMMANDS) {
            Objects.requireNonNull(getCommand(cmd), "command missing in plugin.yml: " + cmd).setExecutor(commandHandler);
            Objects.requireNonNull(getCommand(cmd), "command missing in plugin.yml: " + cmd).setTabCompleter(commandHandler);
        }

        Bukkit.getPluginManager().registerEvents(new PlayerConnectionListener(scoreboardService, moderationService), this);
        Bukkit.getPluginManager().registerEvents(new ChatFilterListener(this, moderationService), this);
        Bukkit.getPluginManager().registerEvents(new GuiListener(), this);

        scoreboardService.startTask();
        eventService.startRotationTask();
        moderationService.startCleanupTask();

        getLogger().info("FafasPlugin enabled with extended systems.");
    }

    @Override
    public void onDisable() {
        if (scoreboardService != null) {
            scoreboardService.stopTask();
        }
        if (eventService != null) {
            eventService.stopTasks();
        }
        if (moderationService != null) {
            moderationService.stopTasks();
        }
        if (dataStore != null) {
            dataStore.save();
        }
        getLogger().info("FafasPlugin disabled.");
    }
}
