package me.omegaweapondev.stylizer.commands;

import me.omegaweapondev.stylizer.Stylizer;
import me.omegaweapondev.stylizer.utilities.MessageHandler;
import me.ou.library.Utilities;
import me.ou.library.commands.GlobalCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ClearLog extends GlobalCommand {
  private final Stylizer plugin;
  private final MessageHandler messageHandler;
  private final FileConfiguration chatLog;

  public ClearLog(final Stylizer plugin) {
    this.plugin = plugin;

    messageHandler = new MessageHandler(plugin, plugin.getMessagesFile().getConfig());
    chatLog = plugin.getChatlog().getConfig();
  }

  @Override
  protected void execute(CommandSender commandSender, String[] strings) {
    if(!(commandSender instanceof Player)) {
      chatLog.set("Chat_Log", null);
      plugin.getChatlog().saveConfig();
      return;
    }

    final Player player = (Player) commandSender;

    if(!Utilities.checkPermissions(player, true, "stylizer.clearlog", "stylizer.admin")) {
      Utilities.message(player, messageHandler.string("No_Permission", "#570000I'm sorry, but you do not have permission to do that!"));
      return;
    }

    chatLog.set("Chat_Log", null);
    plugin.getChatlog().saveConfig();
    Utilities.message(player, messageHandler.string("Clear_Chat_Log", "#14abc9You have cleared the chat log."));
  }
}
