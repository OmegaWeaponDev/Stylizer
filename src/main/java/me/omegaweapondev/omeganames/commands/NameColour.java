package me.omegaweapondev.omeganames.commands;

import me.omegaweapondev.omeganames.OmegaNames;
import me.omegaweapondev.omeganames.utilities.MessageHandler;
import me.ou.library.Utilities;
import me.ou.library.commands.PlayerCommand;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class NameColour extends PlayerCommand {
  private final MessageHandler messagesFile = new MessageHandler(OmegaNames.getInstance().getMessagesFile().getConfig());
  private final FileConfiguration playerData = OmegaNames.getInstance().getPlayerData().getConfig();
  private final FileConfiguration configFile = OmegaNames.getInstance().getConfigFile().getConfig();

  @Override
  protected void execute(final Player player, final String[] strings) {

    if(strings.length == 0) {
      openNameColourGUI(player);
      return;
    }

    if(strings.length == 2) {
      switch(strings[0]) {
        case "remove":
          removeNameColour(player, strings);
          break;
        case "custom":
          customNameColour(player, strings);
          break;
        default:
          break;
      }
    }

    if(strings.length == 3) {
      addNameColour(player, strings);
    }
  }

  private void openNameColourGUI(final Player player) {
    if(!Utilities.checkPermissions(player, true, "omeganames.namecolour.open", "omeganames.admin")) {
      Utilities.message(player, messagesFile.string("No_Permission", "&cSorry, you do not have permission to do that."));
      return;
    }

    OmegaNames.getInstance().getNameColourGUI().openInventory(player);
  }

  private void addNameColour(final Player player, final String[] strings) {
    final Player target = Bukkit.getPlayer(strings[1]);
    final String nameColour = strings[2];

    if(!strings[0].equalsIgnoreCase("add")) {
      return;
    }

    if(!Utilities.checkPermissions(player, true, "omeganames.namecolour.add", "omeganames.admin")) {
      Utilities.message(player, messagesFile.string("No_Permission", "&cSorry, you do not have permission to do that."));
      return;
    }

    if(target == null) {
      Utilities.message(player, messagesFile.string("Invalid_Player", "&cSorry, that player cannot be found."));
      return;
    }

    if(nameColour.equalsIgnoreCase("")) {
      removeNameColour(player, strings);
      return;
    }

    target.setDisplayName(Utilities.colourise(nameColour + target.getName()));
    playerData.set(target.getUniqueId().toString() + ".Name_Colour", nameColour);
    OmegaNames.getInstance().getPlayerData().saveConfig();
    Utilities.message(target, messagesFile.string("Name_Colour_Applied", "&bYour name colour has been changed to: %namecolour%").replace("%namecolour%", nameColour + player.getName()));
  }

  private void removeNameColour(final Player player, final String[] strings) {
    final Player target = Bukkit.getPlayer(strings[1]);

    if(!strings[0].equalsIgnoreCase("remove")) {
      return;
    }

    if(!Utilities.checkPermissions(player, true, "omeganames.namecolour.remove", "omeganames.admin")) {
      Utilities.message(player, messagesFile.string("No_Permission", "&cSorry, you do not have permission to do that."));
      return;
    }

    if(target == null) {
      Utilities.message(player, messagesFile.string("Invalid_Player", "&cSorry, that player cannot be found."));
      return;
    }

    for(String groupName : configFile.getConfigurationSection("Group_Name_Colour.Groups").getKeys(false)) {

      if(Utilities.checkPermission(player, false, "omeganames.namecolour.groups." + groupName)) {
        player.setDisplayName(Utilities.colourise(configFile.getString("Group_Name_Colour.Groups." + groupName) + player.getName()));
        playerData.set(player.getUniqueId().toString() + ".Name_Colour", OmegaNames.getInstance().getConfigFile().getConfig().getString("Group_Name_Colour.Groups." + groupName));
        OmegaNames.getInstance().getPlayerData().saveConfig();
        Utilities.message(target, messagesFile.string("Name_Colour_Removed", "&cYour name colour has been reverted to the default colour"));
        return;
      }

      player.setDisplayName(Utilities.colourise(configFile.getString("Default_Name_Colour", "&e") + player.getName()));
      playerData.set(player.getUniqueId().toString() + ".Name_Colour", configFile.getString("Default_Name_Colour", "&e"));
      OmegaNames.getInstance().getPlayerData().saveConfig();
      Utilities.message(target, messagesFile.string("Name_Colour_Removed", "&cYour name colour has been reverted to the default colour"));
    }
  }

  private void customNameColour(final Player player, final String[] strings) {

    if(!strings[0].equalsIgnoreCase("custom")) {
      return;
    }

    if(!Utilities.checkPermissions(player, true, "omeganames.namecolour.custom", "omeganames.admin")) {
      Utilities.message(player, messagesFile.string("No_Permission", "&cSorry, you do not have permission to do that."));
      return;
    }

    final String customColour = strings[1];

    if(strings[1].equalsIgnoreCase("")) {
      return;
    }

    player.setDisplayName(Utilities.colourise(customColour));
    playerData.set(player.getUniqueId().toString() + ".Name_Colour", customColour);
    OmegaNames.getInstance().getPlayerData().saveConfig();
    Utilities.message(player, "Name_Colour_Applied".replace("%namecolour%", player.getDisplayName()));
  }
}
