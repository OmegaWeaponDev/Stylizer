package me.omegaweapondev.stylizer.events;

import me.ou.library.Utilities;
import me.omegaweapondev.stylizer.Stylizer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import java.util.List;

public class ServerPingListener implements Listener {
  private final FileConfiguration configFile;

  public ServerPingListener(final Stylizer plugin) {
    configFile = plugin.getSettingsHandler().getConfigFile().getConfig();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void serverList(ServerListPingEvent listPingEvent) {

    if(!configFile.getBoolean("Server_Listing.Enabled")) {
      return;
    }

    final List<String> configMotd = configFile.getStringList("Server_Listing.Format");
    final StringBuilder motd = new StringBuilder();

    for(String string : configMotd) {
      motd.append(string).append("\n");
    }

    listPingEvent.setMotd(Utilities.componentSerializerFromString(motd.toString()));
  }
}
