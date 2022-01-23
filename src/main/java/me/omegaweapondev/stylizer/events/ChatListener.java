package me.omegaweapondev.stylizer.events;

import me.clip.placeholderapi.PlaceholderAPI;
import me.omegaweapondev.stylizer.Stylizer;
import me.omegaweapondev.stylizer.utilities.PlayerUtil;
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
  private PlayerUtil playerUtil;
  private final FileConfiguration chatlog;

  private final String defaultChatFormat;
  private final String groupChatFormat;

  public ChatListener(final Stylizer plugin) {
    this.plugin = plugin;
    configFile = plugin.getSettingsHandler().getConfigFile().getConfig();
    chatlog = plugin.getSettingsHandler().getChatLog().getConfig();

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
    playerUtil = new PlayerUtil(plugin, player);

    for(String groupName : configFile.getConfigurationSection(groupChatFormat).getKeys(false)) {
      if(Utilities.checkPermission(player, false, "stylizer.chat.groups." + groupName.toLowerCase())) {
        chatEvent.setFormat(applyFormat(player, configFile.getString(groupChatFormat + "." + groupName)));
        chatEvent.setMessage(Utilities.colourise(playerUtil.getChatColour() + chatEvent.getMessage()));

        addChatLogMessage(chatEvent, player);
        return;
      }
    }

    chatEvent.setFormat(applyFormat(player, defaultChatFormat));
    chatEvent.setMessage(Utilities.colourise(playerUtil.getChatColour() + chatEvent.getMessage()));

    addChatLogMessage(chatEvent, player);
  }

  private String applyFormat(Player player, String configFormat) {
    playerUtil = new PlayerUtil(plugin, player);

    configFormat = configFormat.replace("%", "%%");
    configFormat = configFormat.replace("%%prefix%%", playerUtil.getPrefix());
    configFormat = configFormat.replace("%%suffix%%", playerUtil.getSuffix());
    configFormat = configFormat.replace("%%displayname%%", playerUtil.getNameColour() + "%1$s");
    configFormat = configFormat.replace("%%message%%", "%2$s");

    if(isPlaceholderAPIEnabled()) {
      return Utilities.colourise(PlaceholderAPI.setPlaceholders(player, configFormat));
    }

    return Utilities.colourise(configFormat);
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
