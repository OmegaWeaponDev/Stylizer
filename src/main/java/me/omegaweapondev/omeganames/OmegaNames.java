package me.omegaweapondev.omeganames;

import me.omegaweapondev.omeganames.commands.MainCommand;
import me.omegaweapondev.omeganames.commands.NameColour;
import me.omegaweapondev.omeganames.events.MenuListener;
import me.omegaweapondev.omeganames.events.PlayerListener;
import me.omegaweapondev.omeganames.menus.NameColours;
import me.omegaweapondev.omeganames.utilities.Placeholders;
import me.ou.library.SpigotUpdater;
import me.ou.library.Utilities;
import me.ou.library.configs.ConfigCreator;
import me.ou.library.configs.ConfigUpdater;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.Arrays;

public class OmegaNames extends JavaPlugin {
  private final ConfigCreator configFile = new ConfigCreator("config.yml");
  private final ConfigCreator messagesFile = new ConfigCreator("messages.yml");
  private final ConfigCreator playerData = new ConfigCreator("playerData.yml");
  private static OmegaNames instance;

  // Declaring the GUI's
  private NameColours nameColourGUI;

  @Override
  public void onEnable() {
    initialSetup();
    configSetup();
    configUpdater();
    guiSetup();
    commandSetup();
    eventsSetup();
    spigotUpdater();
  }

  @Override
  public void onDisable() {
    // Set the instance to null when the plugin is disabled
    instance = null;
    super.onDisable();
  }

  public void onReload() {
    // Reload the Name Colour GUI
    new BukkitRunnable() {
      @Override
      public void run() {
        nameColourGUI = new NameColours();
      }
    }.runTaskLaterAsynchronously(getInstance(), 20);

    // Reload the files
    configFile.reloadConfig();
    messagesFile.reloadConfig();
    playerData.reloadConfig();
  }

  // Method to reload just the GUI's
  public void onGUIReload() {
    new BukkitRunnable() {
      @Override
      public void run() {
        nameColourGUI = new NameColours();
      }
    }.runTaskLaterAsynchronously(getInstance(), 20);
  }

  private void initialSetup() {
    // Set the plugin & OULibrary Instances
    instance = this;
    Utilities.setInstance(instance);

    if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
      new Placeholders(instance).register();
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
      "\\ \\_/ / |\\  |  Currently supporting Spigot 1.13 - 1.16.1",
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
        " for all the players who have the permission omeganames.login\n" +
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

    Utilities.setCommand().put("omeganames", new MainCommand());
    Utilities.setCommand().put("namecolour", new NameColour());

    Utilities.registerCommands();

    Utilities.logInfo(true, "Commands Registered: " + Utilities.setCommand().size() + " / 2");
  }

  private void eventsSetup() {
    Utilities.registerEvents(new PlayerListener(), new MenuListener());
  }

  private void spigotUpdater() {
    // The Updater
    new SpigotUpdater(this, 78327).getVersion(version -> {
      Utilities.logInfo(true, "Checking for updates...");
      if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
        Utilities.logInfo(true, "You are already running the latest version");
        return;
      }

      PluginDescriptionFile pdf = this.getDescription();
      Utilities.logWarning(true,
        "A new version of " + pdf.getName() + " is avaliable!",
        "Current Version: " + pdf.getVersion() + " > New Version: " + version,
        "Grab it here: https://spigotmc.org/resources/78327"
      );
    });
  }

  private void configUpdater() {
    Utilities.logInfo(true, "Attempting to update the config files....");

    try {
      if(getConfigFile().getConfig().getDouble("Config_Version") != 1.0) {
        getConfigFile().getConfig().set("Config_Version", 1.0);
        getConfigFile().saveConfig();
        ConfigUpdater.update(OmegaNames.getInstance(), "config.yml", getConfigFile().getFile(), Arrays.asList("Group_Name_Colour.Groups", "Items"));
      }

      if(getMessagesFile().getConfig().getDouble("Config_Version") != 1.0) {
        getMessagesFile().getConfig().set("Config_Version", 1.0);
        getMessagesFile().saveConfig();
        ConfigUpdater.update(OmegaNames.getInstance(), "messages.yml", getMessagesFile().getFile(), Arrays.asList("none"));
      }
      onReload();
      Utilities.logInfo(true, "Config Files have successfully been updated!");
    } catch(IOException ex) {
      ex.printStackTrace();
    }
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

  public static OmegaNames getInstance() {
    return instance;
  }
}
