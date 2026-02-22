package de.fafasplugin;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardService {
    private final JavaPlugin plugin;
    private final EconomyService economyService;
    private BukkitTask task;

    public ScoreboardService(JavaPlugin plugin, EconomyService economyService) {
        this.plugin = plugin;
        this.economyService = economyService;
    }

    public void apply(Player player) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective("fafa", "dummy", ChatColor.GOLD + "Freunde-Server");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.getScore(ChatColor.YELLOW + "Online: " + ChatColor.WHITE + Bukkit.getOnlinePlayers().size()).setScore(4);
        objective.getScore(ChatColor.YELLOW + "Rang: " + ChatColor.WHITE + resolveLuckPermsRank(player)).setScore(3);
        objective.getScore(ChatColor.YELLOW + "Geld: " + ChatColor.WHITE + (int) economyService.balance(player)).setScore(2);
        objective.getScore(ChatColor.YELLOW + "Welt: " + ChatColor.WHITE + player.getWorld().getName()).setScore(1);
        player.setScoreboard(board);
    }

    private String resolveLuckPermsRank(Player player) {
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return "kein PAPI";
        }

        String rank = PlaceholderAPI.setPlaceholders(player, "%luckperms_primary_group_name%");
        if (rank == null || rank.isBlank() || rank.equals("%luckperms_primary_group_name%")) {
            return "kein Rang";
        }
        return ChatColor.stripColor(rank);
    }

    public void startTask() {
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> Bukkit.getOnlinePlayers().forEach(this::apply), 40L, 100L);
    }

    public void stopTask() {
        if (task != null) {
            task.cancel();
        }
    }
}
