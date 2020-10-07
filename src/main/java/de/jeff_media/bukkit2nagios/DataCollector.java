package de.jeff_media.bukkit2nagios;

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

    public LinkedHashMap<String,Pair<String,String>> collect() throws IllegalAccessException {

        long start = System.nanoTime();

        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        double[] tps = dataManager.getTPS();
        Runnable[] processQueue = dataManager.getProcessQueue().toArray(new Runnable[0]);
        Pair<Integer,String> ipBans = DataManager.getBans(BanList.Type.IP);
        Pair<Integer,String> nameBans = DataManager.getBans(BanList.Type.NAME);
        Pair<Pair<Integer,String>,Pair<Integer,String>> plugins = DataManager.getPlugins();
        Pair<Integer,String> onlinePlayers = DataManager.getOnlinePlayers();


        map.put("date", dataManager.getDate());
        map.put("version", Bukkit.getVersion());
        map.put("bukkit version", Bukkit.getBukkitVersion());
        map.put("server name",Bukkit.getName());
        map.put("server name stripped",strip(Bukkit.getName()));
        map.put("server motd",Bukkit.getMotd());
        map.put("server motd stripped",strip(Bukkit.getMotd()));
        map.put("tps 1m", formatTps(tps[0]));
        map.put("tps 5m", formatTps(tps[1]));
        map.put("tps 15m", formatTps(tps[2]));
        map.put("online players count",String.valueOf(onlinePlayers.getLeft()));
        map.put("online players list",onlinePlayers.getRight());
        map.put("max players",String.valueOf(DataManager.getMaxPlayers()));
        map.put("process queue", String.valueOf(processQueue.length));
        map.put("banned names count", String.valueOf(nameBans.getLeft()));
        map.put("banned names list", nameBans.getRight());
        map.put("banned ips count", String.valueOf(ipBans.getLeft()));
        map.put("banned ips list", ipBans.getRight());
        map.put("plugins enabled count", String.valueOf(plugins.getLeft().getLeft()));
        map.put("plugins enabled list", plugins.getLeft().getRight());
        map.put("plugins disabled count", String.valueOf(plugins.getLeft().getLeft()));
        map.put("plugins disabled list", plugins.getRight().getRight());
        map.put("collection time",ns2ms(System.nanoTime()-start));

        LinkedHashMap<String,Pair<String,String>> strippedMap = new LinkedHashMap<>();

        for(Map.Entry<String, String> entry : map.entrySet()) {
            if(enabledKeys.containsKey(entry.getKey())) {
                strippedMap.put(entry.getKey(),new ImmutablePair<>(enabledKeys.get(entry.getKey()),entry.getValue()));
                System.out.println("Key "+entry.getKey()+" is enabled using name "+enabledKeys.get(entry.getKey())+" with value: \""+entry.getValue()+"\"");
            }
        }

        return strippedMap;
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
