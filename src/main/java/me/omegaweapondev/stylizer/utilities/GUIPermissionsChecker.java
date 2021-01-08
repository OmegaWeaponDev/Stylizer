package me.omegaweapondev.omeganames.utilities;

import me.omegaweapondev.omeganames.OmegaNames;
import me.ou.library.Utilities;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class GUIPermissionsChecker {
  private final MessageHandler messagesFile = new MessageHandler(OmegaNames.getInstance().getMessagesFile().getConfig());
  private final FileConfiguration playerData = OmegaNames.getInstance().getPlayerData().getConfig();

  private final Player player;
  private final String name;
  private final String colour;

  public GUIPermissionsChecker(final Player player, final String name, final String colour) {
    this.player = player;
    this.name = name;
    this.colour = colour;
  }

  public void nameColourPermsCheck() {

    if(OmegaNames.getInstance().getConfigFile().getConfig().getBoolean("Per_Name_Colour_Permissions")) {
      perNameColourPerms();
      return;
    }

    if(!Utilities.checkPermissions(player, true, "omeganames.namecolour.colours", "omeganames.admin")) {
      Utilities.message(player, messagesFile.string("No_Permission", "&cSorry, you do not have permission to do that."));
      return;
    }

    player.setDisplayName(Utilities.colourise(colour + player.getName() + "&r"));

    playerData.set(player.getUniqueId().toString() + ".Name_Colour", colour);
    OmegaNames.getInstance().getPlayerData().saveConfig();

    Utilities.message(player, messagesFile.string("Name_Colour_Applied", "&bYour name colour has been changed to: %namecolour%").replace("%namecolour%", colour + player.getName()));
  }

  private void perNameColourPerms() {
    if(!Utilities.checkPermissions(player, true, "omeganames.namecolour.colour." + name.replace(" ", "").toLowerCase(), "omeganames.namecolour.colour.all", "omeganames.admin")) {
      Utilities.message(player, messagesFile.string("No_Permission", "&cSorry, you do not have permission to do that."));
      return;
    }

    player.setDisplayName(Utilities.colourise(colour + player.getName() + "&r"));

    if(!playerData.isConfigurationSection(player.getUniqueId().toString())) {
      OmegaNames.getInstance().getPlayerData().getConfig().createSection(player.getUniqueId().toString());
    }

    playerData.set(player.getUniqueId().toString() + ".Name_Colour", colour);
    OmegaNames.getInstance().getPlayerData().saveConfig();

    Utilities.message(player, messagesFile.string("Name_Colour_Applied", "&bYour name colour has been changed to: %namecolour%").replace("%namecolour%", colour + player.getName()));
  }
}
