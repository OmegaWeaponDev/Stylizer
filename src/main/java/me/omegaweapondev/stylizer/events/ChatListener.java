package me.omegaweapondev.stylizer.events;

import me.clip.placeholderapi.PlaceholderAPI;
import me.omegaweapondev.stylizer.Stylizer;
import me.ou.library.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatListener implements Listener {
  private final Stylizer plugin;
  private final FileConfiguration configFile;
  private final FileConfiguration userData;
  private final FileConfiguration chatlog;

  public ChatListener(final Stylizer plugin) {
    this.plugin = plugin;
    configFile = plugin.getSettingsHandler().getConfigFile().getConfig();
    chatlog = plugin.getSettingsHandler().getChatLog().getConfig();
    userData = plugin.getSettingsHandler().getPlayerData().getConfig();
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerChat(AsyncPlayerChatEvent chatEvent) {
    final Player player = chatEvent.getPlayer();
    final String rawMessage = chatEvent.getMessage();

    if(!configFile.getBoolean("Chat_Settings.Enabled")) {
      return;
    }

    formatChat(player, rawMessage, chatEvent);
  }

  private void formatChat(final Player player, final String rawMessage, final AsyncPlayerChatEvent chatEvent) {
    long currentTime = System.currentTimeMillis();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    Date date = new Date(currentTime);
    String chatLogTime = simpleDateFormat.format(date);

    for(String groupNames : configFile.getConfigurationSection("Chat_Settings.Chat_Formats.Group_Formats.Groups").getKeys(false)) {
      String configFormat = configFile.getString("Chat_Settings.Chat_Formats.Group_Formats.Groups." + groupNames)
        .replace("%prefix%", (plugin.getChat().getPlayerPrefix(player) != null ? plugin.getChat().getPlayerPrefix(player) : ""))
        .replace("%suffix%", (plugin.getChat().getPlayerSuffix(player) != null ? plugin.getChat().getPlayerSuffix(player) : ""))
        .replace("%displayname%", getNameColour(player) + "%s")
        .replace("%message%", getChatColour(player) + "%s");

      if(Utilities.checkPermission(player, false, "stylizer.chat.groups." + groupNames.toLowerCase())) {
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
          String format = PlaceholderAPI.setPlaceholders(player, Utilities.colourise(configFormat));
          chatEvent.setFormat(format);

          if(configFile.getBoolean("Chat_Settings.Log_Chat_Messages")) {
            chatlog.set("Chat_Log." + chatLogTime, player.getName() + " >> " + rawMessage);
            plugin.getSettingsHandler().getChatLog().saveConfig();
          }
          return;
        }

        String format = Utilities.colourise(configFormat);

        chatEvent.setFormat(format);

        if(configFile.getBoolean("Chat_Settings.Log_Chat_Messages")) {
          chatlog.set("Chat_Log." + chatLogTime, player.getName() + " >> " + rawMessage);
          plugin.getSettingsHandler().getChatLog().saveConfig();
        }
        return;
      }
    }

    if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      String configFormat = configFile.getString(PlaceholderAPI.setPlaceholders(player, "Chat_Settings.Chat_Formats.Default_Format")
        .replace("%prefix%", (plugin.getChat().getPlayerPrefix(player) != null ? plugin.getChat().getPlayerPrefix(player) : ""))
        .replace("%suffix%", (plugin.getChat().getPlayerSuffix(player) != null ? plugin.getChat().getPlayerSuffix(player) : ""))
        .replace("%displayname%", getNameColour(player) + "%s")
        .replace("%message%", getChatColour(player) + "%s")
      );

      chatEvent.setFormat(Utilities.colourise(configFormat));

      if(configFile.getBoolean("Chat_Settings.Log_Chat_Messages")) {
        chatlog.set("Chat_Log." + chatLogTime, player.getName() + " >> " + rawMessage);
        plugin.getSettingsHandler().getChatLog().saveConfig();
      }
      return;
    }

    String configFormat = configFile.getString("Chat_Settings.Chat_Formats.Default_Format")
      .replace("%prefix%", (plugin.getChat().getPlayerPrefix(player) != null ? plugin.getChat().getPlayerPrefix(player) : ""))
      .replace("%suffix%", (plugin.getChat().getPlayerSuffix(player) != null ? plugin.getChat().getPlayerSuffix(player) : ""))
      .replace("%displayname%", getNameColour(player) + "%s")
      .replace("%message%", getChatColour(player) + "%s");


    chatEvent.setFormat(Utilities.colourise(configFormat));

    if(configFile.getBoolean("Chat_Settings.Log_Chat_Messages")) {
      chatlog.set("Chat_Log." + chatLogTime, player.getName() + " >> " + rawMessage);
      plugin.getSettingsHandler().getChatLog().saveConfig();
    }
  }

  private String getChatColour(final Player player) {
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

  private String getNameColour(final Player player) {
    if(userData.isSet(player.getUniqueId() + ".Name_Colour")) {
      return Utilities.colourise(userData.getString(player.getUniqueId() + ".Name_Colour"));
    }

    for(String groupName : configFile.getConfigurationSection("Group_Name_Colour.Groups").getKeys(false)) {

      if(Utilities.checkPermission(player, false, "stylizer.namecolour.groups." + groupName.toLowerCase())) {
        return Utilities.colourise(groupNameColour(player, groupName));
      }
    }

    return Utilities.colourise(configFile.getString("Default_Name_Colour", "#fff954"));
  }

  private String groupNameColour(final Player player, final String groupName) {

    if(!configFile.getBoolean("Group_Name_Colour.Enabled")) {
      return playerNameColour(player);
    }

    if(configFile.getConfigurationSection("Group_Name_Colour.Groups").getKeys(false).size() == 0) {
      return playerNameColour(player);
    }

    return configFile.getString("Group_Name_Colour.Groups." + groupName);
  }

  private String playerNameColour(final Player player) {

    if(!userData.isSet(player.getUniqueId() + ".Name_Colour")) {
      return configFile.getString("Default_Name_Colour", "&e");
    }

    return userData.getString(player.getUniqueId() + ".Name_Colour");
  }
}
