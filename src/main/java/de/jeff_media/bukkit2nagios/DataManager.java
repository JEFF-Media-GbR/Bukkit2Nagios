package de.jeff_media.bukkit2nagios;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DataManager {

    Class serverClass;
    Object server;
    Field recentTPSField;
    Field processQueueField;

    final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    DataManager() {
        try {
            serverClass = Mirror.getNMSClass("MinecraftServer");
            server = serverClass.getDeclaredMethod("getServer").invoke(null);
            //serverClass = server.getClass();
            /*for(Field field : serverClass.getDeclaredFields()) {
                System.out.println(field.getType().getName()+" "+field.getName());
            }*/
            recentTPSField = serverClass.getDeclaredField("recentTps");
            processQueueField = serverClass.getDeclaredField("processQueue");
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
            Main.getInstance().getPluginLoader().disablePlugin(Main.getInstance());
        }
    }

    public double[] getTPS() throws IllegalAccessException {
        return (double[]) recentTPSField.get(server);
    }

    public String getDate() {
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    public Queue<Runnable> getProcessQueue() throws IllegalAccessException {
        return (Queue<Runnable>) processQueueField.get(server);
    }

    public static Pair<Integer,String> getBans(BanList.Type type) {
        StringBuilder sb = new StringBuilder();
        Set<BanEntry> entrySet = Main.getInstance().getServer().getBanList(type).getBanEntries();
        for(BanEntry entry : entrySet) {
            sb.append(entry.getTarget());
            sb.append(", ");
        }
        return new ImmutablePair<>(entrySet.size(), cutList(sb));
    }

    public static Pair<Pair<Integer,String>,Pair<Integer,String>> getPlugins() {
        Plugin[] plugins = Main.getInstance().getServer().getPluginManager().getPlugins();
        StringBuilder enabledList = new StringBuilder();
        int enabledCount = 0;
        StringBuilder disabledList = new StringBuilder();
        int disabledCount = 0;
        for(Plugin plugin : plugins) {
            if(plugin.isEnabled()) {
                enabledList.append(plugin.getName());
                enabledList.append(", ");
                enabledCount++;
            } else {
                disabledList.append(plugin.getName());
                disabledList.append(", ");
                disabledCount++;
            }
        }

        return new ImmutablePair<>(new ImmutablePair<>(enabledCount, cutList(enabledList)), new ImmutablePair<>(disabledCount, cutList(disabledList)));

    }

    public static String cutList(StringBuilder sb) {
        return cutList(sb.toString());
    }

    public static String cutList(String s) {
        if(s.length()<2) return s;
        return s.substring(0,s.length()-2);
    }

    public static Pair<Integer,String> getOnlinePlayers() {
        int online = 0;
        StringBuilder sb = new StringBuilder();
        for(Player p : Main.getInstance().getServer().getOnlinePlayers()) {
            sb.append(p.getName());
            sb.append(", ");
            online++;
        }
        return new ImmutablePair<>(online,cutList(sb));
    }

    public static int getMaxPlayers() {
        return Main.getInstance().getServer().getMaxPlayers();
    }


}
