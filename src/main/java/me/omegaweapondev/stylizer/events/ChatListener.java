package me.omegaweapondev.stylizer.events;

import me.clip.placeholderapi.PlaceholderAPI;
import me.omegaweapondev.stylizer.Stylizer;
import me.omegaweapondev.stylizer.utilities.PlayerUtil;
import me.ou.library.Utilities;
import me.ou.library.libs.net.kyori.adventure.text.Component;
import me.ou.library.libs.net.kyori.adventure.text.TextReplacementConfig;
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

    TextReplacementConfig replacePlayer = TextReplacementConfig.builder()
            .matchLiteral("%displayname%").replacement(playerUtil.getNameColour() + "%1&s")
            .matchLiteral("%prefix%").replacement(playerUtil.getPrefix())
            .matchLiteral("%suffix%").replacement(playerUtil.getSuffix())
            .build();

    TextReplacementConfig replaceMessage = TextReplacementConfig.builder()
            .matchLiteral("%message%").replacement("%2&s")
            .build();

    // Add the chat colour and colourise the chat message itself.
    chatEvent.setMessage(
      Utilities.componentSerializerFromString(
        playerUtil.getChatColour() + Utilities.componentSerializerFromString(chatEvent.getMessage())
      )
    );

    // ==================================================================================
    // Group Chat Formatting
    // ==================================================================================

    // Loops through the list of groups in the group chat format section of the config
    for(String groupName : configFile.getConfigurationSection(groupChatFormat).getKeys(false)) {
      // Checks if the player has access to the group format.
      if(Utilities.checkPermission(player, false, "stylizer.chat.groups." + groupName.toLowerCase())) {
        // Create the new chat format from the group chat format in the config
        Component groupChatComponent = Component.text(configFile.getString(groupChatFormat + "." + groupName)).replaceText(replaceMessage).replaceText(replacePlayer);

        // Checks if PlaceholderAPI is enabled,
          // If so, parse any placeholders being used in the chat format and then set the chat format.
        if(isPlaceholderAPIEnabled()) {
          chatEvent.setFormat(PlaceholderAPI.setPlaceholders(player, Utilities.componentSerializer(groupChatComponent)));
          // Add the chat message to the log file if enabled
          addChatLogMessage(chatEvent, player);
          return;
        }

        // PlaceholderAPI is not enabled, so just set the chat format.
        chatEvent.setFormat(Utilities.componentSerializer(groupChatComponent));
        // Add the chat message to the log file if enabled
        addChatLogMessage(chatEvent, player);
        return;
      }
    }
    // ==================================================================================
      // Default Chat Formatting
    // ==================================================================================

    // Create a new chat format using the default chat format in the config file
    Component defaultChatComponent = Component.text(configFile.getString(defaultChatFormat)).replaceText(replaceMessage).replaceText(replacePlayer);
    // Checks if placeholderAPI is enabled
      // If so, parse any placeholders used in the chat format and then set the chat format.
    if(isPlaceholderAPIEnabled()) {
      chatEvent.setFormat(PlaceholderAPI.setPlaceholders(player, Utilities.componentSerializer(defaultChatComponent)));
      // Add the chat message to the log file if enabled
      addChatLogMessage(chatEvent, player);
      return;
    }

    // PlaceholderAPI is not enabled, so just set the chat format.
    chatEvent.setFormat(Utilities.componentSerializer(defaultChatComponent));
    // Add the chat message to the log file if enabled
    addChatLogMessage(chatEvent, player);
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
