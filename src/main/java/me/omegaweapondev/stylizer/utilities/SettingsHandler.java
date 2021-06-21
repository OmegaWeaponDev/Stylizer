package me.omegaweapondev.stylizer.utilities;

import me.omegaweapondev.stylizer.Stylizer;
import me.ou.library.Utilities;
import me.ou.library.configs.ConfigCreator;
import me.ou.library.configs.ConfigUpdater;

import java.io.IOException;
import java.util.Arrays;

public class SettingsHandler {
  private final Stylizer plugin;

  private final ConfigCreator configFile = new ConfigCreator("config.yml");
  private final ConfigCreator messagesFile = new ConfigCreator("messages.yml");
  private final ConfigCreator playerData = new ConfigCreator("playerData.yml");
  private final ConfigCreator chatLog = new ConfigCreator("chatlog.yml");

  public SettingsHandler(final Stylizer plugin) {
    this.plugin = plugin;
  }

  public void setupConfigs() {
    getConfigFile().createConfig();
    getMessagesFile().createConfig();
    getPlayerData().createConfig();
    getChatLog().createConfig();

    getChatLog().getConfig().options().header(
      " -------------------------------------------------------------------------------------------\n" +
        " \n" +
        " Welcome to Stylizer's Chat Log.\n" +
        " \n" +
        " Below will be a list of all the chat messages that have been said.\n" +
        " The format used to log chat messages is <timestamp>: <players name> >> <message>\n" +
        " \n" +
        " -------------------------------------------------------------------------------------------"
    );
    getChatLog().saveConfig();

    getPlayerData().getConfig().options().header(
      " -------------------------------------------------------------------------------------------\n" +
        " \n" +
        " Welcome to Stylizer Player Data file.\n" +
        " \n" +
        " This file contains all the uuids and namecolour colours\n" +
        " for all the players who have the permission stylizer.login\n" +
        " \n" +
        " -------------------------------------------------------------------------------------------"
    );
    getPlayerData().saveConfig();
  }

  public void configUpdater() {
    Utilities.logInfo(true, "Attempting to update the config files....");

    try {
      if(getConfigFile().getConfig().getDouble("Config_Version") != 2.5) {
        getConfigFile().getConfig().set("Config_Version", 2.5);
        getConfigFile().saveConfig();
        ConfigUpdater.update(plugin, "config.yml", getConfigFile().getFile(), Arrays.asList("Group_Name_Colour.Groups", "Name_Colour_Items", "Chat_Colour_Items", "Chat_Settings.Chat_Formats.Group_Formats.Groups"));
      }

      if(getMessagesFile().getConfig().getDouble("Config_Version") != 2.1) {
        getMessagesFile().getConfig().set("Config_Version", 2.1);
        getMessagesFile().saveConfig();
        ConfigUpdater.update(plugin, "messages.yml", getMessagesFile().getFile(), Arrays.asList("none"));
      }
      plugin.onReload();
      Utilities.logInfo(true, "Config Files have successfully been updated!");
    } catch(IOException ex) {
      ex.printStackTrace();
    }
  }

  public void reloadFiles() {
    getConfigFile().reloadConfig();
    getMessagesFile().reloadConfig();
    getChatLog().reloadConfig();
    getPlayerData().reloadConfig();
  }

  public ConfigCreator getConfigFile() {
    return configFile;
  }

  public ConfigCreator getMessagesFile() {
    return messagesFile;
  }

  public ConfigCreator getPlayerData() {
    return playerData;
  }

  public ConfigCreator getChatLog() {
    return chatLog;
  }
}
