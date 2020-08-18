package me.omegaweapondev.omeganames.commands;

import me.omegaweapondev.omeganames.OmegaNames;
import me.omegaweapondev.omeganames.utilities.MessageHandler;
import me.ou.library.Utilities;
import me.ou.library.commands.GlobalCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainCommand extends GlobalCommand {

  @Override
  protected void execute(final CommandSender sender, final String[] strings) {

    if(strings.length == 0) {
      invalidArgsCommand(sender);
      return;
    }

    switch (strings[0]) {
      case "version":
        versionCommand(sender);
        break;
      case "help":
        helpCommand(sender);
        break;
      case "reload":
        reloadCommand(sender);
        break;
      default:
        invalidArgsCommand(sender);
        break;
    }
  }

  private void reloadCommand(final CommandSender sender) {
    if(sender instanceof Player) {
      Player player = (Player) sender;

      if(Utilities.checkPermissions(player, true, "omeganames.reload", "omeganames.admin")) {
        OmegaNames.getInstance().onReload();
        Utilities.message(player, MessageHandler.prefix() + " " + MessageHandler.reloadMessage());
      } else {
        Utilities.message(player, MessageHandler.prefix() + " " + MessageHandler.noPermission());
      }
    } else {
      OmegaNames.getInstance().onReload();
      Utilities.logInfo(true, MessageHandler.reloadMessage());
    }
  }

  private void helpCommand(final CommandSender sender) {
    if(sender instanceof Player) {
      Player player = (Player) sender;

      Utilities.message(player,
        MessageHandler.prefix() + " &bReload Command: &c/omeganames reload",
        MessageHandler.prefix() + " &bVersion Command: &c/omeganames version",
        MessageHandler.prefix() + " &bName colour command: &c/namecolour"
      );
    } else {
      Utilities.logInfo(true,
        "&bReload Command: &c/omeganames reload",
        "&bVersion Command: &c/omeganames version"
      );
    }
  }

  private void versionCommand(final CommandSender sender) {
    if(sender instanceof Player) {
      Player player = (Player) sender;

      Utilities.message(player, MessageHandler.prefix() + " &bCurrently running version: &c" + OmegaNames.getInstance().getDescription().getVersion());
    } else {
      Utilities.logInfo(true, "&bCurrently running version: &c" + OmegaNames.getInstance().getDescription().getVersion());
    }
  }

  private void invalidArgsCommand(final CommandSender sender) {
    if(sender instanceof Player) {
      Player player = (Player) sender;

      Utilities.message(player,
        MessageHandler.prefix() + " &bCurrently running version: &c" + OmegaNames.getInstance().getDescription().getVersion(),
        MessageHandler.prefix() + " &bReload Command: &c/omeganames reload",
        MessageHandler.prefix() + " &bVersion Command: &c/omeganames version",
        MessageHandler.prefix() + " &bName colour command: &c/namecolour"
      );
    } else {
      Utilities.logInfo(true,
        "&bCurrently running version: &c" + OmegaNames.getInstance().getDescription().getVersion(),
        "&bReload Command: &c/omeganames reload",
        "&bVersion Command: &c/omeganames version"
      );
    }
  }
}
