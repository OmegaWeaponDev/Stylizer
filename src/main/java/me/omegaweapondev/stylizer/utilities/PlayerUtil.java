package me.omegaweapondev.stylizer.utilities;

import me.ou.library.Utilities;
import me.omegaweapondev.stylizer.Stylizer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class PlayerUtil {
  private final Stylizer plugin;
  private final Player player;
  private final FileConfiguration configFile;
  private final FileConfiguration userData;

  public PlayerUtil(final Stylizer plugin, final Player player) {
    this.plugin = plugin;
    this.player = player;

    configFile = plugin.getSettingsHandler().getConfigFile().getConfig();
    userData = plugin.getSettingsHandler().getPlayerData().getConfig();
  }

  public String getNameColour() {
    if(userData.getString(player.getUniqueId() + ".Name_Colour") != null) {
      return userData.getString(player.getUniqueId() + ".Name_Colour");
    }

    if(!configFile.getBoolean("Group_Name_Colour.Enabled")) {
      return configFile.getString("Default_Name_Colour");
    }

    for(String groupName : configFile.getConfigurationSection("Group_Name_Colour.Groups").getKeys(false)) {
      if(Utilities.checkPermission(player, false, "stylizer.namecolour.groups." + groupName.toLowerCase())) {
        return configFile.getString("Group_Name_Colour.Groups." + groupName);
      }
    }

    return configFile.getString("Default_Name_Colour");
  }

  public String getChatColour() {
    if(userData.getString(player.getUniqueId() + ".Chat_Colour") != null) {
      return userData.getString(player.getUniqueId() + ".Chat_Colour");
    }

    if(!configFile.getBoolean("Group_Chat_Colour.Enabled")) {
      return configFile.getString("Default_Chat_Colour");
    }

    for(String groupName : configFile.getConfigurationSection("Group_Chat_Colour.Groups").getKeys(false)) {
      if(Utilities.checkPermission(player, false, "stylizer.chatcolour.groups." + groupName.toLowerCase())) {
        return configFile.getString("Group_Chat_Colour.Groups." + groupName);
      }
    }

    return configFile.getString("Default_Chat_Colour");
  }

  public String getPrefix() {
    return (plugin.getChat().getPlayerPrefix(player) != null ? plugin.getChat().getPlayerPrefix(player) + " " : "");
  }

  public String getSuffix() {
    return (plugin.getChat().getPlayerSuffix(player) != null ? " " + plugin.getChat().getPlayerSuffix(player) + " " : "");
  }
}
