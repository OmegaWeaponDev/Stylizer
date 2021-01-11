package me.omegaweapondev.stylizer.commands;

import me.omegaweapondev.stylizer.Stylizer;
import me.omegaweapondev.stylizer.utilities.MessageHandler;
import me.ou.library.Utilities;
import me.ou.library.commands.GlobalCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class MainCommand extends GlobalCommand {
  private final Stylizer plugin;
  private final MessageHandler messageHandler;
  private final String versionMessage;

  public MainCommand(final Stylizer plugin) {
    this.plugin = plugin;
    messageHandler = new MessageHandler(plugin, plugin.getMessagesFile().getConfig());
    versionMessage = messageHandler.getPrefix() + "&bStylizer &cv" + plugin.getDescription().getVersion() + "&b By OmegaWeaponDev";
  }

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

      if(!Utilities.checkPermissions(player, true, "stylizer.reload", "stylizer.admin")) {
        Utilities.message(player, messageHandler.string("No_Permission", "&cSorry, you do not have permission for that."));
        return;
      }

      plugin.onReload();
      Utilities.message(player, messageHandler.string("Reload_Message", "&bOmegaNames has successfully reloaded."));
      return;
    }

    if(sender instanceof ConsoleCommandSender) {
      plugin.onReload();
      Utilities.logInfo(true, messageHandler.console("Reload_Message", "OmegaNames has successfully reloaded."));
    }
  }

  private void helpCommand(final CommandSender sender) {
    if(sender instanceof Player) {
      Player player = (Player) sender;

      Utilities.message(player,
        messageHandler.getPrefix() + "&bReload Command: &c/stylizer reload",
        messageHandler.getPrefix() + "&bVersion Command: &c/stylizer version",
        messageHandler.getPrefix() + "&bName colour command: &c/namecolour",
        messageHandler.getPrefix() + "&bDebug Command: &c/stylizerdebug",
        messageHandler.getPrefix() + "&bItem Namer Command: &c/itemnamer <option> <value>"
      );
      return;
    }

    if(sender instanceof ConsoleCommandSender){
      Utilities.logInfo(true,
        "&bReload Command: &c/stylizer reload",
        "&bVersion Command: &c/stylizer version"
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
        messageHandler.getPrefix() + "&bReload Command: &c/stylizer reload",
        messageHandler.getPrefix() + "&bVersion Command: &c/stylizer version",
        messageHandler.getPrefix() + "&bName colour command: &c/namecolour",
        messageHandler.getPrefix() + "&bDebug Command: &c/stylizerdebug",
        messageHandler.getPrefix() + "&bItem Namer Command: &c/itemnamer <option> <value>"
      );
      return;
    }

    if(sender instanceof ConsoleCommandSender) {
      Utilities.logInfo(true,
        ChatColor.stripColor(versionMessage),
        "Reload Command: /stylizer reload",
        "Version Command: /stylizer version",
        "Debug Command: /stylizerdebug",
        "Item Namer Command: /itemnamer <option> <value>"
      );
    }
  }
}
