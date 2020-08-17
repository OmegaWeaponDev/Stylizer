package me.omegaweapondev.omeganames.utilities;

import me.omegaweapondev.omeganames.OmegaNames;
import me.ou.library.Utilities;
import org.bukkit.entity.Player;

import java.util.List;

public class LoreHandler {

  public static List<String> noPermissionLore(final Player player, String colourName, final String colourCode) {
    colourName = colourName.replace(" ", "").toLowerCase();

    if(OmegaNames.getInstance().getConfigFile().getConfig().getBoolean("Per_Name_Colour_Permissions")) {

      if(player.hasPermission("omeganames.namecolour.colour." + colourName) || player.hasPermission("omeganames.namecolour.colour.*") || player.isOp()) {
        return colourLoreMessage(player, colourName, colourCode);
      } else {
        return Utilities.colourise(MessageHandler.nameColourNoPermissionLore());
      }
    } else {
      if (player.hasPermission("omeganames.namecolour.colours") || player.isOp()) {
        return colourLoreMessage(player, colourName, colourCode);
      } else {
        return Utilities.colourise(MessageHandler.nameColourNoPermissionLore());
      }
    }
  }

  public static List<String> colourLoreMessage(final Player player, String colourName, final String colourCode) {
    colourName = colourName.toLowerCase();

    if(OmegaNames.getInstance().getConfigFile().getConfig().getBoolean("Per_Name_Colour_Permissions")) {
      if(player.hasPermission("omeganames.namecolour.colour." + colourName) || player.isOp()) {
        return Utilities.colourise(MessageHandler.nameColourItemLore(colourCode + player.getName()));
      }
    } else {
      if(player.hasPermission("omeganames.namecolour.colours") || player.isOp()) {
        return Utilities.colourise(MessageHandler.nameColourItemLore(colourCode + player.getName()));
      }
    }
    return null;
  }
}
