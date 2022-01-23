package me.omegaweapondev.stylizer.utilities;

import me.clip.placeholderapi.PlaceholderAPI;
import me.omegaweapondev.stylizer.Stylizer;
import me.ou.library.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TablistManager {
  private final Stylizer plugin;
  private final FileConfiguration configFile;
  private final Player player;
  public final String playerPrefix;
  public final String playerSuffix;
  private final boolean isPlaceholderAPI;

  public TablistManager(final Stylizer plugin, final Player player) {
    this.plugin = plugin;
    this.player = player;
    configFile = plugin.getSettingsHandler().getConfigFile().getConfig();
    playerPrefix = (plugin.getChat().getPlayerPrefix(player) != null ? plugin.getChat().getPlayerPrefix(player) + " " : "");
    playerSuffix = (plugin.getChat().getPlayerSuffix(player) != null ? plugin.getChat().getPlayerSuffix(player) + " " : "");
    isPlaceholderAPI = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
  }

  public void tablistHeaderFooter() {
    if(!configFile.getBoolean("Tablist.Enabled")) {
      return;
    }

    StringBuilder header = new StringBuilder();
    StringBuilder footer = new StringBuilder();

    if(isPlaceholderAPI) {
      for(String headerMessage : configFile.getStringList("Tablist.Tablist_Header")) {
        header.append(headerMessage.replace("%player%", playerPrefix + playerNameColour(player) + player.getName())).append("\n");
      }

      player.setPlayerListHeader(PlaceholderAPI.setPlaceholders(player, Utilities.colourise(header.toString())));

      for(String footerMessage : configFile.getStringList("Tablist.Tablist_Footer")) {
        footer.append(footerMessage.replace("%player%", playerPrefix + playerNameColour(player) + player.getName())).append("\n");
      }

      player.setPlayerListFooter(PlaceholderAPI.setPlaceholders(player, Utilities.colourise(footer.toString())));
      return;
    }


    for(String headerMessage : configFile.getStringList("Tablist.Tablist_Header")) {
      header.append(headerMessage.replace("%player%", playerPrefix + playerNameColour(player) + player.getName())).append("\n");
    }

    player.setPlayerListHeader(Utilities.colourise(header.toString()));


    for(String footerMessage : configFile.getStringList("Tablist.Tablist_Footer")) {
      footer.append(footerMessage.replace("%player%", playerPrefix + playerNameColour(player) + player.getName())).append("\n");
    }

    player.setPlayerListFooter(Utilities.colourise(footer.toString()));
  }

  public void tablistPlayerName() {
    if(!configFile.getBoolean("Tablist.Enabled") && !configFile.getBoolean("Tablist.Player_Name_Formats.Enabled")) {
      return;
    }

    if(!configFile.getBoolean("Tablist.Player_Name_Formats.Group_Formats.Enabled")) {
      player.setPlayerListName(setFormat(player, configFile.getString("Tablist.Player_Name_Formats.Default_Format")));;
      return;
    }

    for(String groupName : configFile.getStringList("Tablist.Player_Name_Formats.Group_Formats.Groups")) {
      if(Utilities.checkPermissions(player, false, "stylizer.tablist.groups." + groupName)) {
        player.setPlayerListName(setFormat(player, configFile.getString("Tablist.Player_Name_Formats.Group_Formats.Groups." + groupName)));
        return;
      }
    }
  }

  public String groupNameColour(final String groupName) {
    if(!configFile.getBoolean("Group_Name_Colour.Enabled")) {
      return playerNameColour(player);
    }

    if(configFile.getConfigurationSection("Group_Name_Colour.Groups").getKeys(false).size() == 0) {
      return playerNameColour(player);
    }

    return configFile.getString("Group_Name_Colour.Groups." + groupName);
  }

  public String playerNameColour(final Player player) {
    if(!plugin.getSettingsHandler().getPlayerData().getConfig().isSet(player.getUniqueId() + ".Name_Colour")) {
      return configFile.getString("Default_Name_Colour", "#fff954");
    }

    return plugin.getSettingsHandler().getPlayerData().getConfig().getString(player.getUniqueId() + ".Name_Colour");
  }

  private String setFormat(@NotNull final Player player, @NotNull String tablistFormat) {
    String finalFormat = tablistFormat.replace("%playername%", player.getName());
    finalFormat = finalFormat.replace("%displayname%", player.getDisplayName());
    finalFormat = finalFormat.replace("%prefix%", playerPrefix);
    finalFormat = finalFormat.replace("%suffix%", playerSuffix);

    if(isPlaceholderAPI) {
      return Utilities.colourise(PlaceholderAPI.setPlaceholders(player, finalFormat));
    }

    return Utilities.colourise(finalFormat);
  }
}
