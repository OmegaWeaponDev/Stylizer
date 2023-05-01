package me.omegaweapondev.stylizer.events;

import me.omegaweapondev.stylizer.Stylizer;
import me.omegaweapondev.stylizer.utilities.MessageAnnouncements;
import me.omegaweapondev.stylizer.utilities.PlayerUtil;
import me.omegaweapondev.stylizer.utilities.TablistManager;
import me.ou.library.SpigotUpdater;
import me.ou.library.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import java.util.Comparator;
import java.util.List;

public class PlayerListener implements Listener {
  private final Stylizer plugin;
  private final FileConfiguration configFile;
  private final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

  private TablistManager tablistManager;

  public PlayerListener(final Stylizer plugin) {
    this.plugin = plugin;
    configFile = plugin.getSettingsHandler().getConfigFile().getConfig();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
    Player player = playerJoinEvent.getPlayer();
    tablistManager = new TablistManager(plugin, player);
    PlayerUtil playerUtil = new PlayerUtil(plugin, player);

    player.setDisplayName(Utilities.componentSerializerFromString(playerUtil.getNameColour() + player.getName()));
    Bukkit.getScheduler().runTaskTimer(plugin, () -> tablistManager.tablistHeaderFooter(), 20L * 5L, 20 * 20L);

    if(configFile.getBoolean("Tablist.Sorting_Order.Enabled")) {
      Bukkit.getScheduler().runTaskTimer(plugin, this::sortTablist, 20L * 5L, 20L * 20L);
    } else {
      Bukkit.getScheduler().runTaskTimer(plugin, this::formatTablist, 20L * 5, 20 * 20L);
    }

    formatNameTags(player);
    spigotUpdater(player);

    if(Bukkit.getOnlinePlayers().size() == 1) {
      messageAnnouncements();
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
    if(Bukkit.getOnlinePlayers().size() - 1 <= 0) {
      Bukkit.getScheduler().cancelTasks(plugin);
    }
  }

  private void formatTablist() {
    Bukkit.getOnlinePlayers().stream().sorted(Comparator.comparing(Player::getDisplayName)).forEachOrdered(player -> {
      tablistManager = new TablistManager(plugin, player);
      tablistManager.tablistPlayerName();
    });
  }

  private void sortTablist() {
    List<String> groupNames = configFile.getStringList("Tablist.Sorting_Order.Order");

    for(Player player : Bukkit.getOnlinePlayers()) {
      for(String groupName : groupNames) {
        if(Utilities.checkPermission(player, false, "stylizer.tablist.order." + groupName)) {
          Team team = scoreboard.getTeam("group_" + groupNames.indexOf(groupName));
          team.addEntry(player.getName());
        }
        tablistManager = new TablistManager(plugin, player);
        tablistManager.tablistPlayerName();
        break;
      }
    }
  }

  private void formatNameTags(Player player) {
    if (!configFile.getBoolean("Player_Name_Tags.Enabled")) return;
    Team nameTagTeam;

    if(configFile.getBoolean("Player_Name_Tags.Player_Prefixes")) {
      nameTagTeam = player.getScoreboard().getEntryTeam(player.getName());

      if(configFile.getBoolean("Player_Name_Tags.Group_Formats.Enabled")) {

        nameTagTeam.setPrefix(tablistManager.playerPrefix +
          Utilities.componentSerializerFromString(
            configFile.getString("Player_Name_Tags.Group_Formats.Groups." +
              nameTagTeam.getName().substring(6), "")
          )
        );

      } else {
        nameTagTeam.setPrefix(tablistManager.playerPrefix);
      }
      nameTagTeam.setSuffix(tablistManager.playerSuffix);
      nameTagTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
      nameTagTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
    }
  }

  private void spigotUpdater(final Player player) {
    if (!configFile.getBoolean("Update_Message")) return;
    if (!Utilities.checkPermissions(player, true, "stylizer.update", "stylizer.admin")) return;

    // Send a message in console if there is a new version of the plugin
    if(plugin.getSettingsHandler().getConfigFile().getConfig().getBoolean("Update_Messages")) {
      new SpigotUpdater(plugin, 78327).getVersion(version -> {
        int spigotVersion = Integer.parseInt(version.replace(".", ""));
        int pluginVersion = Integer.parseInt(plugin.getDescription().getVersion().replace(".", ""));

        if(pluginVersion >= spigotVersion) {
          Utilities.logInfo(true, "You are already running the latest version");
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
