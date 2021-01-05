package me.omegaweapondev.omeganames.commands;

import me.omegaweapondev.omeganames.OmegaNames;
import me.omegaweapondev.omeganames.utilities.MessageHandler;
import me.ou.library.Utilities;
import me.ou.library.commands.GlobalCommand;
import me.ou.library.menus.MenuCreator;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class DebugCommand extends GlobalCommand {
  final MessageHandler messageHandler = new MessageHandler(OmegaNames.getInstance().getMessagesFile().getConfig());

  @Override
  protected void execute(CommandSender commandSender, String[] strings) {
    if(commandSender instanceof Player) {
      debugPlayer(((Player) commandSender).getPlayer());
      return;
    }

    debugConsole();
  }

  private void debugPlayer(final Player player) {

    if(!Utilities.checkPermissions(player, true, "omeganames.debug", "omeganames.admin")) {
      Utilities.message(player, messageHandler.string("No_Permission", "&cSorry, you do not have permission to use that command."));
      return;
    }

    StringBuilder plugins = new StringBuilder();

    for(Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
      plugins.append("&c").append(plugin.getName()).append(" ").append(plugin.getDescription().getVersion()).append("&b, ");
    }

    Utilities.message(player,
      "&b===========================================",
      " &bOmegaNames &cv" + OmegaNames.getInstance().getDescription().getVersion() + " &bBy OmegaWeaponDev",
      "&b===========================================",
      " &bServer Brand: &c" + Bukkit.getName(),
      " &bServer Version: &c" + Bukkit.getServer().getVersion(),
      " &bOnline Mode: &c" + Bukkit.getOnlineMode(),
      " &bPlayers Online: &c" + Bukkit.getOnlinePlayers().size() + " / " + Bukkit.getMaxPlayers(),
      " &bOmegaNames Commands: &c" + Utilities.setCommand().size() + " / 3 &bregistered",
      " &bOpen Inventories: &c" + MenuCreator.getOpenInventories().size(),
      " &bUnique Inventories: &c" + MenuCreator.getInventoriesByUUID().size(),
      " &bCurrently Installed Plugins...",
      " " + plugins.toString(),
      "&b==========================================="
    );
  }

  private void debugConsole() {
    StringBuilder plugins = new StringBuilder();

    for(Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
      plugins.append(plugin.getName()).append(" ").append(plugin.getDescription().getVersion()).append(", ");
    }

    Utilities.logInfo(true,
      "===========================================",
      " OmegaNames v" + OmegaNames.getInstance().getDescription().getVersion() + " By OmegaWeaponDev",
      "===========================================",
      " Server Brand: " + Bukkit.getName(),
      " Server Version: " + Bukkit.getServer().getVersion(),
      " Online Mode: " + Bukkit.getServer().getOnlineMode(),
      " Players Online: " + Bukkit.getOnlinePlayers().size() + " / " + Bukkit.getMaxPlayers(),
      " OmegaNames Commands: " + Utilities.setCommand().size() + " / 3 registered",
      " Open Inventories: " + MenuCreator.getOpenInventories().size(),
      " Unique Inventories: " + MenuCreator.getInventoriesByUUID().size(),
      " Currently Installed Plugins...",
      " " + plugins.toString(),
      "==========================================="
    );
  }
}
