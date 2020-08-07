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
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class OmegaNames extends JavaPlugin {
  private final ConfigCreator configFile = new ConfigCreator("config.yml");
  private final ConfigCreator messagesFile = new ConfigCreator("messages.yml");
  private final ConfigCreator playerData = new ConfigCreator("playerData.yml");
  private static OmegaNames instance;

  // Declaring the GUI's
  private NameColours nameColourGUI;

  @Override
  public void onEnable() {
    // Set the plugin & OULibrary Instances
    instance = this;
    Utilities.setInstance(instance);

    // Send a message to console once the plugin has enabled
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

    if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
      new Placeholders(instance).register();
    }

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

    // Create the GUI's
    nameColourGUI = new NameColours();

    // Register the commands and events
    Utilities.registerCommand("omeganames", new MainCommand());
    Utilities.registerCommand("namecolour", new NameColour());

    Utilities.registerEvents(new PlayerListener(), new MenuListener());

    // Setup bStats
    final int bstatsPluginId = 7490;
    Metrics metrics = new Metrics(getInstance(), bstatsPluginId);

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
