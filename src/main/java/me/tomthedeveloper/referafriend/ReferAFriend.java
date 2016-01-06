package me.tomthedeveloper.referafriend;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Tom on 10/08/2015.
 */
public class ReferAFriend extends JavaPlugin {

    private HashMap<String,String> messages = new HashMap<String, String>();
    private FileConfiguration playerDataConfig;
    private HashMap<Integer, String[]> commands = new HashMap<Integer, String[]>();

    private String prefix = ChatColor.DARK_GREEN + "[Refers] " + ChatColor.GREEN;


    public void loadObjectives(){
        FileConfiguration objconfig = ConfigurationManager.getConfig("Commands");
        if(!objconfig.contains("REFERS_HERE"))
            objconfig.set("REFERS_HERE", Arrays.asList(new String[]{"PUT_HERE_A_COMMAND_WITHOUT_THE_SLASH", "ANOTHER COMMAND HERE", "UNLITMITED COMMANDS"}));
        objconfig.set("1", Arrays.asList(new String[]{"say %PLAYER% reffered %REFERRED%", "eco give %REFERRED% 100"}));
        objconfig.set("2", Arrays.asList(new String[]{"%REFERRED% has now two refers!", "eco give %REFERRED% 100"}));
        try {
            objconfig.save(ConfigurationManager.getFile("Commands"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(String path:objconfig.getKeys(false)){
            if(!path.contains("REFERS_HERE"))
                commands.put(Integer.parseInt(path),objconfig.getList(path).toArray(new String[objconfig.getList(path).size()]));
        }
    }

    private void loadMessages(){
        FileConfiguration messageconfig = ConfigurationManager.getConfig("messages");
        messages.put("No-Refer-Alts", prefix+ "You can't refer one of your alts!");
        messages.put("Succesfull-Refer", prefix+"You succesfully referred %PLAYER%");
        messages.put("No-Refer-Yourself",prefix+ "You cannot refer yourself!");
        messages.put("Already-Reffered-A-Friend", prefix+"You already referred a friend!");
        messages.put("You-Cannot-Refer-A-Unknown-Player",prefix+ "You cannot refer a friend who hasn't played on this server yet!");
        messages.put("You-Cannot-Refer-One-Who-Referred-You",prefix+ "You cannot refer a person who reffered you!");
        messages.put("Got-Reffered-By", prefix+"You got reffered by %PLAYER%");
        messages.put("Refer-Top-Header-Line", ChatColor.DARK_GREEN + "----------{" + ChatColor.GREEN + " TOP REFFERED PLAYERS" + ChatColor.DARK_GREEN +" }----------");
        messages.put("Refer-Top-Footer-Line", ChatColor.DARK_GREEN +  "-------------------------------------------");
        messages.put("Refer-Top- Position", ChatColor.DARK_GREEN + "%NUMBER%. " + ChatColor.GREEN + "%PLAYER% - %REFERS%");
        messages.put("Refer-Help-Header-Line",  ChatColor.DARK_GREEN + "----------{" + ChatColor.GREEN + " REFER HELP" + ChatColor.DARK_GREEN +" }----------");
        messages.put("Refer-Help-Refer-Command", ChatColor.DARK_GREEN + "/refer <player> :" + ChatColor.GREEN + " Refers a player.");
        messages.put("Refer-Help-Top-Command", ChatColor.DARK_GREEN + "/refer top :" + ChatColor.GREEN + " Check out the top referred players!");
        messages.put("Refer-Help-Footer-Line", ChatColor.DARK_GREEN +  "-----------------------------------");

        for(String messagePath:messages.keySet()){
            if(!messageconfig.contains(messagePath)){
                messageconfig.set(messagePath, messages.get(messagePath));
            }else{
                messages.put(messagePath,messageconfig.getString(messagePath));
            }
        }
        try {
            messageconfig.save(ConfigurationManager.getFile("messages"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onEnable(){
        ConfigurationManager.plugin = this;
        playerDataConfig = ConfigurationManager.getConfig("PlayerData");

        this.getCommand("refer").setExecutor(new ReferCommand(this));
        this.getServer().getPluginManager().registerEvents(new events(this),this);
        loadObjectives();
        loadMessages();
    }



    public String getMessage(String string){
        return messages.get(string);
    }

    public HashMap<Integer,String[]> getCommands(){
        return commands;
    }

    private String formatMessage(String message, int integer){
        String returnstring = message;
        returnstring = returnstring.replaceAll("%NUMBER%",Integer.toString(integer));
        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        return returnstring;
    }

    public String getMessage(String string, OfflinePlayer player){
        String returnstring = messages.get(string);
        returnstring = returnstring.replaceAll("%PLAYER%",player.getName());
        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        return returnstring;
    }
    public String getMessage(String string, OfflinePlayer player, int i){
        String returnstring = messages.get(string);
        returnstring = returnstring.replaceAll("%PLAYER%",player.getName());
        returnstring = returnstring.replaceAll("%NUMBER%",Integer.toString(i));

        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        return returnstring;
    }

    public String getMessage(String string, OfflinePlayer player, int i, int refers){
        String returnstring = messages.get(string);
        returnstring = returnstring.replaceAll("%PLAYER%",player.getName());
        returnstring = returnstring.replaceAll("%NUMBER%",Integer.toString(i));
        returnstring = returnstring.replaceAll("%REFERS%",Integer.toString(refers));

        returnstring = returnstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
        return returnstring;
    }
    public String getHostName(UUID uuid){
        if(playerDataConfig.contains(uuid.toString() + ".hostname")) {
            return playerDataConfig.getString(uuid.toString() + ".hostname");
        }else {
            return "";
        }
    }
}
