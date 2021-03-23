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

public class NameColour extends GlobalCommand implements TabCompleter {
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
  protected void execute(final CommandSender commandSender, final String[] strings) {

    if(commandSender instanceof Player) {
      final Player player = ((Player) commandSender).getPlayer();

      if(strings.length == 0) {
        openNameColourGUI(player);
        return;
      }

      if(strings.length == 2) {
        removeNameColour(player, strings);
        return;
      }

      if(strings.length == 3) {
        addNameColour(player, strings);
        return;
      }
    }

    if(strings.length == 0) {
      Utilities.logInfo(true, "You must be a player to open the NameColour GUI");
      return;
    }

    if(strings.length == 2 && strings[0].equalsIgnoreCase("remove")) {
      removeNameColour(null, strings);
      return;
    }

    if(strings.length == 3) {
      addNameColour(null, strings);
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

    if(player != null) {
      if(!Utilities.checkPermissions(player, true, "stylizer.namecolour.add", "stylizer.admin")) {
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

    if(player != null) {
      if(!Utilities.checkPermissions(player, true, "stylizer.namecolour.remove", "stylizer.admin")) {
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

    for(String groupName : configFile.getConfigurationSection("Group_Name_Colour.Groups").getKeys(false)) {

      if(Utilities.checkPermission(target, false, "stylizer.namecolour.groups." + groupName)) {
        target.setDisplayName(Utilities.colourise(configFile.getString("Group_Name_Colour.Groups." + groupName) + target.getName()));
        playerData.set(target.getUniqueId().toString() + ".Name_Colour", plugin.getConfigFile().getConfig().getString("Group_Name_Colour.Groups." + groupName));
        plugin.getPlayerData().saveConfig();
        Utilities.message(target, messageHandler.string("Name_Colour_Removed", "&cYour name colour has been reverted to the default colour"));
        return;
      }

      target.setDisplayName(Utilities.colourise(configFile.getString("Default_Name_Colour", "&e") + target.getName()));
      playerData.set(target.getUniqueId().toString() + ".Name_Colour", configFile.getString("Default_Name_Colour", "&e"));
      plugin.getPlayerData().saveConfig();
      Utilities.message(target, messageHandler.string("Name_Colour_Removed", "&cYour name colour has been reverted to the default colour"));
    }
  }

  @Override
  public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
    if(strings.length <= 1) {
      return new TabCompleteBuilder(commandSender)
        .checkCommand("add", true, "stylizer.namecolour.add", "stylizer.admin")
        .checkCommand("remove", true, "stylizer.namecolour.remove", "stylizer.admin")
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
