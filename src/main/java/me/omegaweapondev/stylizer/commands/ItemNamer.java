package me.omegaweapondev.stylizer.commands;

import me.omegaweapondev.stylizer.Stylizer;
import me.omegaweapondev.stylizer.utilities.MessageHandler;
import me.ou.library.Utilities;
import me.ou.library.builders.TabCompleteBuilder;
import me.ou.library.commands.PlayerCommand;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemNamer extends PlayerCommand implements TabCompleter {
  private final Stylizer plugin;
  private final MessageHandler messageHandler;
  private final FileConfiguration configFile;

  public ItemNamer(final Stylizer plugin) {
    this.plugin = plugin;
    messageHandler = new MessageHandler(plugin, plugin.getSettingsHandler().getMessagesFile().getConfig());
    configFile = plugin.getSettingsHandler().getConfigFile().getConfig();
  }

  @Override
  protected void execute(Player player, String[] strings) {

    if(!Utilities.checkPermissions(player, true, "stylizer.itemnamer.use", "stylizer.admin")) {
      Utilities.message(player, messageHandler.string("No_Permission", "#570000I'm sorry, but you do not have permission to do that"));
      return;
    }

    if(strings.length < 2) {
      invalidArgs(player);
      return;
    }

    String[] values = Arrays.copyOfRange(strings, 2, strings.length);
    StringBuilder stringBuilder = new StringBuilder();

    for(String string : values) {
      stringBuilder.append(string).append(" ");
    }

    final String action = strings[0];
    final String type = strings[1];
    final String value = stringBuilder.toString();

    updateItemName(player, action, type, value);
  }

  private void invalidArgs(final Player player) {
    Utilities.message(player,
      "#14abc9===========================================",
      "#ff4a4aItemNamer Help",
      "#14abc9===========================================",
      "#14abc9Set Item Name: #ff4a4a/itemnamer set name <Item Name>",
      "#14abc9Set Item Lore: #ff4a4a/itemnamer set lore <Item Lore>",
      "#14abc9Remove Item Name: #ff4a4a/itemnamer remove name",
      "#14abc9Remove Item Lore: #ff4a4a/itemnamer remove lore",
      "#14abc9==========================================="
    );
  }

  private void updateItemName(final Player player, final String action, final String type, final String value) {
    if(player.getInventory().getItemInMainHand().getType() == Material.AIR) {
      Utilities.message(player, messageHandler.getPrefix() + "#ff4a4aPlease equip the item you are wanting to update.");
      return;
    }

    if(type.equals("lore")) {
      updateItemLore(player, action, type, value);
      return;
    }

    if(!type.equals("name")) {
      invalidArgs(player);
      return;
    }

    ItemStack playerItem = player.getInventory().getItemInMainHand();
    ItemMeta playerItemMeta = playerItem.getItemMeta();

    if(action.equals("set")) {
      if(!Utilities.checkPermissions(player, true, "stylizer.itemnamer.set.name", "stylizer.itemnamer.admin", "stylizer.admin")) {
        Utilities.message(player, messageHandler.string("No_Permission", "#570000Sorry, you do not have permission to do that."));
        return;
      }

      playerItemMeta.setDisplayName(Utilities.colourise(value));
      playerItem.setItemMeta(playerItemMeta);
      Utilities.message(player, messageHandler.string("ItemNamer.Set_Item_Name", "#14abc9You have set the items name to %itemName%").replace("%itemName%", playerItemMeta.getDisplayName()));
      return;
    }

    if(action.equals("remove")) {
      if(!Utilities.checkPermissions(player, true, "stylizer.itemnamer.remove.name", "stylizer.itemnamer.admin", "stylizer.admin")) {
        Utilities.message(player, messageHandler.string("No_Permission", "#570000Sorry, you do not have permission to do that."));
        return;
      }

      playerItemMeta.setDisplayName(Utilities.colourise("&r" + WordUtils.capitalizeFully(playerItem.getType().toString().replace("_", " "))));
      playerItem.setItemMeta(playerItemMeta);
      Utilities.message(player, messageHandler.string("ItemNamer.Remove_Item_Name", "#14abc9You have removed the items name"));
    }
  }

  private void updateItemLore(final Player player, final String action, final String type, final String value) {
    if(player.getInventory().getItemInMainHand().getType() == Material.AIR) {
      Utilities.message(player, messageHandler.getPrefix() + "#ff4a4aPlease equip the item you are wanting to update.");
      return;
    }

    if(type.equals("name")) {
      updateItemName(player, action, type, value);
      return;
    }

    if(!type.equals("lore")) {
      invalidArgs(player);
      return;
    }

    ItemStack playerItem = player.getInventory().getItemInMainHand();
    ItemMeta playerItemMeta = playerItem.getItemMeta();

    if(action.equals("set")) {
      if(!Utilities.checkPermissions(player, true, "stylizer.itemnamer.set.lore", "stylizer.itemnamer.admin", "stylizer.admin")) {
        Utilities.message(player, messageHandler.string("No_Permission", "#570000Sorry, you do not have permission to do that."));
        return;
      }

      List<String> lore = new ArrayList<>();
      final String regex = "((?:[^\\s]*\\s){0,4}[^\\s]*)\\s";
      final Pattern pattern = Pattern.compile(regex);
      final Matcher matcher = pattern.matcher(value);

      while(matcher.find()) {
        for (int i = 1; i <= matcher.groupCount(); i++) {
          lore.add(matcher.group(i));
        }
      }

      playerItemMeta.setLore(Utilities.colourise(lore));
      playerItem.setItemMeta(playerItemMeta);
      Utilities.message(player, messageHandler.string("ItemNamer.Set_Item_Lore", "#14abc9You have set the items lore"));
      return;
    }

    if(action.equals("remove")) {
      if(!Utilities.checkPermissions(player, true, "stylizer.itemnamer.remove.lore", "stylizer.itemnamer.admin", "stylizer.admin")) {
        Utilities.message(player, messageHandler.string("No_Permission", "#570000Sorry, you do not have permission to do that."));
        return;
      }

      playerItemMeta.setLore(new ArrayList<>());
      playerItem.setItemMeta(playerItemMeta);
      Utilities.message(player, messageHandler.string("ItemNamer.Remove_Item_Lore", "#14abc9You have removed the items"));
    }
  }

  @Override
  public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {

    if(strings.length <= 1) {
      return new TabCompleteBuilder(commandSender)
        .checkCommand("set", true, "stylizer.itemnamer.use", "stylizer.itemnamer.set.name", "stylizer.itemnamer.set.lore", "stylizer.itemnamer.admin", "stylizer.itemnamer.admin")
        .checkCommand("remove", true, "stylizer.itemnamer.use", "stylizer.itemnamer.set.name", "stylizer.itemnamer.set.lore", "stylizer.itemnamer.admin", "stylizer.itemnamer.admin")
        .build(strings[0]);
    }

    if(strings.length == 2 && strings[0].equalsIgnoreCase("set")) {
      return new TabCompleteBuilder(commandSender)
        .checkCommand("name", true, "stylizer.itemnamer.set.name", "stylizer.itemnamer.admin", "stylizer.admin")
        .checkCommand("lore", true, "stylizer.itemnamer.set.lore", "stylizer.itemnamer.admin", "stylizer.admin")
        .build(strings[1]);
    }

    if(strings.length == 2 && strings[0].equalsIgnoreCase("remove")) {
        return new TabCompleteBuilder(commandSender)
        .checkCommand("name", true, "stylizer.itemnamer.remove.name", "stylizer.itemnamer.admin", "stylizer.admin")
        .checkCommand("lore", true, "stylizer.itemnamer.remove.lore", "stylizer.itemnamer.admin", "stylizer.admin")
        .build(strings[1]);
    }

    return Collections.emptyList();
  }
}
