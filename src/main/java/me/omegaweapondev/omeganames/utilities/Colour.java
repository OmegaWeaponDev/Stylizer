package me.omegaweapondev.omeganames.utilities;

import me.omegaweapondev.omeganames.OmegaNames;
import me.ou.library.Utilities;
import org.bukkit.entity.Player;

public class Colour {

  public static String groupNameColour(final Player player, final String groupName) {
    final boolean isconfigGroupNameColour = OmegaNames.getInstance().getConfigFile().getConfig().isBoolean("Group_Name_Colour.Enabled");
    final String configGroupNameColour = OmegaNames.getInstance().getConfigFile().getConfig().getString("Group_Name_Colour.Groups." + groupName);

    final boolean isPlayerDataNameColour = OmegaNames.getInstance().getPlayerData().getConfig().isSet(player.getUniqueId().toString() + ".Name_Colour");
    final String playerDataNameColour = OmegaNames.getInstance().getPlayerData().getConfig().getString(player.getUniqueId().toString() + ".Name_Colour");

    String finalNameColour;

    // Check if a custom player namecolour has been set
    if(isPlayerDataNameColour) {
      finalNameColour = playerDataNameColour;

      return finalNameColour;
    }

    // Checks if config group namecolours have been set
    if(Utilities.checkPermission(player, true,"omeganames.namecolour.groups." + groupName.toLowerCase()) && isconfigGroupNameColour) {
      finalNameColour = configGroupNameColour;

      return finalNameColour;
    } else {
      return playerNameColour(player);
    }
  }

  public static String playerNameColour(final Player player) {
    final boolean isPlayerDataNameColour = OmegaNames.getInstance().getPlayerData().getConfig().isSet(player.getUniqueId().toString() + ".Name_Colour");
    final String playerDataNameColour = OmegaNames.getInstance().getPlayerData().getConfig().getString(player.getUniqueId().toString() + ".Name_Colour");

    final String configNameColour = MessageHandler.defaultNameColour();

    String finalNameColour;

    // Check if a custom player namecolour has been set
    if(isPlayerDataNameColour) {
      finalNameColour = playerDataNameColour;
      return finalNameColour;
    }

    // No custom namecolour, so uses the default namecolour
    finalNameColour = configNameColour;
    return finalNameColour;
  }
}
