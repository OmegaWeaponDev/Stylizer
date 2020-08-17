package me.omegaweapondev.omeganames.events;

import me.omegaweapondev.omeganames.OmegaNames;
import me.omegaweapondev.omeganames.utilities.Colour;
import me.ou.library.SpigotUpdater;
import me.ou.library.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener implements Listener {
  private final FileConfiguration configFile = OmegaNames.getInstance().getConfigFile().getConfig();

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
    Player player = playerJoinEvent.getPlayer();


    Bukkit.getScheduler().scheduleSyncRepeatingTask(OmegaNames.getInstance(), () -> setNameColour(player), 20L * 5L, 20L * 30L);

    // Call gui reload method, so item lore is refreshed for each player, as it checks for permissions
    // to decide which lore messages should be applied
    new BukkitRunnable() {
      @Override
      public void run() {
        OmegaNames.getInstance().onGUIReload();
      }
    }.runTaskLaterAsynchronously(OmegaNames.getInstance(), 40);

    // Send the player a message on join if there is an update for the plugin
    if(Utilities.checkPermissions(player, true, "omeganames.update", "omeganames.*")) {
      new SpigotUpdater(OmegaNames.getInstance(), 78327).getVersion(version -> {
        if (!OmegaNames.getInstance().getDescription().getVersion().equalsIgnoreCase(version)) {
          PluginDescriptionFile pdf = OmegaNames.getInstance().getDescription();
          Utilities.message(player,
            "&bA new version of &c" + pdf.getName() + " &bis avaliable!",
            "&bCurrent Version: &c" + pdf.getVersion() + " &b> New Version: &c" + version,
            "&bGrab it here:&c https://spigotmc.org/resources/78327"
          );
        }
      });
    }
  }

  private void setNameColour(final Player player) {
    // Remove the custom name colours if Name_Colour_Login is not enabled.
    if(!configFile.getBoolean("Name_Colour_Login")) {
      OmegaNames.getInstance().getPlayerData().getConfig().set(player.getUniqueId().toString() + ".Name_Colour", null);
      OmegaNames.getInstance().getPlayerData().saveConfig();

      for(String groupName : configFile.getConfigurationSection("Group_Name_Colour.Groups").getKeys(false)) {

        if(Utilities.checkPermission(player, false, "omeganames.namecolour.groups." + groupName.toLowerCase())) {

          player.setDisplayName(Utilities.colourise(Colour.groupNameColour(player, groupName)) + player.getName());
          player.setPlayerListName(Utilities.colourise(Colour.groupNameColour(player, groupName)) + player.getName());
          return;
        }
      }
      return;
    }

    // Name_Colour_Login is enabled so check if they have the permission, and apply the name colour
    if(!Utilities.checkPermissions(player, true, "omeganames.namecolour.login", "omeganames.*")) {
      OmegaNames.getInstance().getPlayerData().getConfig().set(player.getUniqueId().toString() + ".Name_Colour", null);
      OmegaNames.getInstance().getPlayerData().saveConfig();
    }

    for(String groupName : configFile.getConfigurationSection("Group_Name_Colour.Groups").getKeys(false)) {

      if(Utilities.checkPermission(player, false, "omeganames.namecolour.groups." + groupName.toLowerCase())) {
        player.setDisplayName(Utilities.colourise(Colour.groupNameColour(player, groupName)) + player.getName());
        player.setPlayerListName(Utilities.colourise(Colour.groupNameColour(player, groupName)) + player.getName());
        return;
      }
    }

    player.setDisplayName(Utilities.colourise(Colour.playerNameColour(player)) + player.getName());
    player.setPlayerListName(Utilities.colourise(Colour.playerNameColour(player)) + player.getName());

  }
}
