package me.omegaweapondev.omeganames.commands;

import me.omegaweapondev.omeganames.OmegaNames;
import me.omegaweapondev.omeganames.utilities.Colour;
import me.omegaweapondev.omeganames.utilities.MessageHandler;
import me.ou.library.Utilities;
import me.ou.library.commands.PlayerCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NameColour extends PlayerCommand {

  @Override
  protected void execute(Player player, String[] strings) {

    if(strings.length == 0) {
      openNameColourGUI(player);
      return;
    }

    if(strings.length == 2) {
      removeNameColour(player, strings);
    }

    if(strings.length == 3) {
      addNameColour(player, strings);
    }
  }

  private void openNameColourGUI(final Player player) {
    if(!Utilities.checkPermissions(player, true, "omeganames.namecolour.open", "omeganames.*")) {
      Utilities.message(player, MessageHandler.prefix() + " " + MessageHandler.noPermission());
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

    if(!Utilities.checkPermissions(player, true, "omeganames.namecolour.add", "omeganames.*")) {
      Utilities.message(player, MessageHandler.prefix() + " " + MessageHandler.noPermission());
      return;
    }

    if(target == null) {
      Utilities.message(player, MessageHandler.prefix() + " &cSorry, that player does not exist.");
      return;
    }

    if(nameColour.equalsIgnoreCase("")) {
      removeNameColour(player, strings);
      return;
    }

    target.setDisplayName(Utilities.colourise(nameColour + target.getName()));
    OmegaNames.getInstance().getPlayerData().getConfig().set(target.getUniqueId().toString() + ".Name_Colour", nameColour);
    OmegaNames.getInstance().getPlayerData().saveConfig();
    Utilities.message(target, MessageHandler.prefix() + " " + MessageHandler.nameColourApplied(target, nameColour));
  }

  private void removeNameColour(final Player player, final String[] strings) {
    final Player target = Bukkit.getPlayer(strings[1]);

    if(!strings[0].equalsIgnoreCase("remove")) {
      return;
    }

    if(!Utilities.checkPermissions(player, true, "omeganames.namecolour.remove", "omeganames.*")) {
      Utilities.message(player, MessageHandler.prefix() + " " + MessageHandler.noPermission());
      return;
    }

    if(target == null) {
      Utilities.message(player, MessageHandler.prefix() + " &cSorry, that player does not exist.");
      return;
    }

    for(String groupName : OmegaNames.getInstance().getConfigFile().getConfig().getConfigurationSection("Group_Name_Colour.Groups").getKeys(false)) {

      if(Utilities.checkPermission(player, false, "omeganames.namecolour.groups." + groupName)) {
        player.setDisplayName(Utilities.colourise(Colour.groupNameColour(player, groupName) + player.getName()));
        OmegaNames.getInstance().getPlayerData().getConfig().set(player.getUniqueId().toString() + ".Name_Colour", Colour.groupNameColour(player, groupName));
        OmegaNames.getInstance().getPlayerData().saveConfig();
        Utilities.message(player, MessageHandler.prefix() + " " + MessageHandler.nameColourRemoved());
        return;
      }

      player.setDisplayName(Utilities.colourise(Colour.playerNameColour(player) + player.getName()));
      OmegaNames.getInstance().getPlayerData().getConfig().set(player.getUniqueId().toString() + ".Name_Colour", Colour.playerNameColour(player));
      OmegaNames.getInstance().getPlayerData().saveConfig();
      Utilities.message(player, MessageHandler.prefix() + " " + MessageHandler.nameColourRemoved());
    }
  }
}
