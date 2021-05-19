package me.omegaweapondev.stylizer;

import me.omegaweapondev.stylizer.commands.*;
import me.omegaweapondev.stylizer.events.ChatListener;
import me.omegaweapondev.stylizer.events.MenuListener;
import me.omegaweapondev.stylizer.events.PlayerListener;
import me.omegaweapondev.stylizer.events.ServerPingListener;
import me.omegaweapondev.stylizer.menus.ChatColours;
import me.omegaweapondev.stylizer.menus.NameColours;
import me.omegaweapondev.stylizer.utilities.Placeholders;
import me.ou.library.SpigotUpdater;
import me.ou.library.Utilities;
import me.ou.library.configs.ConfigCreator;
import me.ou.library.configs.ConfigUpdater;
import me.ou.library.menus.MenuCreator;
import net.milkbowl.vault.chat.Chat;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Arrays;

public class Stylizer extends JavaPlugin {
  private Stylizer plugin;
  private Chat chat;
  private NameColours nameColourGUI;
  private ChatColours chatColourGUI;
  private final ConfigCreator configFile = new ConfigCreator("config.yml");
  private final ConfigCreator messagesFile = new ConfigCreator("messages.yml");
  private final ConfigCreator playerData = new ConfigCreator("playerData.yml");
  private final ConfigCreator chatlog = new ConfigCreator("chatlog.yml");

  @Override
  public void onEnable() {
    plugin = this;

    Utilities.logInfo(false,
      " _____ _         _ _",
      "/  ___| |       | (_) " ,
      "\\ `--.| |_ _   _| |_ _______ _ __ ",
      " `--. \\ __| | | | | |_  / _ \\ '__| Stylizer v" + plugin.getDescription().getVersion() + " By OmegaWeaponDev" ,
      "/\\__/ / |_| |_| | | |/ /  __/ | Running on version: " + Bukkit.getVersion(),
      "\\____/ \\__|\\__, |_|_/___\\___|_|",
      "            __/ |",
      "           |___/ "
    );

    initialSetup();
    configSetup();
    configUpdater();
    commandSetup();
    eventsSetup();
    guiSetup();
    spigotUpdater();
    setupChat();
  }

  @Override
  public void onDisable() {
    // Set the instance to null when the plugin is disabled
    if(!MenuCreator.getOpenInventories().isEmpty()) {
      nameColourGUI.deleteInventory();
      chatColourGUI.deleteInventory();
    }

    super.onDisable();
  }

  public void onReload() {

    // Reload the files
    configFile.reloadConfig();
    messagesFile.reloadConfig();
    playerData.reloadConfig();
    chatlog.reloadConfig();
  }

  private void initialSetup() {
    // Set the plugin & OULibrary Instances
    Utilities.setInstance(this);

    if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
      new Placeholders(this).register();
    }

    if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
      Utilities.logWarning(true,
        "Stylizer requires PlaceholderAPI to be installed if you are wanting to use the `%stylizer_namecolour%` placeholder",
        "It is also required if you are wanting to use placeholders in any of the chat formats.",
        "You can install PlaceholderAPI here: https://www.spigotmc.org/resources/placeholderapi.6245/ "
      );
    }

    if(Bukkit.getPluginManager().getPlugin("Vault") == null) {
      Utilities.logWarning(true,
        "Stylizer has detected that you do not have vault installed.",
        "If you are wanting to use the prefix and suffixes in the tablist",
        "it is recommended that you install vault and a chat plugin otherwise these features won't work.",
        "You can install vault here: https://www.spigotmc.org/resources/vault.34315/"
      );
    }

    // Setup bStats
    final int bstatsPluginId = 7490;
    Metrics metrics = new Metrics(plugin, bstatsPluginId);
  }

  private void configSetup() {
    // Create the files
    getConfigFile().createConfig();
    getMessagesFile().createConfig();
    getChatlog().createConfig();
    getPlayerData().createConfig();

    // Give the playerData.yml file a header
    getChatlog().getConfig().options().header(
      " -------------------------------------------------------------------------------------------\n" +
        " \n" +
        " Welcome to Stylizer's Chat Log.\n" +
        " \n" +
        " Below will be a list of all the chat messages that have been said.\n" +
        " The format used to log chat messages is <timestamp>: <players name> >> <message>\n" +
        " \n" +
        " -------------------------------------------------------------------------------------------"
    );
    getChatlog().saveConfig();

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

  private void guiSetup() {
    // Create the GUI's
    nameColourGUI = new NameColours(plugin);
    chatColourGUI = new ChatColours(plugin);
  }

  private void commandSetup() {
    Utilities.logInfo(true, "Registering the commands...");

    Utilities.setCommand().put("stylizer", new MainCommand(plugin));
    Utilities.setCommand().put("namecolour", new NameColour(plugin));
    Utilities.setCommand().put("stylizerdebug", new DebugCommand(plugin));
    Utilities.setCommand().put("itemnamer", new ItemNamer(plugin));
    Utilities.setCommand().put("stylizerclearlog", new ClearLog(plugin));
    Utilities.setCommand().put("chatcolour", new ChatColour(plugin));

    Utilities.registerCommands();

    Utilities.logInfo(true, "Commands Registered: " + Utilities.setCommand().size() + " / 6");
  }

  private void eventsSetup() {
    Utilities.registerEvents(new PlayerListener(plugin), new MenuListener(), new ChatListener(plugin), new ServerPingListener(plugin));
  }

  private void spigotUpdater() {
    // The Updater
    new SpigotUpdater(plugin, 78327).getVersion(version -> {
      int spigotVersion = Integer.parseInt(version.replace(".", ""));
      int pluginVersion = Integer.parseInt(plugin.getDescription().getVersion().replace(".", ""));

      if(pluginVersion >= spigotVersion) {
        Utilities.logInfo(true, "You are already running the latest version");
        return;
      }

      PluginDescriptionFile pdf = plugin.getDescription();
      Utilities.logWarning(true,
        "A new version of " + pdf.getName() + " is avaliable!",
        "Current Version: " + pdf.getVersion() + " > New Version: " + version,
        "Grab it here: https://github.com/OmegaWeaponDev/Stylizer"
      );
    });
  }

  private void configUpdater() {
    Utilities.logInfo(true, "Attempting to update the config files....");

    try {
      if(getConfigFile().getConfig().getDouble("Config_Version") != 2.3) {
        getConfigFile().getConfig().set("Config_Version", 2.3);
        getConfigFile().saveConfig();
        ConfigUpdater.update(plugin, "config.yml", getConfigFile().getFile(), Arrays.asList("Group_Name_Colour.Groups", "Name_Colour_Items", "Chat_Colour_Items", "Chat_Settings.Chat_Formats.Group_Formats.Groups"));
      }

      if(getMessagesFile().getConfig().getDouble("Config_Version") != 2.1) {
        getMessagesFile().getConfig().set("Config_Version", 2.1);
        getMessagesFile().saveConfig();
        ConfigUpdater.update(plugin, "messages.yml", getMessagesFile().getFile(), Arrays.asList("none"));
      }
      onReload();
      Utilities.logInfo(true, "Config Files have successfully been updated!");
    } catch(IOException ex) {
      ex.printStackTrace();
    }
  }

  private boolean setupChat() {
    RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
    chat = rsp.getProvider();
    return chat != null;
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

  public ConfigCreator getChatlog() {
    return chatlog;
  }

  public NameColours getNameColourGUI() {
    return nameColourGUI;
  }

  public ChatColours getChatColourGUI() {
    return chatColourGUI;
  }

  public Chat getChat() {
    return chat;
  }
}
