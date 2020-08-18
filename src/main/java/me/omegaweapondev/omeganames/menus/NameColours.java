package me.omegaweapondev.omeganames.menus;

import me.omegaweapondev.omeganames.OmegaNames;
import me.omegaweapondev.omeganames.utilities.GUIPermissionsChecker;
import me.omegaweapondev.omeganames.utilities.LoreHandler;
import me.omegaweapondev.omeganames.utilities.MessageHandler;
import me.ou.library.Utilities;
import me.ou.library.menus.MenuCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NameColours extends MenuCreator {

  public NameColours() {
    super(4, OmegaNames.getInstance().getMessagesFile().getConfig().getString("Name_Colour_GUI.GUI_Title"), "&6&lNameColours");

    FileConfiguration configFile = OmegaNames.getInstance().getConfigFile().getConfig();

    int slot = -2;

    for(String itemName : configFile.getConfigurationSection("Items").getKeys(false)) {
      if(slot++ > 33) {
        Utilities.logWarning(true, "You can only have 33 colours in the GUI!");
        return;
      }

      createItem(slot + 1, configFile.getString("Items." + itemName + ".Item"), itemName,configFile.getString("Items." + itemName + ".Colour"));
    }

    setItem(34, createItemStack("SPONGE", Utilities.colourise("&cCurrent"), loreMessages(Arrays.asList("&cClick here to view", "&cyour current name colour"))), player -> {
      Utilities.message(player, MessageHandler.prefix() + " " + MessageHandler.currentNameColour(player, player.getDisplayName()));
    });

    setItem(35, createItemStack("BARRIER", Utilities.colourise("&cClose"), loreMessages(Arrays.asList("&cClick here to close", "&cthe name colour gui"))), HumanEntity::closeInventory);
  }

  private void createItem(final Integer slot, final String material, final String name, final String colour) {
    for(Player online : Bukkit.getOnlinePlayers()) {
      if(Utilities.checkPermissions(online, true, "omeganames.namecolour.open", "omeganames.admin")) {
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
