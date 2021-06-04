package me.omegaweapondev.stylizer.utilities;

import me.clip.placeholderapi.PlaceholderAPI;
import me.omegaweapondev.stylizer.Stylizer;
import me.ou.library.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class MessageAnnouncements {
  private final Stylizer plugin;
  private final Player player;
  private final FileConfiguration configFile;

  public MessageAnnouncements(final Stylizer plugin, final Player player) {
    this.plugin = plugin;
    this.player = player;
    configFile = plugin.getConfigFile().getConfig();
  }

  public void broadcastAnnouncements() {
    if(!configFile.getBoolean("Announcement_Messages.Enabled")) {
      return;
    }

    if(configFile.getStringList("Announcement_Messages.Messages").size() == 0) {
      return;
    }

    // Support PlaceholderAPI placeholders
    if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
      for(String message : configFile.getStringList("Announcement_Messages.Messages")) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () ->
          Utilities.broadcast(true, PlaceholderAPI.setPlaceholders(player, message)),
          20L * configFile.getInt("Announcement_Messages.Interval"),
          20L * configFile.getInt("Announcement_Messages.Interval")
        );
      }
      return;
    }

    // If PlaceholderAPI is not installed
    for(String message : configFile.getStringList("Announcement_Messages.Messages")) {
      Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () ->
        Utilities.broadcast(true, message),
        20L * configFile.getInt("Announcement_Messages.Interval"),
        20L * configFile.getInt("Announcement_Messages.Interval")
      );
    }
  }
}
