package me.omegaweapondev.stylizer.menus;

import me.omegaweapondev.stylizer.Stylizer;
import me.omegaweapondev.stylizer.utilities.GUIPermissionsChecker;
import me.omegaweapondev.stylizer.utilities.ItemCreator;
import me.omegaweapondev.stylizer.utilities.MessageHandler;
import me.ou.library.Utilities;
import me.ou.library.menus.MenuCreator;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NameColours extends MenuCreator {
  private final Stylizer plugin;
  private final MessageHandler messageHandler;
  private ItemCreator itemCreator;
  private final FileConfiguration configFile;

  public NameColours(final Stylizer plugin) {
    super(4, plugin.getMessagesFile().getConfig().getString("Name_Colour_GUI.GUI_Title"), "#6928f7&lNameColours");
    this.plugin = plugin;
    configFile = plugin.getConfigFile().getConfig();
    messageHandler = new MessageHandler(plugin, plugin.getMessagesFile().getConfig());

    int slot = -2;

    for(String itemName : configFile.getConfigurationSection("Items").getKeys(false)) {
      if(slot++ > 33) {
        Utilities.logWarning(true, "You can only have 33 colours in the GUI!");
        return;
      }

      createItem(slot + 1, configFile.getString("Items." + itemName + ".Item"), itemName,configFile.getString("Items." + itemName + ".Colour"));
    }

    setItem(34, createItemStack("SPONGE", Utilities.colourise("#570000Current"), Utilities.colourise(Arrays.asList("#ff4a4aClick here to view", "#ff4a4ayour current name colour"))), player -> {
      Utilities.message(player, messageHandler.string("Current_Name_Colour", "#14abc9Your name colour has been changed to: %namecolour%").replace("%namecolour%", player.getDisplayName()));
    });
  }

  private void createItem(final Integer slot, final String material, final String name, final String colour) {

    setItem(slot, createItemStack(material, Utilities.colourise(colour + name), Utilities.colourise(loreMessage(messageHandler.stringList("Name_Colour_GUI.Colour_Lore", Arrays.asList("&cClick here to change", "&cyour name colour to", colour + name)), colour + name))), player -> {
      final GUIPermissionsChecker permChecker = new GUIPermissionsChecker(plugin, player, name, colour);

      permChecker.nameColourPermsCheck();
    });
  }

  private List<String> loreMessage(final List<String> lore, String name) {
    List<String> formattedLore = new ArrayList<>();

    for(String message : lore) {
      formattedLore.add(message.replace("%namecolour%", name));
    }

    return formattedLore;
  }

  private ItemStack createItemStack(final String material, final String name, final List<String> lore) {
    itemCreator = new ItemCreator(Material.getMaterial(material.toUpperCase()));
    return itemCreator.checkInvalidMaterial(material, name, lore);
  }
}
