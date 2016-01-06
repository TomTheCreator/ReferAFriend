package me.tomthedeveloper.referafriend;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;

/**
 * Created by Tom on 10/08/2015.
 */
public class events implements Listener {

    private ReferAFriend plugin;
    private FileConfiguration config;



    public events(ReferAFriend plugin){
        this.plugin = plugin;
         config = ConfigurationManager.getConfig("PlayerData");
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        config.set(event.getPlayer().getUniqueId().toString() + ".hostname", event.getPlayer().getAddress().getHostName());
        try {
            config.save(ConfigurationManager.getFile("PlayerData"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
