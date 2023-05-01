package me.omegaweapondev.stylizer.utilities;

import me.omegaweapondev.stylizer.Stylizer;
import me.ou.library.Utilities;
import me.ou.library.configs.ConfigCreator;
import me.ou.library.configs.ConfigUpdater;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    List<String> chatlogHeaderMessage = new ArrayList<>();
    chatlogHeaderMessage.add(" -------------------------------------------------------------------------------------------\n");
    chatlogHeaderMessage.add(" \n");
    chatlogHeaderMessage.add(" Welcome to Stylizer's Chat Log.\n");
    chatlogHeaderMessage.add(" \n");
    chatlogHeaderMessage.add(" Below will be a list of all the chat messages that have been said.\n");
    chatlogHeaderMessage.add(" The format used to log chat messages is <timestamp>: <players name> >> <message>\n");
    chatlogHeaderMessage.add(" \n");
    chatlogHeaderMessage.add(" -------------------------------------------------------------------------------------------");

    getChatLog().getConfig().options().setHeader(chatlogHeaderMessage);
    getChatLog().saveConfig();

    List<String> playerdataHeaderMessage = new ArrayList<>();
    playerdataHeaderMessage.add(" -------------------------------------------------------------------------------------------\n");
    playerdataHeaderMessage.add(" \n");
    playerdataHeaderMessage.add(" Welcome to Stylizer Player Data file.\n");
    playerdataHeaderMessage.add(" \n");
    playerdataHeaderMessage.add(" This file contains all the uuids and namecolour colours\n");
    playerdataHeaderMessage.add(" for all the players who have the permission stylizer.login\n");
    playerdataHeaderMessage.add(" \n");
    playerdataHeaderMessage.add(" -------------------------------------------------------------------------------------------");

    getPlayerData().getConfig().options().setHeader(playerdataHeaderMessage);
    getPlayerData().saveConfig();
  }

  public void configUpdater() {
    Utilities.logInfo(true, "Attempting to update the config files....");

    try {
      if(getConfigFile().getConfig().getDouble("Config_Version") != 2.11) {
        getConfigFile().getConfig().set("Config_Version", 2.11);
        getConfigFile().saveConfig();
        ConfigUpdater.update(plugin, "config.yml", getConfigFile().getFile(), Arrays.asList("Group_Name_Colour.Groups", "Group_Chat_Colour.Groups" ,"Name_Colour_Items", "Chat_Colour_Items", "Chat_Settings.Chat_Formats.Group_Formats.Groups"));
        Utilities.logInfo(true, "Stylizer has automatically updated your config.yml!");
      }

      if(getMessagesFile().getConfig().getDouble("Config_Version") != 2.1) {
        getMessagesFile().getConfig().set("Config_Version.", 2.1);
        getMessagesFile().saveConfig();
        ConfigUpdater.update(plugin, "messages.yml", getMessagesFile().getFile(), Arrays.asList("none"));
        Utilities.logInfo(true, "Stylizer has automatically updated your messages.yml!");
      }
      reloadFiles();
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
