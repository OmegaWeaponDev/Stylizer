package me.omegaweapondev.stylizer.utilities;

import me.clip.placeholderapi.PlaceholderAPI;
import me.omegaweapondev.stylizer.Stylizer;
import me.ou.library.Utilities;
import me.ou.library.chat.ChatComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MessageAnnouncements {
  private final Stylizer plugin;
  private final FileConfiguration configFile;
  private final MessageHandler messageHandler;
  
  public MessageAnnouncements(final Stylizer plugin) {
    this.plugin = plugin;
    configFile = plugin.getSettingsHandler().getConfigFile().getConfig();
    messageHandler = new MessageHandler(plugin, plugin.getSettingsHandler().getMessagesFile().getConfig());
  }

  public void broadcastAnnouncements() {
    Random rand = new Random();
    List<String> messages = new ArrayList<>(configFile.getConfigurationSection("Announcement_Messages.Messages").getKeys(false));
    String announcement = messages.get(rand.nextInt(messages.size()));

    for(Player player : Bukkit.getOnlinePlayers()) {
      if(configFile.getBoolean("Announcement_Messages.Messages." + announcement + ".Hover.Enabled")) {
        sendWithHover(player, announcement);
      } else {
        sendWithoutHover(player, announcement);
      }
    }
  }

  private String getHoverMessage(@NotNull String announcement) {
    StringBuilder hoverMessage = new StringBuilder();
    for(String hoverMessageRaw : configFile.getStringList("Announcement_Messages.Messages." + announcement + ".Hover.Hover_Message")) {
      hoverMessage.append(hoverMessageRaw).append("\n");
    }
    return hoverMessage.toString();
  }

  private void sendWithHover(@NotNull Player onlinePlayer, @NotNull String announcement) {
    ChatComponent announcementWithPapi = new ChatComponent(
      PlaceholderAPI.setPlaceholders(
        onlinePlayer,
        Utilities.colourise(
          configFile.getString("Announcement_Messages.Messages." + announcement + ".Message")
        )
      )
    ).onHover(
      HoverEvent.Action.SHOW_TEXT,
      PlaceholderAPI.setPlaceholders(
        onlinePlayer,
        Utilities.colourise(
          getHoverMessage(announcement)
        )
      )
    );

    ChatComponent announcementWithoutPapi = new ChatComponent(
      Utilities.colourise(
        configFile.getString("Announcement_Messages.Messages." + announcement + ".Message")
      )
    ).onHover(
      HoverEvent.Action.SHOW_TEXT,
      Utilities.colourise(
        getHoverMessage(announcement)
      )
    );

    if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      announcementWithPapi.send(onlinePlayer);
    } else {
      announcementWithoutPapi.send(onlinePlayer);
    }
  }

  private void sendWithoutHover(@NotNull Player player, @NotNull String announcement) {
    ChatComponent announcementWithPapi = new ChatComponent(
      PlaceholderAPI.setPlaceholders(
        player,
        Utilities.colourise(
          configFile.getString("Announcement_Messages.Messages." + announcement + ".Message")
        )
      )
    );

    ChatComponent announcementWithoutPapi = new ChatComponent(
      Utilities.colourise(
        configFile.getString("Announcement_Messages.Messages." + announcement + ".Message")
      )
    );

    if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      announcementWithPapi.send(player);
    } else {
      announcementWithoutPapi.send(player);
    }
  }
}
