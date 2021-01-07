package me.omegaweapondev.omeganames.events;

import me.omegaweapondev.omeganames.OmegaNames;
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
  private final FileConfiguration configFile = OmegaNames.getInstance().getConfigFile().getConfig();
  private final Map<UUID, Integer> tablistRefreashMap = new HashMap<>();

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
    Player player = playerJoinEvent.getPlayer();

    setNameColour(player);

    BukkitTask tablistRefreashTask = Bukkit.getScheduler().runTaskTimer(OmegaNames.getInstance(), () -> tablistRefreash(player), 20 * 15L, 20 * 15L);
    tablistRefreashMap.put(player.getUniqueId(), tablistRefreashTask.getTaskId());

    // Send the player a message on join if there is an update for the plugin
    if(Utilities.checkPermissions(player, true, "omeganames.update", "omeganames.admin")) {
      new SpigotUpdater(OmegaNames.getInstance(), 78327).getVersion(version -> {
        int spigotVersion = Integer.parseInt(version.replace(".", ""));
        int pluginVersion = Integer.parseInt(OmegaNames.getInstance().getDescription().getVersion().replace(".", ""));

        if(pluginVersion >= spigotVersion) {
          Utilities.logInfo(true, "You are already running the latest version");
          return;
        }

        PluginDescriptionFile pdf = OmegaNames.getInstance().getDescription();
        Utilities.logWarning(true,
          "A new version of " + pdf.getName() + " is avaliable!",
          "Current Version: " + pdf.getVersion() + " > New Version: " + version,
          "Grab it here: https://github.com/OmegaWeaponDev/OmegaNames"
        );
      });
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
    final Player player = playerQuitEvent.getPlayer();

    Bukkit.getScheduler().cancelTask(tablistRefreashMap.get(player.getUniqueId()));
    tablistRefreashMap.remove(player.getUniqueId());
  }

  private void setNameColour(final Player player) {
    if(configFile.getBoolean("Name_Colour_Login")) {

      if(OmegaNames.getInstance().getPlayerData().getConfig().isConfigurationSection(player.getUniqueId().toString())) {
        player.setDisplayName(Utilities.colourise(OmegaNames.getInstance().getPlayerData().getConfig().getString(player.getUniqueId().toString() + ".Name_Colour") + player.getName()) + ChatColor.RESET);
        formatTablist(player);
        return;
      }

      for(String groupName : configFile.getConfigurationSection("Group_Name_Colour.Groups").getKeys(false)) {

        if(Utilities.checkPermission(player, false, "omeganames.namecolour.groups." + groupName.toLowerCase())) {
          player.setDisplayName(Utilities.colourise(groupNameColour(player, groupName) + player.getName()) + ChatColor.RESET);
          formatTablist(player);
          return;
        }
      }
      player.setDisplayName(Utilities.colourise(configFile.getString("Default_Name_Colour", "&e") + player.getName()) + ChatColor.RESET);
      formatTablist(player);
      return;
    }
    player.setDisplayName(Utilities.colourise(configFile.getString("Default_Name_Colour", "&e") + player.getName()) + ChatColor.RESET);
    formatTablist(player);
  }

  private void tablistRefreash(final Player player) {
    if(OmegaNames.getInstance().getPlayerData().getConfig().isConfigurationSection(player.getUniqueId().toString())) {
      player.setDisplayName(Utilities.colourise(OmegaNames.getInstance().getPlayerData().getConfig().getString(player.getUniqueId().toString() + ".Name_Colour") + player.getName()) + ChatColor.RESET);
      formatTablist(player);
      return;
    }

    for(String groupName : configFile.getConfigurationSection("Group_Name_Colour.Groups").getKeys(false)) {

      if(Utilities.checkPermission(player, false, "omeganames.namecolour.groups." + groupName.toLowerCase())) {
        player.setDisplayName(Utilities.colourise(groupNameColour(player, groupName) + player.getName()) + ChatColor.RESET);
        formatTablist(player);
        return;
      }
    }
    player.setDisplayName(Utilities.colourise(configFile.getString("Default_Name_Colour", "&e") + player.getName()) + ChatColor.RESET);
    formatTablist(player);
    return;
  }

  private void formatTablist(final Player player) {
    final String playerPrefix = OmegaNames.getInstance().getChat().getPlayerPrefix(player);
    final String playerSuffix = OmegaNames.getInstance().getChat().getPlayerSuffix(player);

    if(!configFile.getBoolean("Tablist_Name_Colour") && !configFile.getBoolean("Tablist_Prefix_Suffix")) {
      player.setPlayerListName(player.getName());
      return;
    }

    if(!configFile.getBoolean("Tablist_Name_Colour") && configFile.getBoolean("Tablist_Prefix_Suffix") && player.isOnline()) {
      player.setPlayerListName(
        Utilities.colourise(
          (playerPrefix != null ? playerPrefix  + " " : "") + player.getName() + (playerSuffix != null ? playerSuffix  + " " : "")
        )
      );
      return;
    }

    if(configFile.getBoolean("Tablist_Name_Colour") && !configFile.getBoolean("Tablist_Prefix_Suffix")) {
      player.setPlayerListName(player.getDisplayName());
      return;
    }

    if(configFile.getBoolean("Tablist_Name_Colour") && configFile.getBoolean("Tablist_Prefix_Suffix") && player.isOnline()) {
      player.setPlayerListName(
        Utilities.colourise(
          (playerPrefix != null ? playerPrefix  + " " : "") + player.getDisplayName() + (playerSuffix != null ? playerSuffix  + " " : "")
        )
      );
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

    if(!OmegaNames.getInstance().getPlayerData().getConfig().isConfigurationSection(player.getUniqueId().toString())) {
      return configFile.getString("Default_Name_Colour", "&e");
    }

    return OmegaNames.getInstance().getPlayerData().getConfig().getString(player.getUniqueId().toString() + ".Name_Colour");
  }
}
