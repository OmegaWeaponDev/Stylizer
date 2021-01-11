package me.omegaweapondev.stylizer.utilities;

import me.omegaweapondev.stylizer.Stylizer;
import me.ou.library.Utilities;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class MessageHandler {
  private final Stylizer plugin;
  private final FileConfiguration messagesConfig;
  private final String configName;

  public MessageHandler(final Stylizer plugin, final FileConfiguration messagesConfig) {
    this.plugin = plugin;
    this.messagesConfig = messagesConfig;
    this.configName = plugin.getMessagesFile().getFileName();
  }

  public String string(final String message, final String fallbackMessage) {
    if(messagesConfig.getString(message) == null) {
      getErrorMessage(message);
      return getPrefix() + fallbackMessage;
    }
    return getPrefix() + messagesConfig.getString(message);
  }

  public String console(final String message, final String fallbackMessage) {
    if(messagesConfig.getString(message) == null) {
      getErrorMessage(message);
      return fallbackMessage;
    }
    return messagesConfig.getString(message);
  }

  public List<String> stringList(final String message, final List<String> fallbackMessage) {
    if(messagesConfig.getStringList(message).size() == 0) {
      getErrorMessage(message);
      return fallbackMessage;
    }
    return messagesConfig.getStringList(message);
  }

  public String getPrefix() {
    if(messagesConfig.getString("Prefix") == null) {
      getErrorMessage("Prefix");
      return "#6b6b6b&l[#6928f7Stylizer#6b6b6b&l]" + " ";
    }
    return messagesConfig.getString("Prefix") + " ";
  }

  private void getErrorMessage(final String message) {
    Utilities.logInfo(true,
      "There was an error getting the " + message + " message from the " + configName + ".",
      "I have set a fallback message to take it's place until the issue is fixed.",
      "To resolve this, please locate " + message + " in the " + configName + " and fix the issue."
    );
  }
}
