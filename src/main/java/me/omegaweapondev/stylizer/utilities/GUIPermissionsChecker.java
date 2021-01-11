package me.omegaweapondev.stylizer.utilities;

import me.omegaweapondev.stylizer.Stylizer;
import me.ou.library.Utilities;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class GUIPermissionsChecker {
  private final Stylizer plugin;
  private final MessageHandler messageHandler;
  private final FileConfiguration configFile;
  private final FileConfiguration playerData;

  private final Player player;
  private final String name;
  private final String colour;

  public GUIPermissionsChecker(final Stylizer plugin, final Player player, final String name, final String colour) {
    this.plugin = plugin;
    this.player = player;
    this.name = name;
    this.colour = colour;

    messageHandler = new MessageHandler(plugin, plugin.getMessagesFile().getConfig());
    configFile = plugin.getConfigFile().getConfig();
    playerData = plugin.getPlayerData().getConfig();
  }

  public void nameColourPermsCheck() {

    if(configFile.getBoolean("Per_Name_Colour_Permissions")) {
      perNameColourPerms();
      return;
    }

    if(!Utilities.checkPermissions(player, true, "stylizer.namecolour.colours", "stylizer.admin")) {
      Utilities.message(player, messageHandler.string("No_Permission", "#570000Sorry, you do not have permission to do that."));
      return;
    }

    player.setDisplayName(Utilities.colourise(colour + player.getName() + "&r"));

    playerData.set(player.getUniqueId().toString() + ".Name_Colour", colour);
    plugin.getPlayerData().saveConfig();

    Utilities.message(player, messageHandler.string("Name_Colour_Applied", "#14abc9Your name colour has been changed to: %namecolour%").replace("%namecolour%", colour + player.getName()));
  }

  private void perNameColourPerms() {
    if(!Utilities.checkPermissions(player, true, "stylizer.namecolour.colour." + name.replace(" ", "").toLowerCase(), "stylizer.namecolour.colour.all", "stylizer.admin")) {
      Utilities.message(player, messageHandler.string("No_Permission", "#570000Sorry, you do not have permission to do that."));
      return;
    }

    player.setDisplayName(Utilities.colourise(colour + player.getName() + "&r"));

    if(!playerData.isConfigurationSection(player.getUniqueId().toString())) {
      plugin.getPlayerData().getConfig().createSection(player.getUniqueId().toString());
    }

    playerData.set(player.getUniqueId().toString() + ".Name_Colour", colour);
    plugin.getPlayerData().saveConfig();

    Utilities.message(player, messageHandler.string("Name_Colour_Applied", "#14abc9Your name colour has been changed to: %namecolour%").replace("%namecolour%", colour + player.getName()));
  }
}
