package me.omegaweapondev.omeganames.utilities;

import me.omegaweapondev.omeganames.OmegaNames;
import me.ou.library.Utilities;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoreHandler {
  private final MessageHandler messageFile = new MessageHandler(OmegaNames.getInstance().getMessagesFile().getConfig());
  private final FileConfiguration configFile = OmegaNames.getInstance().getConfigFile().getConfig();

  private final Player player;
  private final String colourName;
  private final String colourCode;

  public LoreHandler(final Player player, final String colourName, final String colourCode) {
    this.player = player;
    this.colourName = colourName.toLowerCase();
    this.colourCode = colourCode.replace(" ", "").toLowerCase();
  }

  public List<String> permLoreChecker() {

    if(configFile.getBoolean("Per_Name_Colour_Permissions")) {

      if(!Utilities.checkPermissions(player, true, "omeganames.namecolour.colour." + colourName, "omeganames.namecolour.colour.all", "omeganames.admin")) {
        return Utilities.colourise(messageFile.stringList("Name_Colour_GUI.No_Permission_Lore", Arrays.asList("&cYou currently do not", "&chave permission to", "&cuse this name colour")));
      }

      return colourLoreMessage();
    }

    if(Utilities.checkPermissions(player, true, "omeganames.namecolour.colours", "omeganames.admin")) {
      return colourLoreMessage();
    }

    return Utilities.colourise(messageFile.stringList("Name_Colour_GUI.No_Permission_Lore", Arrays.asList("&cYou currently do not", "&chave permission to", "&cuse this name colour")));
  }

  private List<String> colourLoreMessage() {

    final List<String> colouredLore = Utilities.colourise(messageFile.stringList("Name_Colour_GUI.Colour_Lore".replace("%namecolour%", colourCode + player.getName()), Arrays.asList("&cClick here to change", "&cyour name colour to", "%namecolour%".replace("%namecolour%", colourCode + player.getName()))));

    if(!configFile.getBoolean("Per_Name_Colour_Permissions")) {
      if(Utilities.checkPermissions(player, true, "omeganames.namecolour.colours", "omeganames.admin")) {
        List<String> formattedLore = new ArrayList<>();

        for(String line : colouredLore) {
          formattedLore.add(line.replace("%namecolour%", colourCode + player.getName()));
        }

        return Utilities.colourise(formattedLore);
      }
    }

    if(Utilities.checkPermissions(player, true, "omeganames.namecolour.colour." + colourName, "omeganames.admin")) {
      List<String> formattedLore = new ArrayList<>();

      for(String line : colouredLore) {
        formattedLore.add(line.replace("%namecolour%", colourCode + player.getName()));
      }

      return Utilities.colourise(formattedLore);
    }
    return null;
  }
}
