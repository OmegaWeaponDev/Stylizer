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
    configFile = plugin.getConfigFile().getConfig();
    chatlog = plugin.getChatlog().getConfig();
    userData = plugin.getPlayerData().getConfig();
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
      String configFormat = configFile.getString("Chat_Settings.Chat_Formats.Group_Formats.Groups." + groupNames);
      String playerPrefix = configFile.getString("Chat_Settings.Chat_Formats.Group_Formats.Groups." + groupNames)
        .replace("%prefix%", (plugin.getChat().getPlayerPrefix(player) != null ? plugin.getChat().getPlayerPrefix(player) : ""));
      String playerSuffix = configFile.getString("Chat_Settings.Chat_Formats.Group_Formats.Groups." + groupNames)
        .replace("%suffix%", (plugin.getChat().getPlayerSuffix(player) != null ? plugin.getChat().getPlayerSuffix(player) : ""));

      final String groupFormat = configFormat
        .replace("%prefix%", playerPrefix)
        .replace("%suffix%", playerSuffix)
        .replace("%displayname%", "%s")
        .replace("%message%", getChatColour(player) + "%s");

      if(Utilities.checkPermission(player, false, "stylizer.chat.groups." + groupNames.toLowerCase())) {
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
          String format = Utilities.colourise(PlaceholderAPI.setPlaceholders(player, groupFormat));
          chatEvent.setFormat(format);

          if(configFile.getBoolean("Chat_Settings.Log_Chat_Messages")) {
            chatlog.set("Chat_Log." + chatLogTime, player.getName() + " >> " + rawMessage);
            plugin.getChatlog().saveConfig();
          }
          return;
        }

        String format = Utilities.colourise(groupFormat);
        chatEvent.setFormat(format);

        if(configFile.getBoolean("Chat_Settings.Log_Chat_Messages")) {
          chatlog.set("Chat_Log." + chatLogTime, player.getName() + " >> " + rawMessage);
          plugin.getChatlog().saveConfig();
        }
        return;
      }
    }

    if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      String playerPrefix = configFile.getString("Chat_Settings.Chat_Formats.Default_Format")
        .replace("%prefix%", (plugin.getChat().getPlayerPrefix(player) != null ? plugin.getChat().getPlayerPrefix(player) : ""));
      String playerSuffix = configFile.getString("Chat_Settings.Chat_Formats.Default_Format")
        .replace("%suffix%", (plugin.getChat().getPlayerSuffix(player) != null ? plugin.getChat().getPlayerSuffix(player) : ""));

      String format = Utilities.colourise(PlaceholderAPI.setPlaceholders(player, configFile.getString("Chat_Settings.Chat_Formats.Default_Format")
        .replace("%prefix%", playerPrefix)
        .replace("%suffix%", playerSuffix)
        .replace("%displayname%", "%s")
        .replace("%message%", "%s")
      ));

      chatEvent.setFormat(format);

      if(configFile.getBoolean("Chat_Settings.Log_Chat_Messages")) {
        chatlog.set("Chat_Log." + chatLogTime, player.getName() + " >> " + rawMessage);
        plugin.getChatlog().saveConfig();
      }
      return;
    }

    String playerPrefix = configFile.getString("Chat_Settings.Chat_Formats.Default_Format")
      .replace("%prefix%", (plugin.getChat().getPlayerPrefix(player) != null ? plugin.getChat().getPlayerPrefix(player) : ""));
    String playerSuffix = configFile.getString("Chat_Settings.Chat_Formats.Default_Format")
      .replace("%suffix%", (plugin.getChat().getPlayerSuffix(player) != null ? plugin.getChat().getPlayerSuffix(player) : ""));

    String format = Utilities.colourise(configFile.getString("Chat_Settings.Chat_Formats.Default_Format")
      .replace("%prefix%", "{prefix}").replace("{prefix}", playerPrefix)
      .replace("%suffix%", "{suffix}").replace("{suffix}", playerSuffix)
      .replace("%displayname%", "%s")
      .replace("%message%", getChatColour(player) + "%s")
    );

    chatEvent.setFormat(format);

    if(configFile.getBoolean("Chat_Settings.Log_Chat_Messages")) {
      chatlog.set("Chat_Log." + chatLogTime, player.getName() + " >> " + rawMessage);
      plugin.getChatlog().saveConfig();
    }
  }

  private String getChatColour(final Player player) {
    if(userData.getString(player.getUniqueId() + ".Chat_Colour") != null) {
      return userData.getString(player.getUniqueId() + ".Chat_Colour");
    }

    if(!configFile.getBoolean("Group_Chat_Colour")) {
      return configFile.getString("Default_Chat_Colour");
    }

    for(String groupName : configFile.getStringList("Group_Chat_Colour.Groups")) {
      if(Utilities.checkPermission(player, false, "stylizer.chatcolour.groups." + groupName.toLowerCase())) {
        return configFile.getString("Group_Chat_Colour.Groups." + groupName);
      }
    }

    return "";
  }
}
