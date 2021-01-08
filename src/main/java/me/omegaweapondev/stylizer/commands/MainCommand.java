package me.omegaweapondev.omeganames.commands;

import me.omegaweapondev.omeganames.OmegaNames;
import me.omegaweapondev.omeganames.utilities.MessageHandler;
import me.ou.library.Utilities;
import me.ou.library.commands.GlobalCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class MainCommand extends GlobalCommand {
  private final MessageHandler messagesFile = new MessageHandler(OmegaNames.getInstance().getMessagesFile().getConfig());
  private final String versionMessage = messagesFile.getPrefix() + "&bOmegaNames &cv" + OmegaNames.getInstance().getDescription().getVersion() + "&b By OmegaWeaponDev";

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

      if(!Utilities.checkPermissions(player, true, "omeganames.reload", "omeganames.admin")) {
        Utilities.message(player, messagesFile.string("No_Permission", "&cSorry, you do not have permission for that."));
        return;
      }

      OmegaNames.getInstance().onReload();
      Utilities.message(player, messagesFile.string("Reload_Message", "&bOmegaNames has successfully reloaded."));
      return;
    }

    if(sender instanceof ConsoleCommandSender) {
      OmegaNames.getInstance().onReload();
      Utilities.logInfo(true, messagesFile.console("Reload_Message", "OmegaNames has successfully reloaded."));
    }
  }

  private void helpCommand(final CommandSender sender) {
    if(sender instanceof Player) {
      Player player = (Player) sender;

      Utilities.message(player,
        messagesFile.getPrefix() + "&bReload Command: &c/omeganames reload",
        messagesFile.getPrefix() + "&bVersion Command: &c/omeganames version",
        messagesFile.getPrefix() + "&bName colour command: &c/namecolour"
      );
      return;
    }

    if(sender instanceof ConsoleCommandSender){
      Utilities.logInfo(true,
        "&bReload Command: &c/omeganames reload",
        "&bVersion Command: &c/omeganames version"
      );
    }
  }

  private void versionCommand(final CommandSender sender) {
    if(sender instanceof Player) {
      Player player = (Player) sender;

      Utilities.message(player, versionMessage);
      return;
    }

    if(sender instanceof ConsoleCommandSender) {
      Utilities.logInfo(true, ChatColor.stripColor(versionMessage));
    }
  }

  private void invalidArgsCommand(final CommandSender sender) {
    if(sender instanceof Player) {
      Player player = (Player) sender;

      Utilities.message(player,
        versionMessage,
        messagesFile.getPrefix() + "&bReload Command: &c/omeganames reload",
        messagesFile.getPrefix() + "&bVersion Command: &c/omeganames version",
        messagesFile.getPrefix() + "&bName colour command: &c/namecolour"
      );
      return;
    }

    if(sender instanceof ConsoleCommandSender) {
      Utilities.logInfo(true,
        ChatColor.stripColor(versionMessage),
        "Reload Command: /omeganames reload",
        "Version Command: /omeganames version"
      );
    }
  }
}
