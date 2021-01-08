package me.omegaweapondev.stylizer;

import me.omegaweapondev.stylizer.commands.DebugCommand;
import me.omegaweapondev.stylizer.commands.MainCommand;
import me.omegaweapondev.stylizer.commands.NameColour;
import me.omegaweapondev.stylizer.events.MenuListener;
import me.omegaweapondev.stylizer.events.PlayerListener;
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

public class OmegaNames extends JavaPlugin {
  private static OmegaNames instance;
  private final ConfigCreator configFile = new ConfigCreator("config.yml");
  private final ConfigCreator messagesFile = new ConfigCreator("messages.yml");
  private final ConfigCreator playerData = new ConfigCreator("playerData.yml");

  private Chat chat;

  // Declaring the GUI's
  private NameColours nameColourGUI;

  @Override
  public void onEnable() {
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
    instance = null;

    if(!MenuCreator.getOpenInventories().isEmpty()) {
      nameColourGUI.deleteInventory();
    }

    super.onDisable();
  }

  public void onReload() {

    // Reload the files
    configFile.reloadConfig();
    messagesFile.reloadConfig();
    playerData.reloadConfig();
  }

  private void initialSetup() {
    // Set the plugin & OULibrary Instances
    instance = this;
    Utilities.setInstance(instance);

    if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
      new Placeholders(instance).register();
    }

    if(Bukkit.getPluginManager().getPlugin("Vault") == null) {
      Utilities.logWarning(true,
        "OmegaNames has detected that you do not have vault installed.",
        "If you are wanting to use the prefix and suffixes in the tablist",
        "it is recommended that you install vault and a chat plugin otherwise these features won't work."
      );
    }

    // Setup bStats
    final int bstatsPluginId = 7490;
    Metrics metrics = new Metrics(getInstance(), bstatsPluginId);

    // Log message for successful enabling of the plugin
    Utilities.logInfo(true,
      " _____ _   _",
      "|  _  | \\ | |  ",
      "| | | |  \\| |  OmegaNames v" + OmegaNames.getInstance().getDescription().getVersion() + " By OmegaWeaponDev",
      "| | | | . ` |  Allow your players to choose their own name colours!",
      "\\ \\_/ / |\\  |  Currently supporting Spigot 1.13 - 1.16.4",
      " \\___/\\_| \\_/",
      ""
    );
  }

  private void configSetup() {
    // Create the files
    getConfigFile().createConfig();
    getMessagesFile().createConfig();
    getPlayerData().createConfig();

    // Give the playerData.yml file a header
    getPlayerData().getConfig().options().header(
      " -------------------------------------------------------------------------------------------\n" +
        " \n" +
        " Welcome to OmegaNames' Player Data file.\n" +
        " \n" +
        " This file contains all the uuids and namecolour colours\n" +
        " for all the players who have the permission stylizer.login\n" +
        " \n" +
        " -------------------------------------------------------------------------------------------"
    );
  }

  private void guiSetup() {
    // Create the GUI's
    nameColourGUI = new NameColours();
  }

  private void commandSetup() {
    Utilities.logInfo(true, "Registering the commands...");

    Utilities.setCommand().put("stylizer", new MainCommand());
    Utilities.setCommand().put("namecolour", new NameColour());
    Utilities.setCommand().put("omeganamesdebug", new DebugCommand());

    Utilities.registerCommands();

    Utilities.logInfo(true, "Commands Registered: " + Utilities.setCommand().size() + " / 2");
  }

  private void eventsSetup() {
    Utilities.registerEvents(new PlayerListener(), new MenuListener());
  }

  private void spigotUpdater() {
    // The Updater
    new SpigotUpdater(this, 78327).getVersion(version -> {
      int spigotVersion = Integer.parseInt(version.replace(".", ""));
      int pluginVersion = Integer.parseInt(this.getDescription().getVersion().replace(".", ""));

      if(pluginVersion >= spigotVersion) {
        Utilities.logInfo(true, "You are already running the latest version");
        return;
      }

      PluginDescriptionFile pdf = this.getDescription();
      Utilities.logWarning(true,
        "A new version of " + pdf.getName() + " is avaliable!",
        "Current Version: " + pdf.getVersion() + " > New Version: " + version,
        "Grab it here: https://github.com/OmegaWeaponDev/OmegaNames"
      );
    });
  }

  private void configUpdater() {
    Utilities.logInfo(true, "Attempting to update the config files....");

    try {
      if(getConfigFile().getConfig().getDouble("Config_Version") != 1.2) {
        getConfigFile().getConfig().set("Config_Version", 1.2);
        getConfigFile().saveConfig();
        ConfigUpdater.update(OmegaNames.getInstance(), "config.yml", getConfigFile().getFile(), Arrays.asList("Group_Name_Colour.Groups", "Items"));
      }

      if(getMessagesFile().getConfig().getDouble("Config_Version") != 1.1) {
        getMessagesFile().getConfig().set("Config_Version", 1.1);
        getMessagesFile().saveConfig();
        ConfigUpdater.update(OmegaNames.getInstance(), "messages.yml", getMessagesFile().getFile(), Arrays.asList("none"));
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

  public NameColours getNameColourGUI() {
    return nameColourGUI;
  }

  public Chat getChat() {
    return chat;
  }

  public static OmegaNames getInstance() {
    return instance;
  }
}
