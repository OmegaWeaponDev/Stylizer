package me.omegaweapondev.stylizer.commands;

import me.omegaweapondev.stylizer.Stylizer;
import me.omegaweapondev.stylizer.utilities.MessageHandler;
import me.ou.library.Utilities;
import me.ou.library.builders.TabCompleteBuilder;
import me.ou.library.commands.GlobalCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatColour extends GlobalCommand implements TabCompleter {
  private final Stylizer plugin;
  private final MessageHandler messageHandler;
  private final FileConfiguration configFile;
  private final FileConfiguration playerData;
  
  public ChatColour(final Stylizer plugin) {
    this.plugin = plugin;
    messageHandler = new MessageHandler(plugin, plugin.getSettingsHandler().getMessagesFile().getConfig());
    configFile = plugin.getSettingsHandler().getConfigFile().getConfig();
    playerData = plugin.getSettingsHandler().getPlayerData().getConfig();
  }

  @Override
  protected void execute(final CommandSender commandSender, final String[] strings) {

    if(commandSender instanceof Player) {
      final Player player = ((Player) commandSender).getPlayer();

      if(strings.length == 0) {
        openChatColourGUI(player);
        return;
      }

      if(strings.length == 2) {
        removeChatColour(player, strings);
        return;
      }

      if(strings.length == 3) {
        addChatColour(player, strings);
        return;
      }
    }

    if(strings.length == 0) {
      Utilities.logInfo(true, "You must be a player to open the ChatColour GUI");
      return;
    }

    if(strings.length == 2 && strings[0].equalsIgnoreCase("remove")) {
      removeChatColour(null, strings);
      return;
    }

    if(strings.length == 3) {
      addChatColour(null, strings);
    }
  }

  private void openChatColourGUI(final Player player) {
    if(!Utilities.checkPermissions(player, true, "stylizer.chatcolour.open", "stylizer.admin")) {
      Utilities.message(player, messageHandler.string("No_Permission", "&cSorry, you do not have permission to do that."));
      return;
    }

    plugin.getChatColourGUI().openInventory(player);
  }

  private void addChatColour(final Player player, final String[] strings) {
    final Player target = Bukkit.getPlayer(strings[1]);
    final String chatColour = strings[2];

    if(!strings[0].equalsIgnoreCase("add")) {
      return;
    }

    if(player != null) {
      if(!Utilities.checkPermissions(player, true, "stylizer.chatcolour.add", "stylizer.admin")) {
        Utilities.message(player, messageHandler.string("No_Permission", "&cSorry, you do not have permission to do that."));
        return;
      }
    }

    if(target == null) {
      if(player != null) {
        Utilities.message(player, messageHandler.string("Invalid_Player", "&cSorry, that player cannot be found."));
        return;
      }
      Utilities.logInfo(true, "Sorry, that player cannot be found.");
      return;
    }

    if(chatColour.equalsIgnoreCase("")) {
      removeChatColour(player, strings);
      return;
    }

    playerData.set(target.getUniqueId().toString() + ".Chat_Colour", chatColour);
    plugin.getSettingsHandler().getPlayerData().saveConfig();
    Utilities.message(target, messageHandler.string("Chat_Colour_Applied", "&bYour chat colour has been changed to: %chatcolour%").replace("%chatcolour%", chatColour + player.getName()));
  }

  private void removeChatColour(final Player player, final String[] strings) {
    final Player target = Bukkit.getPlayer(strings[1]);

    if(!strings[0].equalsIgnoreCase("remove")) {
      return;
    }

    if(player != null) {
      if(!Utilities.checkPermissions(player, true, "stylizer.chatcolour.remove", "stylizer.admin")) {
        Utilities.message(player, messageHandler.string("No_Permission", "&cSorry, you do not have permission to do that."));
        return;
      }
    }

    if(target == null) {
      if(player != null) {
        Utilities.message(player, messageHandler.string("Invalid_Player", "&cSorry, that player cannot be found."));
        return;
      }
      Utilities.logInfo(true, "Sorry, that player cannot be found.");
      return;
    }

    for(String groupName : configFile.getConfigurationSection("Group_Chat_Colour.Groups").getKeys(false)) {

      if(Utilities.checkPermission(target, false, "stylizer.chatcolour.groups." + groupName)) {
        playerData.set(target.getUniqueId().toString() + ".Chat_Colour", plugin.getSettingsHandler().getConfigFile().getConfig().getString("Group_Chat_Colour.Groups." + groupName));
        plugin.getSettingsHandler().getPlayerData().saveConfig();
        Utilities.message(target, messageHandler.string("Chat_Colour_Removed", "&cYour chat colour has been reverted to the default colour"));
        return;
      }

      playerData.set(target.getUniqueId().toString() + ".Chat_Colour", configFile.getString("Default_Chat_Colour", "&e"));
      plugin.getSettingsHandler().getPlayerData().saveConfig();
      Utilities.message(target, messageHandler.string("Chat_Colour_Removed", "&cYour chat colour has been reverted to the default colour"));
    }
  }

  @Override
  public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
    if(strings.length <= 1) {
      return new TabCompleteBuilder(commandSender)
        .checkCommand("add", true, "stylizer.chatcolour.add", "stylizer.admin")
        .checkCommand("remove", true, "stylizer.chatcolour.remove", "stylizer.admin")
        .build(strings[0]);
    }

    if(strings.length <= 2 && strings[0].equalsIgnoreCase("add")) {
      List<String> players = new ArrayList<>();
      for(Player player : Bukkit.getOnlinePlayers()) {
        players.add(player.getName());
      }

      return new TabCompleteBuilder(commandSender).addCommand(players).build(strings[1]);
    }

    if(strings.length == 2 && strings[0].equalsIgnoreCase("remove")) {
      List<String> players = new ArrayList<>();
      for(Player player : Bukkit.getOnlinePlayers()) {
        players.add(player.getName());
      }

      return new TabCompleteBuilder(commandSender).addCommand(players).build(strings[1]);
    }

    if(strings.length == 3 && strings[0].equalsIgnoreCase("add")) {
      List<String> colours = new ArrayList<>();
      for(ChatColor color : ChatColor.values()) {
        colours.add(color.toString().replaceAll("§", "&"));
      }

      return new TabCompleteBuilder(commandSender).addCommand(colours).build(strings[2]);
    }

    return Collections.emptyList();
  }
}
