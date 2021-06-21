package me.omegaweapondev.stylizer.events;

import me.omegaweapondev.stylizer.Stylizer;
import me.omegaweapondev.stylizer.utilities.TablistManager;
import me.ou.library.SpigotUpdater;
import me.ou.library.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.List;

public class PlayerListener implements Listener {
  private final Stylizer plugin;
  private final FileConfiguration configFile;

  private TablistManager tablistManager;

  public PlayerListener(final Stylizer plugin) {
    this.plugin = plugin;
    configFile = plugin.getSettingsHandler().getConfigFile().getConfig();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
    Player player = playerJoinEvent.getPlayer();
    tablistManager = new TablistManager(plugin, player);

    Bukkit.getScheduler().runTaskTimer(plugin, () -> tablistManager.tablistHeaderFooter(), 20L * 5L, 20 * 20L);
    setNameColour(player);

    if(configFile.getBoolean("Tablist.Sorting_Order.Enabled")) {
      Bukkit.getScheduler().runTaskTimer(plugin, this::sortTablist, 20L * 5L, 20L * 20L);
    } else {
      Bukkit.getScheduler().runTaskTimer(plugin, this::formatTablistAndNames, 20L * 5, 20 * 20L);
    }


    // Send the player a message on join if there is an update for the plugin
    if(Utilities.checkPermissions(player, true, "stylizer.update", "stylizer.admin")) {
      new SpigotUpdater(plugin, 78327).getVersion(version -> {
        int spigotVersion = Integer.parseInt(version.replace(".", ""));
        int pluginVersion = Integer.parseInt(plugin.getDescription().getVersion().replace(".", ""));

        if(pluginVersion >= spigotVersion) {
          Utilities.message(player, "#00D4FFYou are already running the latest version");
          return;
        }

        PluginDescriptionFile pdf = plugin.getDescription();
        Utilities.message(player,
          "#00D4FFA new version of #FF4A4A" + pdf.getName() + " #00D4FFis avaliable!",
          "#00D4FFCurrent Version: #FF4A4A" + pdf.getVersion() + " #00D4FF> New Version: #FF4A4A" + version,
          "#00D4FFGrab it here:#FF4A4A https://www.spigotmc.org/resources/stylizer.78327/"
        );
      });
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
    if(Bukkit.getOnlinePlayers().size() < 2) {
      Bukkit.getScheduler().cancelTasks(plugin);
    }
  }

  private void setNameColour(final Player player) {
    tablistManager = new TablistManager(plugin, player);

    if(configFile.getBoolean("Name_Colour_Login")) {

      if(plugin.getSettingsHandler().getPlayerData().getConfig().isConfigurationSection(player.getUniqueId().toString())) {
        player.setDisplayName(Utilities.colourise(plugin.getSettingsHandler().getPlayerData().getConfig().getString(player.getUniqueId() + ".Name_Colour") + player.getName()) + ChatColor.RESET);
        return;
      }

      for(String groupName : configFile.getConfigurationSection("Group_Name_Colour.Groups").getKeys(false)) {

        if(Utilities.checkPermission(player, false, "stylizer.namecolour.groups." + groupName.toLowerCase())) {
          player.setDisplayName(Utilities.colourise(tablistManager.groupNameColour(groupName) + player.getName()) + ChatColor.RESET);
          return;
        }
      }
      return;
    }
    player.setDisplayName(Utilities.colourise(configFile.getString("Default_Name_Colour", "#fff954") + player.getName()) + ChatColor.RESET);
  }

  private void formatTablistAndNames() {
    for(Player player : Bukkit.getOnlinePlayers()) {
      ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
      Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
      Team team = scoreboard.registerNewTeam("players");
      String playerPrefix = (plugin.getChat().getPlayerPrefix(player) != null ? plugin.getChat().getPlayerPrefix(player) + " " : "");
      String playerSuffix = (plugin.getChat().getPlayerSuffix(player) != null ? plugin.getChat().getPlayerSuffix(player) + " " : "");

      team.addEntry(player.getName());
      team.setPrefix(Utilities.colourise(playerPrefix));
      team.setSuffix(Utilities.colourise(playerSuffix));
      team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);

      for(String groupNameColors : configFile.getConfigurationSection("Player_Name_Tags.Groups").getKeys(false)) {
        if(Utilities.checkPermission(player, false, "stylizer.nametags.groups." + groupNameColors)) {
          team.setColor(ChatColor.getByChar(configFile.getString("Player_Name_Tags.Groups." + groupNameColors, "&e").charAt(1)));
          break;
        }
      }
      tablistManager = new TablistManager(plugin, player);
      tablistManager.tablistPlayerName();
      player.setScoreboard(scoreboard);

    }
  }

  private void sortTablist() {
    ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
    Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
    Team team;
    List<String> groupNames = configFile.getStringList("Tablist.Sorting_Order.Order");

    for(Player player : Bukkit.getOnlinePlayers()) {
      for(String groupName : groupNames) {
        String playerPrefix = (plugin.getChat().getPlayerPrefix(player) != null ? plugin.getChat().getPlayerPrefix(player) + " " : "");
        String playerSuffix = (plugin.getChat().getPlayerSuffix(player) != null ? plugin.getChat().getPlayerSuffix(player) + " " : "");

        if(scoreboard.getTeam("group" + groupNames.indexOf(groupName)) == null) {
          team = scoreboard.registerNewTeam("group" + groupNames.indexOf(groupName));
        } else {
          team = scoreboard.getTeam("group" + groupNames.indexOf(groupName));
        }

        if(Utilities.checkPermission(player, false, "stylizer.tablist.order." + groupName)) {
          team.addEntry(player.getName());
          team.setPrefix(Utilities.colourise(playerPrefix));
          team.setSuffix(Utilities.colourise(playerSuffix));
          team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);

          if(configFile.getBoolean("Player_Name_Tags.Enabled")) {
            for(String nameTagGroup : configFile.getConfigurationSection("Player_Name_Tags.Groups").getKeys(false)) {
              if(Utilities.checkPermission(player, false, "stylizer.nametags.groups." + nameTagGroup)) {
                team.setColor(ChatColor.getByChar(configFile.getString("Player_Name_Tags.Groups." + nameTagGroup).charAt(1)));
                break;
              }
            }

          }
        }
        tablistManager = new TablistManager(plugin, player);
        tablistManager.tablistPlayerName();
        player.setScoreboard(scoreboard);
      }
    }
  }



}
