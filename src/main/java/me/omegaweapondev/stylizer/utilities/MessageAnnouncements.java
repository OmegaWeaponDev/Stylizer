package me.omegaweapondev.stylizer.utilities;

import me.clip.placeholderapi.PlaceholderAPI;
import me.omegaweapondev.stylizer.Stylizer;
import me.ou.library.Utilities;
import me.ou.library.chat.ChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MessageAnnouncements {
  private final FileConfiguration configFile;
  private final Stylizer plugin;

  public MessageAnnouncements(final Stylizer plugin) {
    this.plugin = plugin;
    configFile = plugin.getSettingsHandler().getConfigFile().getConfig();
  }

  public void broadcastAnnouncements() {
    Random rand = new Random();
    List<String> messages = new ArrayList<>(configFile.getConfigurationSection("Announcement_Messages.Messages").getKeys(false));
    String announcement = messages.get(rand.nextInt(messages.size()));

    for(Player player : Bukkit.getOnlinePlayers()) {
      if(configFile.getBoolean("Announcement_Messages.Messages." + announcement + ".Hover.Enabled")) {
        sendWithHover(player, announcement).send(player);
      } else {
        sendWithoutHover(player, announcement);
      }
    }
  }

  private String getHoverMessage(String announcement) {
    StringBuilder hoverMessage = new StringBuilder();
    for(String hoverMessageRaw : configFile.getStringList("Announcement_Messages.Messages." + announcement + ".Hover.Hover_Message")) {
      hoverMessage.append(hoverMessageRaw).append("\n");
    }
    return hoverMessage.toString();
  }

  private ChatComponent sendWithHover(Player onlinePlayer, String announcement) {
    final String CONFIG_MESSAGE = configFile.getString("Announcement_Messages.Messages." + announcement + ".Message", "");
    ChatComponent messageComponent;

    if(plugin.isPlaceholderAPI()) {
      messageComponent = new ChatComponent(Utilities.componentSerializerFromString(PlaceholderAPI.setPlaceholders(onlinePlayer, CONFIG_MESSAGE)));
      messageComponent.appendHover(Utilities.componentSerializerFromString(getHoverMessage(announcement)));

      return messageComponent;
    }

    messageComponent = new ChatComponent(Utilities.componentSerializerFromString(CONFIG_MESSAGE));
    messageComponent.appendHover(Utilities.componentSerializerFromString(getHoverMessage(announcement)));

    return messageComponent;
  }

  private void sendWithoutHover(Player player, String announcement) {
    final String CONFIG_MESSAGE = configFile.getString("Announcement_Messages.Messages." + announcement + ".Message", "");

    if(plugin.isPlaceholderAPI()) {
      player.sendMessage(Utilities.componentSerializerFromString(PlaceholderAPI.setPlaceholders(player, CONFIG_MESSAGE)));
      return;
    }
    player.sendMessage(Utilities.componentSerializerFromString(CONFIG_MESSAGE));
  }
}
