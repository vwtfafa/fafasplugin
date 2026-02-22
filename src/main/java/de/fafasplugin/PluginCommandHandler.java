package de.fafasplugin;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginCommandHandler implements CommandExecutor, TabCompleter {
    public static final List<String> HANDLED_COMMANDS = List.of(
            "fafa", "workbench", "ec", "anvil", "sethome", "home", "spawn", "tpa", "tpaccept", "tpdeny",
            "mute", "tempban", "money", "pay", "shop", "eventstart", "party", "servermenu", "dailyreward", "minigame"
    );

    private final JavaPlugin plugin;
    private final HomeService homes;
    private final EconomyService economy;
    private final TpaService tpa;
    private final ModerationService moderation;
    private final PartyService parties;
    private final EventService events;

    public PluginCommandHandler(JavaPlugin plugin, HomeService homes, EconomyService economy, TpaService tpa,
                                ModerationService moderation, PartyService parties, EventService events) {
        this.plugin = plugin;
        this.homes = homes;
        this.economy = economy;
        this.tpa = tpa;
        this.moderation = moderation;
        this.parties = parties;
        this.events = events;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = command.getName().toLowerCase(Locale.ROOT);
        if (cmd.equals("fafa")) {
            sender.sendMessage("§aFafasPlugin aktiv. Nutze /servermenu oder /shop.");
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cNur Spieler.");
            return true;
        }

        switch (cmd) {
            case "workbench" -> player.openWorkbench(null, true);
            case "ec" -> player.openInventory(player.getEnderChest());
            case "anvil" -> player.openAnvil(null, true);
            case "sethome" -> {
                String name = args.length > 0 ? args[0].toLowerCase(Locale.ROOT) : "main";
                homes.setHome(player, name);
                player.sendMessage("§aHome gesetzt: " + name);
            }
            case "home" -> {
                String name = args.length > 0 ? args[0].toLowerCase(Locale.ROOT) : "main";
                if (homes.teleportHome(player, name)) {
                    player.sendMessage("§aTeleport zu Home " + name);
                } else {
                    player.sendMessage("§cHome nicht gefunden.");
                }
            }
            case "spawn" -> {
                homes.teleportSpawn(player);
                player.sendMessage("§aTeleport zum Spawn.");
            }
            case "tpa" -> {
                if (args.length < 1) {
                    player.sendMessage("§c/tpa <spieler>");
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target == null) {
                    player.sendMessage("§cSpieler offline.");
                    return true;
                }
                tpa.request(player, target);
                player.sendMessage("§aTPA gesendet.");
                target.sendMessage("§e" + player.getName() + " möchte sich zu dir teleportieren. /tpaccept oder /tpdeny");
            }
            case "tpaccept" -> {
                Player requester = tpa.accept(player);
                if (requester == null) {
                    player.sendMessage("§cKeine Anfrage.");
                } else {
                    player.sendMessage("§aAnfrage akzeptiert.");
                    requester.sendMessage("§aDeine TPA wurde akzeptiert.");
                }
            }
            case "tpdeny" -> {
                Player requester = tpa.deny(player);
                if (requester == null) {
                    player.sendMessage("§cKeine Anfrage.");
                } else {
                    player.sendMessage("§eAnfrage abgelehnt.");
                    requester.sendMessage("§cDeine TPA wurde abgelehnt.");
                }
            }
            case "mute" -> {
                if (!player.hasPermission("fafasplugin.mod")) {
                    player.sendMessage("§cKeine Rechte.");
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage("§c/mute <spieler> <minuten>");
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target == null) return true;
                moderation.mute(target, Integer.parseInt(args[1]));
                Bukkit.broadcastMessage("§6" + target.getName() + " wurde gemutet.");
            }
            case "tempban" -> {
                if (!player.hasPermission("fafasplugin.mod")) {
                    player.sendMessage("§cKeine Rechte.");
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage("§c/tempban <spieler> <minuten>");
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target == null) return true;
                moderation.tempban(target, Integer.parseInt(args[1]));
            }
            case "money" -> player.sendMessage("§aKontostand: §e" + economy.balance(player));
            case "pay" -> {
                if (args.length < 2) {
                    player.sendMessage("§c/pay <spieler> <betrag>");
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target == null) return true;
                double amount = Double.parseDouble(args[1]);
                if (economy.pay(player, target, amount)) {
                    player.sendMessage("§aBezahlt: " + amount);
                    target.sendMessage("§aErhalten: " + amount);
                } else {
                    player.sendMessage("§cZahlung fehlgeschlagen.");
                }
            }
            case "shop" -> {
                Inventory inv = Bukkit.createInventory(null, 9, "Server-Shop");
                inv.setItem(2, new ItemStack(Material.GOLDEN_APPLE, 1));
                inv.setItem(4, new ItemStack(Material.ENDER_PEARL, 4));
                inv.setItem(6, new ItemStack(Material.EXPERIENCE_BOTTLE, 8));
                player.openInventory(inv);
                economy.buy(player, 5.0);
            }
            case "eventstart" -> {
                if (!player.hasPermission("fafasplugin.event")) return true;
                String mode = args.length > 0 ? args[0] : "drop";
                if (mode.equalsIgnoreCase("drop")) {
                    events.startDropEvent(player.getWorld());
                } else if (mode.equalsIgnoreCase("kiste")) {
                    events.spawnCrate(player.getWorld());
                }
                events.announceBossbar("Event läuft: " + mode);
            }
            case "party" -> {
                if (args.length < 2) {
                    player.sendMessage("§c/party <invite|accept> <spieler>");
                    return true;
                }
                if (args[0].equalsIgnoreCase("invite")) {
                    Player target = Bukkit.getPlayerExact(args[1]);
                    if (target == null) return true;
                    parties.invite(player.getUniqueId(), target.getUniqueId());
                    player.sendMessage("§aParty-Einladung gesendet.");
                    target.sendMessage("§eParty-Einladung von " + player.getName() + " via /party accept " + player.getName());
                } else if (args[0].equalsIgnoreCase("accept")) {
                    var leader = parties.accept(player.getUniqueId());
                    player.sendMessage(leader == null ? "§cKeine Einladung." : "§aParty beigetreten.");
                }
            }
            case "servermenu" -> {
                Inventory menu = Bukkit.createInventory(null, 9, "Server-Menü");
                menu.setItem(1, new ItemStack(Material.CRAFTING_TABLE));
                menu.setItem(4, new ItemStack(Material.ENDER_CHEST));
                menu.setItem(7, new ItemStack(Material.CHEST));
                player.openInventory(menu);
            }
            case "dailyreward" -> {
                economy.setBalance(player, economy.balance(player) + 25.0);
                player.sendMessage("§aDaily Reward: +25 Coins");
            }
            case "minigame" -> {
                if (args.length < 1 || !args[0].equalsIgnoreCase("join")) {
                    player.sendMessage("§c/minigame join");
                    return true;
                }
                World world = Bukkit.getWorld("fafa_minigames");
                if (world == null) {
                    world = Bukkit.createWorld(new WorldCreator("fafa_minigames"));
                }
                player.teleport(world.getSpawnLocation());
                player.setGameMode(GameMode.ADVENTURE);
                player.sendMessage("§aWillkommen in der Minigame-Welt!");
            }
            default -> {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("eventstart") && args.length == 1) {
            return List.of("drop", "kiste");
        }
        if (Set.of("tpa", "mute", "tempban", "pay").contains(command.getName().toLowerCase(Locale.ROOT)) && args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(n -> n.toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT))).toList();
        }
        return List.of();
    }
}
