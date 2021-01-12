package me.omegaweapondev.stylizer.events;

import me.omegaweapondev.stylizer.Stylizer;
import me.ou.library.Utilities;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerPingListener implements Listener {
  private final Stylizer plugin;
  private final FileConfiguration configFile;

  public ServerPingListener(final Stylizer plugin) {
    this.plugin = plugin;
    configFile = plugin.getConfigFile().getConfig();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void serverList(ServerListPingEvent listPingEvent) {

    if(!configFile.getBoolean("Server_Listing.Enabled")) {
      return;
    }

    String serverMOTD = "";

    for(String string : configFile.getStringList("Server_Listing.Format")) {
      serverMOTD = serverMOTD.concat(Utilities.colourise(string) + "\n");
    }
    listPingEvent.setMotd(serverMOTD);
  }
}
