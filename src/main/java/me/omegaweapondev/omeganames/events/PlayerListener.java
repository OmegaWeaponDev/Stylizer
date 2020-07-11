package me.omegaweapondev.omeganames.events;

import me.omegaweapondev.omeganames.OmegaNames;
import me.omegaweapondev.omeganames.UpdateChecker;
import me.omegaweapondev.omeganames.utilities.Colour;
import me.ou.library.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  public static void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
    Player player = playerJoinEvent.getPlayer();

    // Set the players namecolours
    for(String groupName : OmegaNames.getInstance().getConfigFile().getConfig().getConfigurationSection("Group_Name_Colour.Groups").getKeys(false)) {
      if(Utilities.checkPermission(player, true, "omeganames.namecolours.groups." + groupName.toLowerCase())) {
        Utilities.colourise(Colour.groupNameColour(player, groupName));



        // Format the tablist
        if(OmegaNames.getInstance().getConfigFile().getConfig().getBoolean("Tablist_Name_Colour")) {
          Bukkit.getScheduler().scheduleSyncRepeatingTask(OmegaNames.getInstance(), () -> {
            player.setPlayerListName(Utilities.colourise(Colour.groupNameColour(player, groupName) + player.getName()));
          }, 20L * 5L, 20L * 60L);
        }
      } else {
        player.setDisplayName(Utilities.colourise(Colour.playerNameColour(player) + player.getName() + "&r"));

        // Format the tablist

        if(OmegaNames.getInstance().getConfigFile().getConfig().getBoolean("Tablist_Name_Colour")) {
          Bukkit.getScheduler().scheduleSyncRepeatingTask(OmegaNames.getInstance(), () -> {
            player.setPlayerListName(Utilities.colourise(Colour.playerNameColour(player) + player.getName()));
          }, 20L * 5L, 20L * 60L);
        }
      }
    }

    // Call gui reload method, so item lore is refreshed for each player, as it checks for permissions
    // to decide which lore messages should be applied
    new BukkitRunnable() {
      @Override
      public void run() {
        OmegaNames.getInstance().onGUIReload();
      }
    }.runTaskLaterAsynchronously(OmegaNames.getInstance(), 40);

    // Send the player a message on join if there is an update for the plugin
    if(Utilities.checkPermission(player, true, "omeganames.update")) {
      new UpdateChecker(OmegaNames.getInstance(), 78327).getVersion(version -> {
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
}
