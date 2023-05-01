package me.omegaweapondev.stylizer.commands;

import me.omegaweapondev.stylizer.Stylizer;
import me.omegaweapondev.stylizer.utilities.MessageHandler;
import me.ou.library.Utilities;
import me.ou.library.builders.TabCompleteBuilder;
import me.ou.library.commands.GlobalCommand;
import me.ou.library.menus.MenuCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;

public class MainCommand extends GlobalCommand implements TabCompleter {
  private final Stylizer plugin;
  private final MessageHandler messageHandler;
  private final String versionMessage;
  private final FileConfiguration chatLog;

  public MainCommand(final Stylizer plugin) {
    this.plugin = plugin;
    messageHandler = new MessageHandler(plugin, plugin.getSettingsHandler().getMessagesFile().getConfig());
    versionMessage = messageHandler.getPrefix() + "&bStylizer &cv" + plugin.getDescription().getVersion() + "&b By OmegaWeaponDev";
    chatLog = plugin.getSettingsHandler().getChatLog().getConfig();
  }

  @Override
  protected void execute(final CommandSender sender, final String [] strings) {

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
      case "debug":
        debugCommand(sender);
        break;
      case "clearlog":
        clearLogCommand(sender);
      default:
        invalidArgsCommand(sender);
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
        messageHandler.getPrefix() + "&bClear Chat Log Command: &c/stylizer clearlog",
        messageHandler.getPrefix() + "&bName colour command: &c/namecolour",
        messageHandler.getPrefix() + "&bChat colour command: &c/chatcolour",
        messageHandler.getPrefix() + "&bDebug Command: &c/stylizerdebug",
        messageHandler.getPrefix() + "&bItem Namer Command: &c/itemnamer <option> <value>",
        messageHandler.getPrefix() + "&bPrivate message Command: &c/privatemessage <player> <command>",
        messageHandler.getPrefix() + "&bReply Command: &c/reply <message>"
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

  private void clearLogCommand(final CommandSender sender) {
    if(!(sender instanceof Player)) {
      chatLog.set("Chat_Log", null);
      plugin.getSettingsHandler().getChatLog().saveConfig();
      return;
    }

    final Player player = (Player) sender;

    if(!Utilities.checkPermissions(player, true, "stylizer.clearlog", "stylizer.admin")) {
      Utilities.message(player, messageHandler.string("No_Permission", "#570000I'm sorry, but you do not have permission to do that!"));
      return;
    }

    chatLog.set("Chat_Log", null);
    plugin.getSettingsHandler().getChatLog().saveConfig();
    Utilities.message(player, messageHandler.string("Clear_Chat_Log", "#14abc9You have cleared the chat log."));
  }

  private void invalidArgsCommand(final CommandSender sender) {
    if(sender instanceof Player) {
      Player player = (Player) sender;

      Utilities.message(player,
        versionMessage,
        messageHandler.getPrefix() + "&bReload Command: &c/stylizer reload",
        messageHandler.getPrefix() + "&bVersion Command: &c/stylizer version",
        messageHandler.getPrefix() + "&bClear Chat Log Command: &c/stylizer clearlog",
        messageHandler.getPrefix() + "&bName colour command: &c/namecolour",
        messageHandler.getPrefix() + "&bChat colour command: &c/chatcolour",
        messageHandler.getPrefix() + "&bDebug Command: &c/stylizer debug",
        messageHandler.getPrefix() + "&bItem Namer Command: &c/itemnamer <option> <value>",
        messageHandler.getPrefix() + "&bPrivate message Command: &c/privatemessage <player> <command>",
        messageHandler.getPrefix() + "&bReply Command: &c/reply <message>"
      );
      return;
    }

    if(sender instanceof ConsoleCommandSender) {
      Utilities.logInfo(true,
        ChatColor.stripColor(versionMessage),
        "Reload Command: /stylizer reload",
        "Version Command: /stylizer version",
        "Debug Command: /stylizer debug",
        "Clear Log Command: /stylizer clearlog",
        "Item Namer Command: /itemnamer <option> <value>"
      );
    }
  }

  private void debugCommand(CommandSender commandSender) {
    if (commandSender instanceof Player) {
      Player player = (Player) commandSender;

      debugPlayer(player);
      return;
    }
    debugConsole();
  }

  private void debugPlayer(final Player player) {

    if(!Utilities.checkPermissions(player, true, "stylizer.debug", "stylizer.admin")) {
      Utilities.message(player, messageHandler.string("No_Permission", "#570000I'm sorry, but you do not have permission to do that!"));
      return;
    }

    StringBuilder plugins = new StringBuilder();

    for(Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
      plugins.append("#ff4a4a").append(plugin.getName()).append(" ").append(plugin.getDescription().getVersion()).append("#14abc9, ");
    }

    Utilities.message(player,
            "#14abc9===========================================",
            " #6928f7Stylizer #ff4a4av" + plugin.getDescription().getVersion() + " #14abc9By OmegaWeaponDev",
            "#14abc9===========================================",
            " #14abc9Server Brand: #ff4a4a" + Bukkit.getName(),
            " #14abc9Server Version: #ff4a4a" + Bukkit.getServer().getVersion(),
            " #14abc9Online Mode: #ff4a4a" + Bukkit.getOnlineMode(),
            " #14abc9Players Online: #ff4a4a" + Bukkit.getOnlinePlayers().size() + " / " + Bukkit.getMaxPlayers(),
            " #14abc9Stylizer Commands: #ff4a4a" + Utilities.setCommand().size() + " / 4 #14abc9registered",
            " #14abc9Open Inventories: #ff4a4a" + MenuCreator.getOpenInventories().size(),
            " #14abc9Unique Inventories: #ff4a4a" + MenuCreator.getInventoriesByUUID().size(),
            " #14abc9Currently Installed Plugins...",
            " " + plugins,
            "#14abc9==========================================="
    );
  }

  private void debugConsole() {
    StringBuilder plugins = new StringBuilder();

    for(Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
      plugins.append(plugin.getName()).append(" ").append(plugin.getDescription().getVersion()).append(", ");
    }

    Utilities.logInfo(true,
            "===========================================",
            " Stylizer v" + plugin.getDescription().getVersion() + " By OmegaWeaponDev",
            "===========================================",
            " Server Brand: " + Bukkit.getName(),
            " Server Version: " + Bukkit.getServer().getVersion(),
            " Online Mode: " + Bukkit.getServer().getOnlineMode(),
            " Players Online: " + Bukkit.getOnlinePlayers().size() + " / " + Bukkit.getMaxPlayers(),
            " Stylizer Commands: " + Utilities.setCommand().size() + " / 4 registered",
            " Open Inventories: " + MenuCreator.getOpenInventories().size(),
            " Unique Inventories: " + MenuCreator.getInventoriesByUUID().size(),
            " Currently Installed Plugins...",
            " " + plugins,
            "==========================================="
    );
  }

  @Override
  public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
    if(strings.length <= 1) {
      return new TabCompleteBuilder(commandSender)
        .checkCommand("version", true, "stylizer.admin")
        .checkCommand("help", true, "stylizer.admin")
        .checkCommand("reload", true, "stylizer.reload", "stylizer.admin")
        .checkCommand("debug", true, "stylizer.debug", "stylizer.reload")
        .checkCommand("clearlog", true, "stylizer.clearlog", "stylizer.admin")
        .build(strings[0]);
    }

    return Collections.emptyList();
  }
}
