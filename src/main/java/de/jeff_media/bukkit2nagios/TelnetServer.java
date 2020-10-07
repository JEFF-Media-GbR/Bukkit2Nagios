package de.jeff_media.bukkit2nagios;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class TelnetServer {

    public static void startServer() {
        Main.getInstance().task = Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), TelnetServer::connectToServer);
    }

    public static void connectToServer() {

        final String fieldSeparator = Main.getInstance().dataCollector.separator;
        final String lineSeparator = ";"+fieldSeparator + ";" + fieldSeparator + ";";
        while(true) {
            try (ServerSocket serverSocket = new ServerSocket(9991)) {
                Main.getInstance().getLogger().info("Socket created.");
                Socket connectionSocket = serverSocket.accept();
                Main.getInstance().getLogger().info("Client connected from " + connectionSocket.getRemoteSocketAddress().toString());
                //InputStream inputToServer = connectionSocket.getInputStream();
                OutputStream outputFromServer = connectionSocket.getOutputStream();
                //Scanner scanner = new Scanner(inputToServer, "UTF-8");
                PrintWriter serverPrintOut = new PrintWriter(new OutputStreamWriter(outputFromServer, StandardCharsets.UTF_8), true);

                serverPrintOut.println("Bukkit2Nagios v" + Main.getInstance().getDescription().getVersion());
                serverPrintOut.println(fieldSeparator);
                try {
                    Map<String, Pair<String, String>> data = Main.getInstance().dataCollector.collect();
                    for (Map.Entry<String, Pair<String, String>> entry : data.entrySet()) {
                        String sb = entry.getKey() +
                                fieldSeparator +
                                entry.getValue().getLeft() +
                                fieldSeparator +
                                entry.getValue().getRight() +
                                lineSeparator;
                        serverPrintOut.print(sb);
                    }
                } catch (IllegalAccessException e) {
                    serverPrintOut.println("error collecting data");
                    e.printStackTrace();
                }
                serverPrintOut.println("");
                connectionSocket.close();

            } catch (IOException e) {
                //e.printStackTrace();
            }
        }


    }
}