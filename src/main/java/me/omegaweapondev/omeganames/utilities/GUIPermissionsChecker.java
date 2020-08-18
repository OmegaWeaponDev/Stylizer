package me.omegaweapondev.omeganames.utilities;

import me.omegaweapondev.omeganames.OmegaNames;
import me.ou.library.Utilities;
import org.bukkit.entity.Player;

public class GUIPermissionsChecker {

  public static void nameColourPermsCheck(final Player player, final String name, final String colour) {

    if(OmegaNames.getInstance().getConfigFile().getConfig().getBoolean("Per_Name_Colour_Permissions")) {
      if(Utilities.checkPermissions(player, true, ("omeganames.namecolour.colour." + name).replace(" ", "").toLowerCase(), "omeganames.namecolour.colour.all", "omeganames.admin")) {

        player.setDisplayName(Utilities.colourise(colour + player.getName() + "&r"));

        if(!OmegaNames.getInstance().getPlayerData().getConfig().isConfigurationSection(player.getUniqueId().toString())) {
          OmegaNames.getInstance().getPlayerData().getConfig().createSection(player.getUniqueId().toString());
        }

        OmegaNames.getInstance().getPlayerData().getConfig().set(player.getUniqueId().toString() + ".Name_Colour", colour);
        OmegaNames.getInstance().getPlayerData().saveConfig();

        Utilities.message(player, MessageHandler.prefix() + " " + MessageHandler.nameColourApplied(player, name + player.getName()));
      } else {
        Utilities.message(player, MessageHandler.prefix() + " " + MessageHandler.noPermission());
      }
    } else {
      if(Utilities.checkPermissions(player, true, "omeganames.namecolour.colours", "omeganames.admin")) {
        player.setDisplayName(Utilities.colourise(colour + player.getName() + "&r"));

        OmegaNames.getInstance().getPlayerData().getConfig().set(player.getUniqueId().toString() + ".Name_Colour", colour);
        OmegaNames.getInstance().getPlayerData().saveConfig();

        Utilities.message(player, MessageHandler.prefix() + " " + MessageHandler.nameColourApplied(player, name + player.getName()));
      } else {
        Utilities.message(player, MessageHandler.prefix() + " " + MessageHandler.noPermission());
      }
    }
  }
}
