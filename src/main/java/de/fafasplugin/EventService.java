package de.fafasplugin;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class EventService {
    private final JavaPlugin plugin;
    private BukkitTask rotationTask;
    private BukkitTask bossbarTask;

    public EventService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void startRotationTask() {
        rotationTask = Bukkit.getScheduler().runTaskTimer(plugin, () ->
                Bukkit.broadcast(Component.text("Rotation: In 5 Minuten startet ein Drop-Event!")), 20L * 300L, 20L * 1200L);
    }

    public void startDropEvent(World world) {
        Location drop = world.getSpawnLocation().add(0, 2, 0);
        world.dropItemNaturally(drop, new ItemStack(Material.DIAMOND, 3));
        world.dropItemNaturally(drop, new ItemStack(Material.GOLDEN_APPLE, 2));
        Bukkit.broadcast(Component.text("Drop-Event aktiv am Spawn!"));
    }

    public void spawnCrate(World world) {
        Location location = world.getSpawnLocation().add(2, 0, 2);
        world.getBlockAt(location).setType(Material.CHEST);
        Bukkit.broadcast(Component.text("Eine Event-Kiste wurde am Spawn platziert!"));
    }

    public void announceBossbar(String message) {
        BossBar bossBar = BossBar.bossBar(Component.text(message), 1.0f, BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showBossBar(bossBar);
        }
        bossbarTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.hideBossBar(bossBar);
            }
        }, 20L * 10L);
    }

    public void stopTasks() {
        if (rotationTask != null) {
            rotationTask.cancel();
        }
        if (bossbarTask != null) {
            bossbarTask.cancel();
        }
    }
}
