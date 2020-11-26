package me.omegaweapondev.omeganames.menus;

import me.omegaweapondev.omeganames.OmegaNames;
import me.omegaweapondev.omeganames.utilities.GUIPermissionsChecker;
import me.omegaweapondev.omeganames.utilities.ItemCreator;
import me.omegaweapondev.omeganames.utilities.MessageHandler;
import me.ou.library.Utilities;
import me.ou.library.menus.MenuCreator;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class NameColours extends MenuCreator {
  private final MessageHandler messagesFile = new MessageHandler(OmegaNames.getInstance().getMessagesFile().getConfig());

  private ItemCreator itemCreator;

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

    setItem(34, createItemStack("SPONGE", Utilities.colourise("&cCurrent"), Utilities.colourise(Arrays.asList("&cClick here to view", "&cyour current name colour"))), player -> {
      Utilities.message(player, messagesFile.string("Current_Name_Colour", "&bYour name colour has been changed to: %namecolour%").replace("%namecolour%", player.getDisplayName()));
    });

    setItem(35, createItemStack("BARRIER", Utilities.colourise("&cClose"), Utilities.colourise(Arrays.asList("&cClick here to close", "&cthe name colour gui"))), HumanEntity::closeInventory);
  }

  private void createItem(final Integer slot, final String material, final String name, final String colour) {

    setItem(slot, createItemStack(material, Utilities.colourise(colour + name), Utilities.colourise(messagesFile.stringList("Name_Colour_GUI.Colour_Lore" , Arrays.asList("&cClick here to change", "&cyour name colour to", "%namecolour%".replace("%namecolour%", colour + name))))), player -> {
      final GUIPermissionsChecker permChecker = new GUIPermissionsChecker(player, name, colour);

      permChecker.nameColourPermsCheck();
    });
  }

  private ItemStack createItemStack(final String material, final String name, final List<String> lore) {
    if(Material.getMaterial(material.toUpperCase()) == null) {
      itemCreator = new ItemCreator(Material.BARRIER);
      itemCreator.setDisplayName("&cInvalid Material");
      itemCreator.setLore("This item is invalid.", "Please pick another material to use", "that is supported by your server version");

      return itemCreator.getItem();
    }

    itemCreator = new ItemCreator(Material.valueOf(material));

    itemCreator.setDisplayName(name);
    itemCreator.setLore(String.valueOf(lore));

    return itemCreator.getItem();
  }
}
