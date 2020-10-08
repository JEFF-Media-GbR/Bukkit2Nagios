package de.jeff_media.bukkit2nagios;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataCollector {

    final DataManager dataManager = new DataManager();
    final HashMap<String,String> enabledKeys = new HashMap<>();
    final String separator;

    DataCollector() {
            for (String key : Main.getInstance().getConfig().getConfigurationSection("data").getKeys(false)) {
                if (isEnabled(key)) {
                    String name = getName(key);
                    enabledKeys.put(key,name);
                }
            }
        separator = ";;\\t;\\t";
    }

    public String collect() throws IllegalAccessException {

        long start = System.nanoTime();

        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        double[] tps = dataManager.getTPS();
        Runnable[] processQueue = dataManager.getProcessQueue().toArray(new Runnable[0]);
        Pair<Integer,String> ipBans = DataManager.getBans(BanList.Type.IP);
        Pair<Integer,String> nameBans = DataManager.getBans(BanList.Type.NAME);
        Pair<Pair<Integer,String>,Pair<Integer,String>> plugins = DataManager.getPlugins();
        Pair<Integer,String> onlinePlayers = DataManager.getOnlinePlayers();


        map.put("Date", dataManager.getDate());
        map.put("Version", Bukkit.getVersion());
        map.put("Bukkit Version", Bukkit.getBukkitVersion());
        //map.put("Server Name",Bukkit.getName());
        //map.put("server name stripped",strip(Bukkit.getName()));
        //map.put("MOTD",Bukkit.getMotd());
        map.put("MOTD",strip(Bukkit.getMotd()));
        map.put("TPS 1m", formatTps(tps[0]));
        map.put("TPS 5m", formatTps(tps[1]));
        map.put("TPS 15m", formatTps(tps[2]));
        map.put("Online Players",String.valueOf(onlinePlayers.getLeft()));
        //map.put("Online Players List",onlinePlayers.getRight());
        map.put("Max Players",String.valueOf(DataManager.getMaxPlayers()));
        map.put("Process Queue", String.valueOf(processQueue.length));
        map.put("Banned Names", String.valueOf(nameBans.getLeft()));
        //map.put("Banned Names List", nameBans.getRight());
        map.put("Banned IPs", String.valueOf(ipBans.getLeft()));
        //map.put("Banned IPs List", ipBans.getRight());
        map.put("Plugins enabled", String.valueOf(plugins.getLeft().getLeft()));
        //map.put("Plugins enabled List", plugins.getLeft().getRight());
        map.put("Plugins disabled", String.valueOf(plugins.getRight().getLeft()));
        //map.put("Plugins disabled List", plugins.getRight().getRight());
        map.put("Collection Time",ns2ms(System.nanoTime()-start));

        return new Gson().toJson(map, LinkedHashMap.class);
    }

    private String formatTps(double tps) {
        tps=Math.min(2000,tps*100);
        tps = (int) tps;
        tps = tps/100;
        return String.valueOf(tps);
    }

    private String ns2ms(long ns) {
        ns = ns / 10000;
        double ms = (int)ns;
        return ms / 100 +" ms";
    }

    private String strip(String text) {
        return ChatColor.stripColor(text.trim());
    }

    private boolean isEnabled(String key) {
        return Main.getInstance().getConfig().getBoolean("data."+key+".enabled",false);
    }

    private String getName(String key) {
        if(Main.getInstance().getConfig().getString("data."+key+".rename","").equals("")) {
            return key;
        } else {
            return Main.getInstance().getConfig().getString("data."+key+".rename");
        }
    }

}
