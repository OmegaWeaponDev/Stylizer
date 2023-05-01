package me.omegaweapondev.stylizer;

import me.omegaweapondev.stylizer.commands.*;
import me.omegaweapondev.stylizer.events.ChatListener;
import me.omegaweapondev.stylizer.events.MenuListener;
import me.omegaweapondev.stylizer.events.PlayerListener;
import me.omegaweapondev.stylizer.events.ServerPingListener;
import me.omegaweapondev.stylizer.menus.ChatColours;
import me.omegaweapondev.stylizer.menus.NameColours;
import me.omegaweapondev.stylizer.utilities.Placeholders;
import me.omegaweapondev.stylizer.utilities.SettingsHandler;
import me.ou.library.Utilities;
import me.ou.library.menus.MenuCreator;
import net.milkbowl.vault.chat.Chat;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class Stylizer extends JavaPlugin {
  private Stylizer plugin;
  private SettingsHandler settingsHandler;
  private NameColours nameColourGUI;
  private ChatColours chatColourGUI;
  private Chat chat = null;
  private final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

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

    if(Bukkit.getPluginManager().getPlugin("Vault") == null) {
      Utilities.logSevere(true,
        "Stylizer has detected that you do not have vault installed.",
        "A majority of Stylizers features rely on Vault for them to work correctly.",
        "So it is required that you install vault otherwise these features won't work.",
        "You can install vault here: https://www.spigotmc.org/resources/vault.34315/"
      );
      Bukkit.getPluginManager().disablePlugin(plugin);
      return;
    }

    setupChat();
    getSettingsHandler().setupConfigs();
    getSettingsHandler().configUpdater();
    commandSetup();
    eventsSetup();
    registerTablistTeams();
  }

  @Override
  public void onDisable() {
    // Set the instance to null when the plugin is disabled
    if(!MenuCreator.getOpenInventories().isEmpty()) {
      nameColourGUI.deleteInventory();
      chatColourGUI.deleteInventory();
    }

    if (!Bukkit.getScoreboardManager().getMainScoreboard().getTeams().isEmpty()) {
      for(Team team : scoreboard.getTeams()) {
        team.unregister();
      }
    }

    super.onDisable();
  }

  public void onReload() {
    // Reload the files
    getSettingsHandler().reloadFiles();
  }

  private void guiSetup() {

    // Create the GUI's
    nameColourGUI = new NameColours(plugin);
    chatColourGUI = new ChatColours(plugin);

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

    // Setup bStats
    final int bstatsPluginId = 17447;
    Metrics metrics = new Metrics(plugin, bstatsPluginId);
  }

  private void commandSetup() {
    Utilities.logInfo(true, "Registering the commands...");

    Utilities.setCommand().put("stylizer", new MainCommand(plugin));
    Utilities.setCommand().put("namecolour", new NameColour(plugin));
    Utilities.setCommand().put("itemnamer", new ItemNamer(plugin));
    Utilities.setCommand().put("chatcolour", new ChatColour(plugin));
    Utilities.registerCommands();

    Utilities.logInfo(true, "Commands Registered: " + Utilities.setCommand().size() + " / 4");
  }

  private void eventsSetup() {
    Utilities.registerEvents(new PlayerListener(plugin), new MenuListener(plugin), new ChatListener(plugin), new ServerPingListener(plugin));
  }

  private boolean setupChat() {
    RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
    chat = rsp.getProvider();
    return chat != null;
  }

  private void registerTablistTeams() {
    if(settingsHandler.getConfigFile().getConfig().getBoolean("Tablist.Sorting_Order.Enabled")) {
      for(String configGroup : settingsHandler.getConfigFile().getConfig().getConfigurationSection("Tablist.Sorting_Order.Order").getKeys(false)) {
        Team team = scoreboard.getTeam("group_" + configGroup);
        if (team == null) {
          scoreboard.registerNewTeam("group_" + configGroup);
        }
      }
    }
  }

  public boolean isPlaceholderAPI() {
    return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
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
