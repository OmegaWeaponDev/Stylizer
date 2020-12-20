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
import org.bukkit.plugin.PluginDescriptionFile;

public class PlayerListener implements Listener {
  private final FileConfiguration configFile = OmegaNames.getInstance().getConfigFile().getConfig();

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
    Player player = playerJoinEvent.getPlayer();

    Bukkit.getScheduler().scheduleSyncRepeatingTask(OmegaNames.getInstance(), () -> setNameColour(player), 20L * 5L, 20L * 15L);

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

  private void setNameColour(final Player player) {

    if(configFile.getBoolean("Name_Colour_Login")) {

      if(OmegaNames.getInstance().getPlayerData().getConfig().isConfigurationSection(player.getUniqueId().toString())) {
        player.setDisplayName(Utilities.colourise(OmegaNames.getInstance().getPlayerData().getConfig().getString(player.getUniqueId().toString() + ".Name_Colour") + player.getName()) + ChatColor.RESET);
        player.setPlayerListName(player.getDisplayName());
        return;
      }

      for(String groupName : configFile.getConfigurationSection("Group_Name_Colour.Groups").getKeys(false)) {

        if(Utilities.checkPermission(player, false, "omeganames.namecolour.groups." + groupName.toLowerCase())) {
          player.setDisplayName(Utilities.colourise(groupNameColour(player, groupName) + player.getName()) + ChatColor.RESET);
          player.setPlayerListName(player.getDisplayName());
          return;
        }
      }

      player.setDisplayName(Utilities.colourise(configFile.getString("Default_Name_Colour", "&e") + player.getName()) + ChatColor.RESET);
      player.setPlayerListName(player.getDisplayName());
      return;
    }

    for(String groupName : configFile.getConfigurationSection("Group_Name_Colour.Groups").getKeys(false)) {

      if(Utilities.checkPermission(player, false, "omeganames.namecolour.groups." + groupName.toLowerCase())) {
        player.setDisplayName(Utilities.colourise(groupNameColour(player, groupName) + player.getName()) + ChatColor.RESET);
        player.setPlayerListName(player.getDisplayName());
        return;
      }
    }

    player.setDisplayName(Utilities.colourise(playerNameColour(player) + player.getName()) + ChatColor.RESET);
    player.setPlayerListName(player.getDisplayName());
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
