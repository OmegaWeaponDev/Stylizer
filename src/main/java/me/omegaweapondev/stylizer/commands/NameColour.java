package me.omegaweapondev.stylizer.commands;

import me.omegaweapondev.stylizer.Stylizer;
import me.omegaweapondev.stylizer.utilities.MessageHandler;
import me.ou.library.Utilities;
import me.ou.library.commands.PlayerCommand;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class NameColour extends PlayerCommand {
  private final Stylizer plugin;

  private final MessageHandler messageHandler;
  private final FileConfiguration playerData;
  private final FileConfiguration configFile;

  public NameColour(final Stylizer plugin) {
    this.plugin = plugin;
    messageHandler = new MessageHandler(plugin, plugin.getMessagesFile().getConfig());
    playerData = plugin.getPlayerData().getConfig();
    configFile = plugin.getConfigFile().getConfig();
  }

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
    if(!Utilities.checkPermissions(player, true, "stylizer.namecolour.open", "stylizer.admin")) {
      Utilities.message(player, messageHandler.string("No_Permission", "&cSorry, you do not have permission to do that."));
      return;
    }

    plugin.getNameColourGUI().openInventory(player);
  }

  private void addNameColour(final Player player, final String[] strings) {
    final Player target = Bukkit.getPlayer(strings[1]);
    final String nameColour = strings[2];

    if(!strings[0].equalsIgnoreCase("add")) {
      return;
    }

    if(!Utilities.checkPermissions(player, true, "stylizer.namecolour.add", "stylizer.admin")) {
      Utilities.message(player, messageHandler.string("No_Permission", "&cSorry, you do not have permission to do that."));
      return;
    }

    if(target == null) {
      Utilities.message(player, messageHandler.string("Invalid_Player", "&cSorry, that player cannot be found."));
      return;
    }

    if(nameColour.equalsIgnoreCase("")) {
      removeNameColour(player, strings);
      return;
    }

    target.setDisplayName(Utilities.colourise(nameColour + target.getName()));
    playerData.set(target.getUniqueId().toString() + ".Name_Colour", nameColour);
    plugin.getPlayerData().saveConfig();
    Utilities.message(target, messageHandler.string("Name_Colour_Applied", "&bYour name colour has been changed to: %namecolour%").replace("%namecolour%", nameColour + player.getName()));
  }

  private void removeNameColour(final Player player, final String[] strings) {
    final Player target = Bukkit.getPlayer(strings[1]);

    if(!strings[0].equalsIgnoreCase("remove")) {
      return;
    }

    if(!Utilities.checkPermissions(player, true, "stylizer.namecolour.remove", "stylizer.admin")) {
      Utilities.message(player, messageHandler.string("No_Permission", "&cSorry, you do not have permission to do that."));
      return;
    }

    if(target == null) {
      Utilities.message(player, messageHandler.string("Invalid_Player", "&cSorry, that player cannot be found."));
      return;
    }

    for(String groupName : configFile.getConfigurationSection("Group_Name_Colour.Groups").getKeys(false)) {

      if(Utilities.checkPermission(player, false, "stylizer.namecolour.groups." + groupName)) {
        player.setDisplayName(Utilities.colourise(configFile.getString("Group_Name_Colour.Groups." + groupName) + player.getName()));
        playerData.set(player.getUniqueId().toString() + ".Name_Colour", plugin.getConfigFile().getConfig().getString("Group_Name_Colour.Groups." + groupName));
        plugin.getPlayerData().saveConfig();
        Utilities.message(target, messageHandler.string("Name_Colour_Removed", "&cYour name colour has been reverted to the default colour"));
        return;
      }

      player.setDisplayName(Utilities.colourise(configFile.getString("Default_Name_Colour", "&e") + player.getName()));
      playerData.set(player.getUniqueId().toString() + ".Name_Colour", configFile.getString("Default_Name_Colour", "&e"));
      plugin.getPlayerData().saveConfig();
      Utilities.message(target, messageHandler.string("Name_Colour_Removed", "&cYour name colour has been reverted to the default colour"));
    }
  }

  private void customNameColour(final Player player, final String[] strings) {

    if(!strings[0].equalsIgnoreCase("custom")) {
      return;
    }

    if(!Utilities.checkPermissions(player, true, "stylizer.namecolour.custom", "stylizer.admin")) {
      Utilities.message(player, messageHandler.string("No_Permission", "&cSorry, you do not have permission to do that."));
      return;
    }

    final String customColour = strings[1];

    if(strings[1].equalsIgnoreCase("")) {
      return;
    }

    player.setDisplayName(Utilities.colourise(customColour));
    playerData.set(player.getUniqueId().toString() + ".Name_Colour", customColour);
    plugin.getPlayerData().saveConfig();
    Utilities.message(player, "Name_Colour_Applied".replace("%namecolour%", player.getDisplayName()));
  }
}
