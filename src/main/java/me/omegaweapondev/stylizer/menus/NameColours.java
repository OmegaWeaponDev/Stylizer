package me.omegaweapondev.stylizer.menus;

import me.omegaweapondev.stylizer.Stylizer;
import me.omegaweapondev.stylizer.utilities.GUIPermissionsChecker;
import me.omegaweapondev.stylizer.utilities.MessageHandler;
import me.ou.library.Utilities;
import me.ou.library.builders.ItemBuilder;
import me.ou.library.libs.net.kyori.adventure.text.Component;
import me.ou.library.libs.net.kyori.adventure.text.TextComponent;
import me.ou.library.libs.net.kyori.adventure.text.TextReplacementConfig;
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
  private ItemBuilder itemBuilder;
  private final FileConfiguration configFile;

  public NameColours(final Stylizer plugin) {
    super(4, plugin.getSettingsHandler().getMessagesFile().getConfig().getString("Name_Colour_GUI.GUI_Title"), Utilities.componentSerializerFromString("#6928f7&lNameColours"));
    this.plugin = plugin;
    configFile = plugin.getSettingsHandler().getConfigFile().getConfig();
    messageHandler = new MessageHandler(plugin, plugin.getSettingsHandler().getMessagesFile().getConfig());

    int slot = -2;

    for(String itemName : configFile.getConfigurationSection("Name_Colour_Items").getKeys(false)) {
      if(slot++ > 33) {
        Utilities.logWarning(true, "You can only have 33 colours in the Name Colour GUI!");
        return;
      }

      createItem(slot + 1, configFile.getString("Name_Colour_Items." + itemName + ".Item"), itemName,configFile.getString("Name_Colour_Items." + itemName + ".Colour"));
    }

    List<String> stringLoreList = new ArrayList<>();
    stringLoreList.add("#ff4a4aClick here to view");
    stringLoreList.add("#ff4a4ayour current name colour");

    setItem(34, createItemStack(Utilities.componentDeserializer(stringLoreList)), player -> {
      Utilities.message(player, messageHandler.string("Current_Name_Colour", "#14abc9Your name colour is currently set to: %namecolour%")
              .replace("%namecolour%", plugin.getSettingsHandler().getPlayerData().getConfig().getString(player.getUniqueId() + ".Name_Colour", "No Colour Set")));
    });
  }

  private void createItem(final Integer slot, final String material, final String name, final String colour) {
    final List<String> configLore = messageHandler.stringList("Name_Colour_GUI.Colour_Lore", Arrays.asList("#ff4a4aClick here to change", "#ff4a4ayour name colour to", colour + name));
    setItem(slot, createItemStack(material, Utilities.componentDeserializer(colour + name), colour, loreMessage(Utilities.componentDeserializer(configLore), colour + name)), player -> {
      final GUIPermissionsChecker permissionsChecker = new GUIPermissionsChecker(plugin, player, name, colour);
      permissionsChecker.chatColourPermsCheck();
    });
  }

  private List<TextComponent> loreMessage(final List<TextComponent> lore, String name) {
    List<TextComponent> formattedLore = new ArrayList<>();
    TextReplacementConfig colourNameReplace = TextReplacementConfig.builder().match("%namecolour%").replacement(name).build();

    for(TextComponent message : lore) {
      formattedLore.add(Component.textOfChildren(message.replaceText(colourNameReplace)));
    }

    return formattedLore;
  }

  private ItemStack createItemStack(final String material, final TextComponent name, final String color, final List<TextComponent> lore) {
    itemBuilder = new ItemBuilder(Material.getMaterial(material.toUpperCase()));
    return itemBuilder.checkInvalidMaterial(material, Utilities.componentSerializer(Component.text(color + name)), lore);
  }

  private ItemStack createItemStack(final List<TextComponent> lore) {
    itemBuilder = new ItemBuilder(Material.getMaterial("SPONGE".toUpperCase()));
    return itemBuilder.checkInvalidMaterial("SPONGE", Utilities.componentSerializer(Component.text("#570000Current")), lore);
  }
}
