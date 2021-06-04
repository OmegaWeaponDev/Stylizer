package me.omegaweapondev.stylizer.events;

import me.clip.placeholderapi.PlaceholderAPI;
import me.omegaweapondev.stylizer.Stylizer;
import me.omegaweapondev.stylizer.utilities.MessageAnnouncements;
import me.ou.library.SpigotUpdater;
import me.ou.library.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerListener implements Listener {
  private final Stylizer plugin;

  private final FileConfiguration configFile;
  private final Map<UUID, Integer> tablistRefreashMap = new HashMap<>();

  public PlayerListener(final Stylizer plugin) {
    this.plugin = plugin;
    configFile = plugin.getConfigFile().getConfig();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
    Player player = playerJoinEvent.getPlayer();

    tablistHeaderFooter(player);
    setNameColour(player);

    BukkitTask tablistRefreashTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> tablistRefreash(player), 20L * 5L, 20L * 10L);
    tablistRefreashMap.put(player.getUniqueId(), tablistRefreashTask.getTaskId());

    // Send the player a message on join if there is an update for the plugin
    if(Utilities.checkPermissions(player, true, "stylizer.update", "stylizer.admin")) {
      new SpigotUpdater(plugin, 78327).getVersion(version -> {
        int spigotVersion = Integer.parseInt(version.replace(".", ""));
        int pluginVersion = Integer.parseInt(plugin.getDescription().getVersion().replace(".", ""));

        if(pluginVersion >= spigotVersion) {
          Utilities.logInfo(true, "You are already running the latest version");
          return;
        }

        PluginDescriptionFile pdf = plugin.getDescription();
        Utilities.logWarning(true,
          "A new version of " + pdf.getName() + " is avaliable!",
          "Current Version: " + pdf.getVersion() + " > New Version: " + version,
          "Grab it here: https://github.com/OmegaWeaponDev/Stylizer"
        );
      });
    }

    if(Bukkit.getOnlinePlayers().size() >= 1) {
      MessageAnnouncements messageAnnouncements = new MessageAnnouncements(plugin, player);
      messageAnnouncements.broadcastAnnouncements();
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
    final Player player = playerQuitEvent.getPlayer();

    Bukkit.getScheduler().cancelTask(tablistRefreashMap.get(player.getUniqueId()));

    if(tablistRefreashMap.get(player.getUniqueId()) != null) {
      tablistRefreashMap.remove(player.getUniqueId());
    }
  }

  private void setNameColour(final Player player) {
    if(configFile.getBoolean("Name_Colour_Login")) {

      if(plugin.getPlayerData().getConfig().isConfigurationSection(player.getUniqueId().toString())) {
        player.setDisplayName(Utilities.colourise(plugin.getPlayerData().getConfig().getString(player.getUniqueId() + ".Name_Colour") + player.getName()) + ChatColor.RESET);
        formatTablist(player);
        return;
      }

      for(String groupName : configFile.getConfigurationSection("Group_Name_Colour.Groups").getKeys(false)) {

        if(Utilities.checkPermission(player, false, "stylizer.namecolour.groups." + groupName.toLowerCase())) {
          player.setDisplayName(Utilities.colourise(groupNameColour(player, groupName) + player.getName()) + ChatColor.RESET);
          formatTablist(player);
          return;
        }
      }
      player.setDisplayName(Utilities.colourise(configFile.getString("Default_Name_Colour", "#fff954") + player.getName()) + ChatColor.RESET);
      formatTablist(player);
      return;
    }
    player.setDisplayName(Utilities.colourise(configFile.getString("Default_Name_Colour", "#fff954") + player.getName()) + ChatColor.RESET);
    formatTablist(player);
  }

  private void tablistRefreash(final Player player) {

    if(plugin.getPlayerData().getConfig().isConfigurationSection(player.getUniqueId().toString())) {
      player.setDisplayName(Utilities.colourise(plugin.getPlayerData().getConfig().getString(player.getUniqueId() + ".Name_Colour") + player.getName()) + ChatColor.RESET);
      formatTablist(player);
      return;
    }

    for(String groupName : configFile.getConfigurationSection("Group_Name_Colour.Groups").getKeys(false)) {

      if(Utilities.checkPermission(player, false, "stylizer.namecolour.groups." + groupName.toLowerCase())) {
        player.setDisplayName(Utilities.colourise(groupNameColour(player, groupName) + player.getName()) + ChatColor.RESET);
        formatTablist(player);
        return;
      }
    }
    player.setDisplayName(Utilities.colourise(configFile.getString("Default_Name_Colour", "#fff954") + player.getName()) + ChatColor.RESET);
    formatTablist(player);
    tablistHeaderFooter(player);
  }

  private void formatTablist(final Player player) {
    final String playerPrefix = plugin.getChat().getPlayerPrefix(player);
    final String playerSuffix = plugin.getChat().getPlayerSuffix(player);
    boolean isPlaceholderAPI = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

    if(!configFile.getBoolean("Tablist_Name_Colour") && !configFile.getBoolean("Tablist_Prefix_Suffix")) {
      player.setPlayerListName(PlaceholderAPI.setPlaceholders(player, player.getName()));
      return;
    }

    if(!configFile.getBoolean("Tablist_Name_Colour") && configFile.getBoolean("Tablist_Prefix_Suffix") && player.isOnline()) {
      if(isPlaceholderAPI) {
        player.setPlayerListName(
          Utilities.colourise(PlaceholderAPI.setPlaceholders(player,
            (playerPrefix != null ? playerPrefix  + " " : "") + player.getName() + (playerSuffix != null ? playerSuffix  + " " : "")
          )
        ));
        return;
      }

      player.setPlayerListName(
        Utilities.colourise(
          (playerPrefix != null ? playerPrefix  + " " : "") + player.getName() + (playerSuffix != null ? playerSuffix  + " " : "")
          )
        );
      return;
    }

    if(configFile.getBoolean("Tablist_Name_Colour") && !configFile.getBoolean("Tablist_Prefix_Suffix")) {
      if(!configFile.getConfigurationSection("Group_Name_Colour.Groups").getKeys(false).isEmpty()) {
        for(String groupName : configFile.getConfigurationSection("Group_Name_Colour.Groups").getKeys(false)) {

          if(Utilities.checkPermission(player, false, "stylizer.namecolour.groups." + groupName.toLowerCase())) {
            if(isPlaceholderAPI) {
              player.setPlayerListName(
                Utilities.colourise(PlaceholderAPI.setPlaceholders(player,
                  groupNameColour(player, groupName) + player.getName()
                )
              ));
              return;
            }

            player.setPlayerListName(
              Utilities.colourise(
                groupNameColour(player, groupName) + player.getName()
                )
              );
            return;
          }
          player.setPlayerListName(Utilities.colourise(
            configFile.getString("Default_Name_Colour", "#fff954") + player.getName()
          ));
        }
      }
      return;
    }

    if(configFile.getBoolean("Tablist_Name_Colour") && configFile.getBoolean("Tablist_Prefix_Suffix") && player.isOnline()) {
      if(!configFile.getConfigurationSection("Group_Name_Colour.Groups").getKeys(false).isEmpty()) {
        for(String groupName : configFile.getConfigurationSection("Group_Name_Colour.Groups").getKeys(false)) {

          if(Utilities.checkPermission(player, false, "stylizer.namecolour.groups." + groupName.toLowerCase())) {

            if(isPlaceholderAPI) {
              player.setPlayerListName(
                Utilities.colourise(PlaceholderAPI.setPlaceholders(player,
                  (playerPrefix != null ? playerPrefix  + " " : "") + groupNameColour(player, groupName) + player.getName() + (playerSuffix != null ? playerSuffix  + " " : "")
                )
              ));
              return;
            }

            player.setPlayerListName(
              Utilities.colourise(PlaceholderAPI.setPlaceholders(player,
                (playerPrefix != null ? playerPrefix  + " " : "") + groupNameColour(player, groupName) + player.getName() + (playerSuffix != null ? playerSuffix  + " " : "")
                )
              ));
            return;
          }

          if(isPlaceholderAPI) {
            player.setPlayerListName(Utilities.colourise(PlaceholderAPI.setPlaceholders(player,
              (playerPrefix != null ? playerPrefix  + " " : "") + configFile.getString("Default_Name_Colour", "#fff954") + player.getName() + (playerSuffix != null ? playerSuffix  + " " : "")
            )));
            return;
          }
          player.setPlayerListName(Utilities.colourise(
            (playerPrefix != null ? playerPrefix  + " " : "") + configFile.getString("Default_Name_Colour", "#fff954") + player.getName() + (playerSuffix != null ? playerSuffix  + " " : "")
          ));
          return;

        }
      }
    }
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

    if(!plugin.getPlayerData().getConfig().isSet(player.getUniqueId() + ".Name_Colour")) {
      return configFile.getString("Default_Name_Colour", "#fff954");
    }

    return plugin.getPlayerData().getConfig().getString(player.getUniqueId() + ".Name_Colour");
  }

  private void tablistHeaderFooter(final Player player) {
    if(!configFile.getBoolean("Tablist.Enabled")) {
      return;
    }

    final String playerPrefix = plugin.getChat().getPlayerPrefix(player);
    final String playerSuffix = plugin.getChat().getPlayerSuffix(player);

    player.setPlayerListHeader(Utilities.colourise(configFile.getString("Tablist.Tablist_Header").replace("%player%", (playerPrefix != null ? playerPrefix  + " " : "") + player.getDisplayName() + (playerSuffix != null ? playerSuffix  + " " : ""))));

    StringBuilder footer = new StringBuilder();
    for(String footerMessage : configFile.getStringList("Tablist.Tablist_Footer")) {
      footer.append(footerMessage.replace("%player%", (playerPrefix != null ? playerPrefix  + " " : "") + player.getDisplayName() + (playerSuffix != null ? playerSuffix  + " " : ""))).append("\n");
    }

    player.setPlayerListFooter(Utilities.colourise(footer.toString()));
  }
}
