package me.omegaweapondev.stylizer.events;

import me.omegaweapondev.stylizer.Stylizer;
import me.omegaweapondev.stylizer.utilities.MessageAnnouncements;
import me.omegaweapondev.stylizer.utilities.MessageHandler;
import me.omegaweapondev.stylizer.utilities.PlayerUtil;
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
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlayerListener implements Listener {
  private final Stylizer plugin;
  private final FileConfiguration configFile;

  private TablistManager tablistManager;
  private PlayerUtil playerUtil;

  public PlayerListener(final Stylizer plugin) {
    this.plugin = plugin;
    configFile = plugin.getSettingsHandler().getConfigFile().getConfig();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
    Player player = playerJoinEvent.getPlayer();
    tablistManager = new TablistManager(plugin, player);
    playerUtil = new PlayerUtil(plugin, player);

    player.setDisplayName(playerUtil.getNameColour() + player.getName() + ChatColor.RESET);
    Bukkit.getScheduler().runTaskTimer(plugin, () -> tablistManager.tablistHeaderFooter(), 20L * 5L, 20 * 20L);

    if(configFile.getBoolean("Tablist.Sorting_Order.Enabled")) {
      Bukkit.getScheduler().runTaskTimer(plugin, this::sortTablist, 20L * 5L, 20L * 20L);
    } else {
      Bukkit.getScheduler().runTaskTimer(plugin, this::formatTablistAndNames, 20L * 5, 20 * 20L);
    }

    if(Bukkit.getOnlinePlayers().size() == 1) {
      messageAnnouncements();
    }

    SpigotUpdater(player);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
    if(Bukkit.getOnlinePlayers().size() - 1 <= 0) {
      Bukkit.getScheduler().cancelTasks(plugin);
    }
  }

  private void formatTablistAndNames() {
    for(Player player : Bukkit.getOnlinePlayers()) {
      ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
      Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
      Team team = scoreboard.registerNewTeam("players");
      String playerPrefix = (plugin.getChat().getGroupPrefix(player.getWorld(), plugin.getChat().getPrimaryGroup(player)) != null ? plugin.getChat().getGroupPrefix(player.getWorld(), plugin.getChat().getPrimaryGroup(player)) + " " : "");
      String playerSuffix = (plugin.getChat().getGroupSuffix(player.getWorld(), plugin.getChat().getPrimaryGroup(player)) != null ? plugin.getChat().getGroupSuffix(player.getWorld(), plugin.getChat().getPrimaryGroup(player)) + " " : "");

      team.addEntry(player.getName());

      if(configFile.getBoolean("Player_Name_Tags.Enabled") && configFile.getBoolean("Player_Name_Tags.Player_Prefixes")) {
        team.setPrefix(Utilities.colourise(playerPrefix));
        team.setSuffix(Utilities.colourise(playerSuffix));
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
      }

      if(configFile.getBoolean("Player_Name_Tags.Enabled")) {
        for(String nameTagGroup : configFile.getConfigurationSection("Player_Name_Tags.Groups").getKeys(false)) {
          if(Utilities.checkPermission(player, false, "stylizer.nametags.groups." + nameTagGroup)) {
            team.setColor(ChatColor.getByChar(configFile.getString("Player_Name_Tags.Groups." + nameTagGroup).charAt(1)));
            break;
          }
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
        if(scoreboard.getTeam("group" + groupNames.indexOf(groupName)) == null) {
          team = scoreboard.registerNewTeam("group" + groupNames.indexOf(groupName));
        } else {
          team = scoreboard.getTeam("group" + groupNames.indexOf(groupName));
        }

        if(Utilities.checkPermission(player, false, "stylizer.tablist.order." + groupName)) {
          String playerPrefix = (plugin.getChat().getGroupPrefix(player.getWorld(), groupName) != null ? plugin.getChat().getGroupPrefix(player.getWorld(), groupName) + " " : "");
          String playerSuffix = (plugin.getChat().getGroupSuffix(player.getWorld(), groupName) != null ? plugin.getChat().getGroupSuffix(player.getWorld(), groupName) + " " : "");

          team.addEntry(player.getName());

          if(configFile.getBoolean("Player_Name_Tags.Enabled") && configFile.getBoolean("Player_Name_Tags.Player_Prefixes")) {
            team.setPrefix(Utilities.colourise(playerPrefix));
            team.setSuffix(Utilities.colourise(playerSuffix));
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
          }

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
        break;
      }
    }
  }

  private void SpigotUpdater(@NotNull final Player player) {
    if(!configFile.getBoolean("Update_Messages")) {
      return;
    }

    // Send the player a message on join if there is an update for the plugin
    if(Utilities.checkPermissions(player, true, "stylizer.update", "stylizer.admin")) {
      new SpigotUpdater(plugin, 78327).getVersion(version -> {
        int spigotVersion = Integer.parseInt(version.replace(".", ""));
        int pluginVersion = Integer.parseInt(plugin.getDescription().getVersion().replace(".", ""));

        if(pluginVersion >= spigotVersion) {
          MessageHandler messageHandler = new MessageHandler(plugin, plugin.getSettingsHandler().getMessagesFile().getConfig());

          Utilities.message(player, messageHandler.getPrefix() + "#00D4FFYou are already running the latest version");
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

  private void messageAnnouncements() {
    if(!configFile.getBoolean("Announcement_Messages.Enabled")) {
      return;
    }

    if(configFile.getConfigurationSection("Announcement_Messages.Messages").getKeys(false).isEmpty()) {
      return;
    }

    MessageAnnouncements messageAnnouncements = new MessageAnnouncements(plugin);

    Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, messageAnnouncements::broadcastAnnouncements,
    20L * plugin.getSettingsHandler().getConfigFile().getConfig().getInt("Announcement_Messages.Interval"),
    20L * plugin.getSettingsHandler().getConfigFile().getConfig().getInt("Announcement_Messages.Interval")
    );
  }
}
