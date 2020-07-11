package me.omegaweapondev.omeganames.utilities;

import me.omegaweapondev.omeganames.OmegaNames;
import me.ou.library.Utilities;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageHandler {

  public static String prefix() {
    if(OmegaNames.getInstance().getMessagesFile().getConfig().getString("Prefix") == null) {
      Utilities.logWarning(true,
        "The plugin prefix in the messages.yml has returned null",
        "so a fallback prefix has been set.",
        "To resolve this, please go into the messages.yml and fix the Prefix option and reload the plugin."
      );
      return "&7&l[&bOmegaNames&7&l]";
    } else {
      return OmegaNames.getInstance().getMessagesFile().getConfig().getString("Prefix");
    }
  }

  public static String nameColourApplied(Player player, String namecolour) {
    if(OmegaNames.getInstance().getMessagesFile().getConfig().getString("Name_Colour_Applied") == null) {
      Utilities.logWarning(true,
        "The message players get when they apply a name colour in the messages.yml has returned null",
        "so a fallback message has been set for when they apply a name colour.",
        "To resolve this, please go into the message.yml and fix the Name_Colour_Applied option and reload the plugin."
      );
      return "&bYour name colour has been changed to: " + namecolour;
    } else {
      return OmegaNames.getInstance().getMessagesFile().getConfig().getString("Name_Colour_Applied").replace("%namecolour%", player.getDisplayName());
    }
  }

  public static String nameColourRemoved() {
    if(OmegaNames.getInstance().getMessagesFile().getConfig().getString("Name_Colour_Removed") == null) {
      Utilities.logWarning(true,
        "The message players get when they remove a name colour in the messages.yml has returned null",
        "so a fallback message has been set for when they remove a name colour.",
        "To resolve this, please go into the messages.yml and fix the Name_Colour_Removed option and reload the plugin"
      );
      return "&cYour name colour has been reverted to the default colour";
    } else {
      return OmegaNames.getInstance().getMessagesFile().getConfig().getString("Name_Colour_Removed");
    }
  }

  public static String currentNameColour(Player player, String namecolour) {
    if(OmegaNames.getInstance().getMessagesFile().getConfig().getString("Current_Name_Colour") == null) {
      Utilities.logWarning(true,
        "The message players get when they check their current name colour in the messages.yml has returned null",
        "so a fallback message has been set for when they check their current name colour.",
        "To resolve this, please go into the messages.yml and fix the Current_Name_Colour option and reload the plugin"
      );
      return "&bYour name colour is currently set to: " + namecolour;
    } else {
      return OmegaNames.getInstance().getMessagesFile().getConfig().getString("Current_Name_Colour").replace("%namecolour%", player.getDisplayName());
    }
  }

  public static List<String> nameColourItemLore(String namecolour) {
    if(OmegaNames.getInstance().getMessagesFile().getConfig().getStringList("Name_Colour_GUI.Colour_Lore").size() == 0) {
      Utilities.logWarning(true,
        "The lore messages for the name colour items in the messages.yml have returned null",
        "so fallback lore messages have been set.",
        "To resolve this, please go into the messages.yml and fix the Name_Colour_GUI.Colour_Lore option and reload the plugin"
      );
      return Utilities.colourise(Arrays.asList("&cClick here to change", "&cyour name colour to", namecolour));
    } else {
      List<String> nameColourItemLoreMessages = new ArrayList<>();

      for(String s : OmegaNames.getInstance().getMessagesFile().getConfig().getStringList("Name_Colour_GUI.Colour_Lore")) {
        nameColourItemLoreMessages.add(s.replace("%namecolour%", namecolour));
      }
      return Utilities.colourise(nameColourItemLoreMessages);
    }
  }

  public static List<String> nameColourNoPermissionLore() {
    if(OmegaNames.getInstance().getMessagesFile().getConfig().getStringList("Name_Colour_GUI.No_Permission_Lore").size() == 0) {
      Utilities.logWarning(true,
        "The no permission lore messages for the name colour items in the messages.yml have returned null",
        "so fallback lore messages have been set.",
        "To resolve this, please go into the messages.yml and fix the Name_Colour_GUI.No_Permission_Lore option and reload the plugin"
      );
      return Arrays.asList("&cYou curerntly don't", "&chave permission to", "&cuse this name colour");
    } else {
      return OmegaNames.getInstance().getMessagesFile().getConfig().getStringList("Name_Colour_GUI.No_Permission_Lore");
    }
  }

  public static String reloadMessage() {
    if(OmegaNames.getInstance().getMessagesFile().getConfig().getString("Reload_Message") == null) {
      Utilities.logWarning(true,
        "The reload message in the messages.yml has returned null",
        "so a fallback reload message has been set.",
        "To resolve this, please go into the messages.yml and fix the Reload_Message option and reload the plugin"
      );
      return "&bThe plugin has successfully been reloaded";
    } else {
      return OmegaNames.getInstance().getMessagesFile().getConfig().getString("Reload_Message");
    }
  }

  public static String noPermission() {
    if(OmegaNames.getInstance().getMessagesFile().getConfig().getString("No_Permission") == null) {
      Utilities.logWarning(true,
        "The no permission message in the messages.yml has returned null",
        "so a fallback no permission message has been set",
        "To resolve this, please go into the messages.yml and fix the No_Permission option and reload the plugin"
      );
      return "&cI'm sorry, but you do not have permission to do that";
    } else {
      return OmegaNames.getInstance().getMessagesFile().getConfig().getString("No_Permission");
    }
  }

  public static String defaultNameColour() {
    if(OmegaNames.getInstance().getConfigFile().getConfig().getString("Default_Name_Colour") == null) {
      Utilities.logWarning(true,
        "The default name colour in the config.yml has returned null",
        "so a fallback name colour has been set for the default namecolour",
        "To resolve this, please go into the config.yml and fix the Default_Name_Colour option and reload the plugin"
      );
      return "&e";
    } else {
      return OmegaNames.getInstance().getConfigFile().getConfig().getString("Default_Name_Colour");
    }
  }
}
