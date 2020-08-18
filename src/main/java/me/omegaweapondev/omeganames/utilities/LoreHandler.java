package me.omegaweapondev.omeganames.utilities;

import me.omegaweapondev.omeganames.OmegaNames;
import me.ou.library.Utilities;
import org.bukkit.entity.Player;

import java.util.List;

public class LoreHandler {

  public static List<String> noPermissionLore(final Player player, String colourName, final String colourCode) {
    colourName = colourName.replace(" ", "").toLowerCase();

    if(OmegaNames.getInstance().getConfigFile().getConfig().getBoolean("Per_Name_Colour_Permissions")) {

      if(Utilities.checkPermissions(player, true, "omeganames.namecolour.colour." + colourName, "omeganames.namecolour.colour.all", "omeganames.admin")) {
        return colourLoreMessage(player, colourName, colourCode);
      } else {
        return Utilities.colourise(MessageHandler.nameColourNoPermissionLore());
      }
    } else {
      if (Utilities.checkPermissions(player, true, "omeganames.namecolour.colours", "omeganames.admin")) {
        return colourLoreMessage(player, colourName, colourCode);
      } else {
        return Utilities.colourise(MessageHandler.nameColourNoPermissionLore());
      }
    }
  }

  public static List<String> colourLoreMessage(final Player player, String colourName, final String colourCode) {
    colourName = colourName.toLowerCase();

    if(OmegaNames.getInstance().getConfigFile().getConfig().getBoolean("Per_Name_Colour_Permissions")) {
      if(Utilities.checkPermissions(player, true, "omeganames.namecolour.colour." + colourName, "omeganames.admin")) {
        return Utilities.colourise(MessageHandler.nameColourItemLore(colourCode + player.getName()));
      }
    } else {
      if(Utilities.checkPermissions(player, true, "omeganames.namecolour.colours", "omeganames.admin")) {
        return Utilities.colourise(MessageHandler.nameColourItemLore(colourCode + player.getName()));
      }
    }
    return null;
  }
}
