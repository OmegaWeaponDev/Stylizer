package me.omegaweapondev.stylizer.events;

import me.ou.library.Utilities;
import me.ou.library.menus.MenuCreator;
import me.omegaweapondev.stylizer.Stylizer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class MenuListener implements Listener {
  final Stylizer plugin;

  public MenuListener(final Stylizer plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onClick(InventoryClickEvent e) {
    Player player = (Player) e.getWhoClicked();
    UUID playerUUID = player.getUniqueId();
    UUID inventoryUUID = MenuCreator.getOpenInventories().get(playerUUID);

    if(!(e.getWhoClicked() instanceof Player)) {
      return;
    }

    ItemStack clickedItem = e.getCurrentItem();
    if(clickedItem == null || clickedItem.getType() == Material.AIR) {
      return;
    }

    if(e.getClick().equals(ClickType.DROP)) {
      e.setCancelled(true);
      return;
    }


    if(clickedItem.getItemMeta().getDisplayName().equals(Utilities.componentSerializerFromString("#FF4A4AInvalid Item"))) {
      return;
    }

    if(inventoryUUID != null) {
      e.setCancelled(true);
      MenuCreator gui = MenuCreator.getInventoriesByUUID().get(inventoryUUID);
      MenuCreator.inventoryAction action = gui.getActions().get(e.getSlot());

      if(action != null) {
        action.click(player);
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onClose(InventoryCloseEvent e) {
    Player player = (Player) e.getPlayer();
    UUID playerUUID = player.getUniqueId();

    MenuCreator.getOpenInventories().remove(playerUUID);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerQuit(PlayerQuitEvent e) {
    Player player = e.getPlayer();
    UUID playerUUID = player.getUniqueId();

    MenuCreator.getOpenInventories().remove(playerUUID);
  }
}
