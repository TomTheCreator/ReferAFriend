package me.tomthedeveloper.referafriend;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.*;

/**
 * Created by Tom on 10/08/2015.
 */
public class ReferCommand implements CommandExecutor {

    private ReferAFriend plugin;
    private FileConfiguration config;
    private HashMap<Integer,UUID> toplist = new LinkedHashMap<Integer, UUID>();

    public ReferCommand(ReferAFriend plugin){
        this.plugin = plugin;
        config = ConfigurationManager.getConfig("Refers");
    }

    private Random random = new Random();


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (!command.getLabel().equalsIgnoreCase("refer"))
                return true;
            if (strings == null || strings.length == 0) {
                player.sendMessage(plugin.getMessage("Refer-Help-Header-Line"));
                player.sendMessage(plugin.getMessage("Refer-Help-Refer-Command"));
                player.sendMessage(plugin.getMessage("Refer-Help-Top-Command"));
                player.sendMessage(plugin.getMessage("Refer-Help-Footer-Line"));
            }
            if (strings != null && strings.length == 1 && strings[0].equalsIgnoreCase("top")) {

                for (int b = 1; b <= 10; b++) {
                    toplist.put(b, null);
                }
                for (String path : config.getKeys(false)) {
                    if (!config.contains(path + ".refers"))
                        continue;
                    int i = config.getInt(path + ".refers");
                    Iterator it = toplist.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        Integer rang = (Integer) pair.getKey();
                        if ((UUID) toplist.get(rang) == null) {
                            toplist.put(rang, UUID.fromString(path));
                            break;
                        }
                        if (i > config.getInt(toplist.get(rang) + ".refers")) {
                            insertScore(rang, UUID.fromString(path));
                            break;
                        }

                    }

                }
                player.sendMessage(plugin.getMessage("Refer-Top-Header-Line"));
                for (int rang : toplist.keySet()) {
                    if (toplist.get(rang) == null)
                        break;
                    player.sendMessage(plugin.getMessage("Refer-Top- Position", Bukkit.getOfflinePlayer(toplist.get(rang)), rang, config.getInt(toplist.get(rang).toString() + ".refers")));
                }
                player.sendMessage(plugin.getMessage("Refer-Top-Footer-Line"));
                return true;
            }
            if (strings != null && strings.length == 1) {
                OfflinePlayer referred = Bukkit.getOfflinePlayer(strings[0]);
                if (!referred.hasPlayedBefore()) {
                    player.sendMessage(plugin.getMessage("You-Cannot-Refer-A-Unknown-Player"));
                    return true;
                } else if (strings[0].equalsIgnoreCase(player.getName())) {
                    player.sendMessage(plugin.getMessage("No-Refer-Yourself"));
                    return true;

                } else if (plugin.getHostName(referred.getUniqueId()).equalsIgnoreCase(player.getAddress().getHostName())) {
                    System.out.print("test");
                    player.sendMessage(plugin.getMessage("No-Refer-Alts"));
                    return true;
                } else if (config.contains(player.getUniqueId().toString() + ".referred")) {
                    player.sendMessage(plugin.getMessage("Already-Reffered-A-Friend"));
                    return true;

                } else if (config.contains(player.getUniqueId().toString() + "referrals." + referred.getUniqueId().toString())) {
                    player.sendMessage(plugin.getMessage("You-Cannot-Refer-One-Who-Referred-You"));
                    return true;
                } else {
                    player.sendMessage(plugin.getMessage("Succesfull-Refer", referred));
                    config.set(player.getUniqueId().toString() + ".referred", true);
                    if (referred.isOnline()) {
                        Player referredonline = Bukkit.getPlayer(referred.getUniqueId());
                        referredonline.sendMessage(plugin.getMessage("Got-Reffered-By", player));
                    }
                    config.set(referred.getUniqueId().toString() + ".referrals." + player.getUniqueId().toString(), true);
                    if (config.contains(referred.getUniqueId().toString() + ".refers")) {
                        config.set(referred.getUniqueId().toString() + ".refers", config.getInt(referred.getUniqueId().toString() + ".refers") + 1);
                    } else {
                        config.set(referred.getUniqueId().toString() + ".refers", 1);
                    }
                    try {
                        config.save(ConfigurationManager.getFile("Refers"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    for (Map.Entry<Integer, String[]> entry : plugin.getCommands().entrySet()) {
                        Integer key = entry.getKey();
                        String[] value = entry.getValue();
                        for (String string : value) {
                            Integer time = config.getInt(referred.getUniqueId().toString() + ".refers");
                            if (time != key)
                                continue;
                            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), string.replaceAll("%REFERRED%", referred.getName()).replaceAll("%PLAYER%", player.getName()));
                        }

                    }
                }
            }
        }
        return true;
    }


    private void insertScore(int rang, UUID uuid){
        UUID after = toplist.get(rang);
        toplist.put(rang,uuid);
        if(!(rang>10))
            insertScore(rang + 1, after);
    }
}
