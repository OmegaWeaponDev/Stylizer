package me.omegaweapondev.stylizer.commands;

import me.omegaweapondev.stylizer.Stylizer;
import me.omegaweapondev.stylizer.utilities.MessageHandler;
import me.ou.library.Utilities;
import me.ou.library.commands.GlobalCommand;
import me.ou.library.menus.MenuCreator;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;

public class DebugCommand extends GlobalCommand implements TabCompleter {
  private final Stylizer plugin;
  private final MessageHandler messageHandler;

  public DebugCommand(final Stylizer plugin) {
    this.plugin = plugin;
    messageHandler = new MessageHandler(plugin, plugin.getSettingsHandler().getMessagesFile().getConfig());
  }

  @Override
  protected void execute(CommandSender commandSender, String[] strings) {
    if(commandSender instanceof Player) {
      debugPlayer(((Player) commandSender).getPlayer());
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
      " #14abc9Stylizer Commands: #ff4a4a" + Utilities.setCommand().size() + " / 6 #14abc9registered",
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
      " Stylizer Commands: " + Utilities.setCommand().size() + " / 6 registered",
      " Open Inventories: " + MenuCreator.getOpenInventories().size(),
      " Unique Inventories: " + MenuCreator.getInventoriesByUUID().size(),
      " Currently Installed Plugins...",
      " " + plugins,
      "==========================================="
    );
  }

  @Override
  public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
    return Collections.emptyList();
  }
}
