package me.omegaweapondev.stylizer.menus;

import me.omegaweapondev.stylizer.Stylizer;
import me.omegaweapondev.stylizer.utilities.GUIPermissionsChecker;
import me.omegaweapondev.stylizer.utilities.MessageHandler;
import me.ou.library.Utilities;
import me.ou.library.builders.ItemBuilder;
import me.ou.library.menus.MenuCreator;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class ChatColours extends MenuCreator {
  private final Stylizer plugin;
  private final MessageHandler messageHandler;
  private ItemBuilder itemBuilder;
  private final FileConfiguration configFile;

  public ChatColours(final Stylizer plugin) {
    super(4, plugin.getSettingsHandler().getMessagesFile().getConfig().getString("Chat_Colour_GUI.GUI_Title"), "#6928f7&lChatColours");
    this.plugin = plugin;
    configFile = plugin.getSettingsHandler().getConfigFile().getConfig();
    messageHandler = new MessageHandler(plugin, plugin.getSettingsHandler().getMessagesFile().getConfig());

    int slot = -2;
    for(String itemName : configFile.getConfigurationSection("Chat_Colour_Items").getKeys(false)) {
      if(slot++ > 33) {
        Utilities.logWarning(true, "You can only have 33 colours in the Chat Colour GUI!");
        return;
      }

      createItem(slot + 1, configFile.getString("Chat_Colour_Items." + itemName + ".Item"), itemName,configFile.getString("Chat_Colour_Items." + itemName + ".Colour"));
    }

    setItem(34, createItemStack("SPONGE", Utilities.colourise("#570000Current"), Utilities.colourise(Arrays.asList("#ff4a4aClick here to view", "#ff4a4ayour current chat colour"))), player -> {
      Utilities.message(player, messageHandler.string("Current_Chat_Colour", "#14abc9Your name colour has been changed to: %chatcolour%").replace("%chatcolour%", plugin.getSettingsHandler().getPlayerData().getConfig().getString(player.getUniqueId().toString() + ".Chat_Colour")));
    });
  }

  private void createItem(final Integer slot, final String material, final String name, final String colour) {
    setItem(slot, createItemStack(material, name, messageHandler.stringList("Chat_Colour_GUI.Colour_Lore", Arrays.asList("&cClick here to change", "&cyour chat colour to", colour + name))), player -> {
      final GUIPermissionsChecker permChecker = new GUIPermissionsChecker(plugin, player, name, colour);

      permChecker.chatColourPermsCheck();
    });
  }

  private ItemStack createItemStack(final String material, final String name, final List<String> lore) {
    itemBuilder = new ItemBuilder(Material.getMaterial(material.toUpperCase()));
    return itemBuilder.checkInvalidMaterial(material, name, lore);
  }
}
