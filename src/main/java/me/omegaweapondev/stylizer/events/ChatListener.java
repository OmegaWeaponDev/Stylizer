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

  private final String defaultChatFormat;
  private final String groupChatFormat;

  public ChatListener(final Stylizer plugin) {
    this.plugin = plugin;
    configFile = plugin.getSettingsHandler().getConfigFile().getConfig();
    chatlog = plugin.getSettingsHandler().getChatLog().getConfig();
    userData = plugin.getSettingsHandler().getPlayerData().getConfig();

    defaultChatFormat = configFile.getString("Chat_Settings.Chat_Formats.Default_Format");
    groupChatFormat = "Chat_Settings.Chat_Formats.Group_Formats.Groups";
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onPlayerChat(AsyncPlayerChatEvent chatEvent) {
    final Player player = chatEvent.getPlayer();

    if(!configFile.getBoolean("Chat_Settings.Enabled")) {
      return;
    }

    formatChat(player, chatEvent);
  }

  private void formatChat(final Player player, final AsyncPlayerChatEvent chatEvent) {
    String playerPrefix = plugin.getChat().getPlayerPrefix(player) != null ? plugin.getChat().getPlayerPrefix(player) : "";
    String playerSuffix = plugin.getChat().getPlayerSuffix(player) != null ? plugin.getChat().getPlayerSuffix(player) : "";

    for(String groupName : configFile.getConfigurationSection(groupChatFormat).getKeys(false)) {
      if(Utilities.checkPermission(player, false, "stylizer.chat.groups." + groupName.toLowerCase())) {
        chatEvent.setFormat(applyFormat(player, configFile.getString(groupChatFormat + "." + groupName), playerPrefix, playerSuffix));
        chatEvent.setMessage(Utilities.colourise(getChatColour(player) + chatEvent.getMessage()));

        addChatLogMessage(chatEvent, player);
        return;
      }
    }

    chatEvent.setFormat(applyFormat(player, defaultChatFormat, playerPrefix, playerSuffix));
    chatEvent.setMessage(Utilities.colourise(getChatColour(player) + chatEvent.getMessage()));

    addChatLogMessage(chatEvent, player);
  }

  private String applyFormat(Player player, String configFormat, String prefix, String suffix) {
    configFormat = configFormat.replace("%", "%%");
    configFormat = configFormat.replace("%%prefix%%", prefix);
    configFormat = configFormat.replace("%%suffix%%", suffix);
    configFormat = configFormat.replace("%%displayname%%", getNameColour(player) + "%1$s");
    configFormat = configFormat.replace("%%message%%", "%2$s");

    if(isPlaceholderAPIEnabled()) {
      return Utilities.colourise(PlaceholderAPI.setPlaceholders(player, configFormat));
    }

    return Utilities.colourise(configFormat);
  }

  private String getNameColour(final Player player) {
    if(userData.getString(player.getUniqueId() + ".Name_Colour") != null) {
      return userData.getString(player.getUniqueId() + ".Name_Colour");
    }

    if(!configFile.getBoolean("Group_Name_Colour.Enabled")) {
      return configFile.getString("Default_Name_Colour");
    }

    for(String groupName : configFile.getConfigurationSection("Group_Name_Colour.Groups").getKeys(false)) {
      if(Utilities.checkPermission(player, false, "stylizer.chatcolour.groups." + groupName.toLowerCase())) {
        return configFile.getString("Group_Name_Colour.Groups." + groupName);
      }
    }

    return configFile.getString("Default_Name_Colour");
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

  private void addChatLogMessage(AsyncPlayerChatEvent chatEvent, Player player) {
    long currentTime = System.currentTimeMillis();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    Date date = new Date(currentTime);
    String chatLogTime = simpleDateFormat.format(date);

    if(configFile.getBoolean("Chat_Settings.Log_Chat_Messages")) {
      chatlog.set("Chat_Log." + chatLogTime, player.getName() + " >> " + chatEvent.getMessage());
      plugin.getSettingsHandler().getChatLog().saveConfig();
    }
  }

  private boolean isPlaceholderAPIEnabled() {
    return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
  }
}
