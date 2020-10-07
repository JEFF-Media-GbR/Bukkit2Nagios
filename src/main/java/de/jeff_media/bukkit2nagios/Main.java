package de.jeff_media.bukkit2nagios;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class Main extends JavaPlugin implements CommandExecutor {

    static Main instance;
    DataCollector dataCollector;
    BukkitTask task;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        dataCollector = new DataCollector();
        TelnetServer.startServer();
    }


}
