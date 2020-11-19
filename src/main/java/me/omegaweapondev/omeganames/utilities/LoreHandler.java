package me.omegaweapondev.omeganames.utilities;

import me.omegaweapondev.omeganames.OmegaNames;
import me.ou.library.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoreHandler {
  private final MessageHandler messageFile = new MessageHandler(OmegaNames.getInstance().getMessagesFile().getConfig());

  private final String colourName;
  private final String colourCode;

  public LoreHandler(final String colourName, final String colourCode) {
    this.colourName = colourName;
    this.colourCode = colourCode.replace(" ", "").toLowerCase();
  }


  public List<String> colourLoreMessage() {

    final List<String> colouredLore = Utilities.colourise(messageFile.stringList("Name_Colour_GUI.Colour_Lore".replace("%namecolour%", colourCode + colourName), Arrays.asList("&cClick here to change", "&cyour name colour to", "%namecolour%".replace("%namecolour%", colourCode + colourName))));

    List<String> formattedLore = new ArrayList<>();

    for(String line : colouredLore) {
      formattedLore.add(line.replace("%namecolour%", colourCode + colourName));
    }

    return Utilities.colourise(formattedLore);
  }
}
