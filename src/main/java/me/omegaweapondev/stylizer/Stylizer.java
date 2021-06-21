package me.omegaweapondev.stylizer;

import me.omegaweapondev.stylizer.commands.*;
import me.omegaweapondev.stylizer.events.ChatListener;
import me.omegaweapondev.stylizer.events.MenuListener;
import me.omegaweapondev.stylizer.events.PlayerListener;
import me.omegaweapondev.stylizer.events.ServerPingListener;
import me.omegaweapondev.stylizer.menus.ChatColours;
import me.omegaweapondev.stylizer.menus.NameColours;
import me.omegaweapondev.stylizer.utilities.MessageAnnouncements;
import me.omegaweapondev.stylizer.utilities.Placeholders;
import me.omegaweapondev.stylizer.utilities.SettingsHandler;
import me.ou.library.SpigotUpdater;
import me.ou.library.Utilities;
import me.ou.library.menus.MenuCreator;
import net.milkbowl.vault.chat.Chat;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Stylizer extends JavaPlugin {
  private Stylizer plugin;
  private Chat chat;
  private NameColours nameColourGUI;
  private ChatColours chatColourGUI;
  private SettingsHandler settingsHandler;

  @Override
  public void onEnable() {
    plugin = this;
    settingsHandler = new SettingsHandler(plugin);

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
    getSettingsHandler().setupConfigs();
    getSettingsHandler().configUpdater();
    commandSetup();
    eventsSetup();
    guiSetup();
    spigotUpdater();
    setupChat();

    messageAnnouncements();
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
    getSettingsHandler().reloadFiles();
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
        "Grab it here: https://www.spigotmc.org/resources/stylizer.78327/"
      );
    });
  }

  private void messageAnnouncements() {
    Bukkit.getScheduler().runTaskTimer(plugin, () -> {
        if(!Bukkit.getOnlinePlayers().isEmpty()) {
          for(Player player : Bukkit.getOnlinePlayers()) {
            MessageAnnouncements messageAnnouncements = new MessageAnnouncements(plugin, player);
            messageAnnouncements.broadcastAnnouncements();
          }
        }},
      20L * getSettingsHandler().getConfigFile().getConfig().getInt("Announcement_Messages.Interval"),
      20L * getSettingsHandler().getConfigFile().getConfig().getInt("Announcement_Messages.Interval")
    );
  }

  private boolean setupChat() {
    RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
    chat = rsp.getProvider();
    return chat != null;
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

  public SettingsHandler getSettingsHandler() {
    return settingsHandler;
  }
}
