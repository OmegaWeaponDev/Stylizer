package me.omegaweapondev.omeganames.menus;

import me.omegaweapondev.omeganames.OmegaNames;
import me.omegaweapondev.omeganames.utilities.GUIPermissionsChecker;
import me.omegaweapondev.omeganames.utilities.LoreHandler;
import me.omegaweapondev.omeganames.utilities.MessageHandler;
import me.ou.library.Utilities;
import me.ou.library.menus.MenuCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NameColours extends MenuCreator {

  public NameColours() {
    super(5, OmegaNames.getInstance().getMessagesFile().getConfig().getString("Name_Colour_GUI.GUI_Title"), "&6&lNameColours");

    createItem(10, "RED_WOOL", "Dark Red", OmegaNames.getInstance().getConfigFile().getConfig().getString("Name_Colours.darkred"));
    createItem(11, "RED_WOOL", "Red", OmegaNames.getInstance().getConfigFile().getConfig().getString("Name_Colours.darkred"));
    createItem(12, "ORANGE_WOOL", "Gold", OmegaNames.getInstance().getConfigFile().getConfig().getString("Name_Colours.darkred"));
    createItem(13, "YELLOW_WOOL", "Yellow", OmegaNames.getInstance().getConfigFile().getConfig().getString("Name_Colours.darkred"));
    createItem(14, "GREEN_WOOL", "Green", OmegaNames.getInstance().getConfigFile().getConfig().getString("Name_Colours.darkred"));
    createItem(15, "GREEN_WOOL", "Lime", OmegaNames.getInstance().getConfigFile().getConfig().getString("Name_Colours.darkred"));
    createItem(16, "LIGHT_BLUE_WOOL", "Aqua", OmegaNames.getInstance().getConfigFile().getConfig().getString("Name_Colours.darkred"));

    createItem(19, "LIGHT_BLUE_WOOL", "Dark Aqua", OmegaNames.getInstance().getConfigFile().getConfig().getString("Name_Colours.darkred"));
    createItem(20, "BLUE_WOOL", "Dark Blue", OmegaNames.getInstance().getConfigFile().getConfig().getString("Name_Colours.darkred"));
    createItem(21, "BLUE_WOOL", "Blue", OmegaNames.getInstance().getConfigFile().getConfig().getString("Name_Colours.darkred"));
    createItem(22, "PINK_WOOL", "Pink", OmegaNames.getInstance().getConfigFile().getConfig().getString("Name_Colours.darkred"));
    createItem(23, "MAGENTA_WOOL", "Purple", OmegaNames.getInstance().getConfigFile().getConfig().getString("Name_Colours.darkred"));
    createItem(24, "WHITE_WOOL", "White", OmegaNames.getInstance().getConfigFile().getConfig().getString("Name_Colours.darkred"));
    createItem(25, "LIGHT_GRAY_WOOL","Light Gray", OmegaNames.getInstance().getConfigFile().getConfig().getString("Name_Colours.darkred"));

    createItem(29, "GRAY_WOOL","Gray", OmegaNames.getInstance().getConfigFile().getConfig().getString("Name_Colours.darkred"));
    createItem(30, "BLACK_WOOL", "Black", OmegaNames.getInstance().getConfigFile().getConfig().getString("Name_Colours.darkred"));

    setItem(32, createItemStack("SPONGE", Utilities.colourise("&cCurrent"), loreMessages(Arrays.asList("&cClick here to view", "&cyour current name colour"))), player -> {
      Utilities.message(player, MessageHandler.prefix() + " " + MessageHandler.currentNameColour(player, player.getDisplayName()));
    });

    setItem(33, createItemStack("BARRIER", Utilities.colourise("&cReset"), loreMessages(Arrays.asList("&cClick here to remove", "&cyour name colour completely"))), player -> {
      player.setDisplayName(Utilities.colourise(MessageHandler.defaultNameColour() + player.getName()));
      OmegaNames.getInstance().getPlayerData().getConfig().set(player.getUniqueId().toString() + ".Name_Colour", OmegaNames.getInstance().getConfigFile().getConfig().getString("Name_Colour.Default_Colour"));
      OmegaNames.getInstance().getPlayerData().saveConfig();
      Utilities.message(player, MessageHandler.prefix() + " " + MessageHandler.nameColourRemoved());
    });

    setItem(34, createItemStack("BARRIER", Utilities.colourise("&cClose"), loreMessages(Arrays.asList("&cClick here to close", "&cthe name colour gui"))), HumanEntity::closeInventory);
  }

  private void createItem(final Integer slot, final String material, final String name, final String colour) {
    for(Player online : Bukkit.getOnlinePlayers()) {
      if(Utilities.checkPermission(online, true, "omeganames.namecolours.open")) {
        setItem(slot, createItemStack(material, Utilities.colourise(colour + name), LoreHandler.noPermissionLore(online, name, colour)), player -> {
          GUIPermissionsChecker.nameColourPermsCheck(player, name, colour);
        });
      }
    }
  }

  private ItemStack createItemStack(final String material, final String name, final List<String> itemLore) {
    final ItemStack item =  new ItemStack(Material.valueOf(material), 1);
    final ItemMeta itemMeta = item.getItemMeta();

    itemMeta.setDisplayName(Utilities.colourise(name));
    itemMeta.setLore(itemLore);
    item.setItemMeta(itemMeta);

    return item;
  }

  private List<String> loreMessages(final List<String> lore) {
    List<String> colouredLore = new ArrayList<>();

    for(String string : lore) {
      colouredLore.add(Utilities.colourise(string));
    }

    return colouredLore;
  }
}
