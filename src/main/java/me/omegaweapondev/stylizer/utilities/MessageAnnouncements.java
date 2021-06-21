package me.omegaweapondev.stylizer.utilities;

import me.clip.placeholderapi.PlaceholderAPI;
import me.omegaweapondev.stylizer.Stylizer;
import me.ou.library.Utilities;
import me.ou.library.chat.ChatComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MessageAnnouncements {
  private final Stylizer plugin;
  private final Player player;
  private final FileConfiguration configFile;
  private final MessageHandler messageHandler;
  private final SettingsHandler settingsHandler;

  public MessageAnnouncements(final Stylizer plugin, final Player player) {
    this.plugin = plugin;
    this.player = player;
    configFile = plugin.getSettingsHandler().getConfigFile().getConfig();
    messageHandler = new MessageHandler(plugin, plugin.getSettingsHandler().getMessagesFile().getConfig());
    settingsHandler = new SettingsHandler(plugin);
  }

  public void broadcastAnnouncements() {

    if(!configFile.getBoolean("Announcement_Messages.Enabled")) {
      return;
    }

    if(configFile.getConfigurationSection("Announcement_Messages.Messages").getKeys(false).isEmpty()) {
      return;
    }

    Random rand = new Random();
    List<String> messages = new ArrayList<>(configFile.getConfigurationSection("Announcement_Messages.Messages").getKeys(false));
    String announcement = messages.get(rand.nextInt(messages.size()));

    // Support PlaceholderAPI placeholders
    if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
      if(configFile.getBoolean("Announcement_Messages.Messages." + announcement + ".Hover.Enabled")) {
        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
          StringBuilder hoverMessage = new StringBuilder();
          for(String hoverMessageRaw : configFile.getStringList("Announcement_Messages.Messages." + announcement + ".Hover.Hover_Message")) {
            hoverMessage.append(hoverMessageRaw).append("\n");
          }

          new ChatComponent(PlaceholderAPI.setPlaceholders(player,
            configFile.getString("Announcement_Messages.Messages." + announcement + ".Message")
          )).onHover(HoverEvent.Action.SHOW_TEXT, PlaceholderAPI.setPlaceholders(player, Utilities.colourise(hoverMessage.toString()))).send(onlinePlayer);
        }
      } else {
        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
          new ChatComponent(PlaceholderAPI.setPlaceholders(player, Utilities.colourise(configFile.getString("Announcement_Messages.Messages." + announcement + ".Message")))).send(onlinePlayer);
        }
      }
      return;
    }

    // If PlaceholderAPI is not installed
    if(!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      for(String message : configFile.getConfigurationSection("Announcement_Messages.Messages").getKeys(false)) {
        if(configFile.getBoolean("Announcement_Messages.Messages." + announcement + ".Hover.Enabled")) {
          for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            StringBuilder hoverMessage = new StringBuilder();
            for(String hoverMessageRaw : configFile.getStringList("Announcement_Messages.Messages." + announcement + ".Hover.Hover_Message")) {
              hoverMessage.append(hoverMessageRaw).append("\n");
            }

            new ChatComponent(Utilities.colourise(messageHandler.string(configFile.getString("Announcement_Messages.Messages." + message + ".Message"), ""))
            ).onHover(HoverEvent.Action.SHOW_TEXT, PlaceholderAPI.setPlaceholders(player, Utilities.colourise(hoverMessage.toString()))).send(onlinePlayer);
          }

        } else {
          for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            new ChatComponent(Utilities.colourise("Announcement_Messages.Messages." + announcement + ".Message")).send(onlinePlayer);
          }
        }
      }
    }
  }
}
