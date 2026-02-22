package de.fafasplugin;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GuiListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().title().toString().contains("Server-Menü")) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) {
                return;
            }
            if (event.getCurrentItem().getType() == Material.CRAFTING_TABLE) {
                event.getWhoClicked().closeInventory();
                event.getWhoClicked().sendMessage("§aNutze /workbench für schnelles Crafting.");
            }
            if (event.getCurrentItem().getType() == Material.ENDER_CHEST) {
                event.getWhoClicked().sendMessage("§aNutze /ec um deine Enderchest zu öffnen.");
            }
            if (event.getCurrentItem().getType() == Material.CHEST) {
                event.getWhoClicked().getInventory().addItem(new ItemStack(Material.BREAD, 3));
            }
        }
    }
}
