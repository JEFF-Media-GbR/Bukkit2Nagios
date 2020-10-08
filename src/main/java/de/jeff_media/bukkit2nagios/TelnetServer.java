package de.jeff_media.bukkit2nagios;

import com.google.gson.Gson;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TelnetServer {

    public static void startServer() {
        Main.getInstance().task = Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), TelnetServer::connectToServer);
    }

    public static void connectToServer() {
        while(true) {
            try (ServerSocket serverSocket = new ServerSocket(9991)) {
                Main.getInstance().getLogger().info("Socket created.");
                Socket connectionSocket = serverSocket.accept();
                Main.getInstance().getLogger().info("Client connected from " + connectionSocket.getRemoteSocketAddress().toString());
                //InputStream inputToServer = connectionSocket.getInputStream();
                OutputStream outputFromServer = connectionSocket.getOutputStream();
                //Scanner scanner = new Scanner(inputToServer, "UTF-8");
                PrintWriter serverPrintOut = new PrintWriter(new OutputStreamWriter(outputFromServer, StandardCharsets.UTF_8), true);

                try {
                    String data = Main.getInstance().dataCollector.collect();
                    serverPrintOut.println(data);
                } catch (IllegalAccessException e) {
                    HashMap<String,String> map = new HashMap<>();
                    map.put("error","illegal access error while trying to fetch data");
                    serverPrintOut.println(new Gson().toJson(map, HashMap.class));
                }

                connectionSocket.close();

            } catch (IOException e) {
                //e.printStackTrace();
            }
        }


    }
}