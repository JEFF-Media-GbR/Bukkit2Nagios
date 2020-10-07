package de.jeff_media.bukkit2nagios;

import org.bukkit.Bukkit;

public class Mirror {
    static Class<?> getNMSClass(String name) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
    }
}
