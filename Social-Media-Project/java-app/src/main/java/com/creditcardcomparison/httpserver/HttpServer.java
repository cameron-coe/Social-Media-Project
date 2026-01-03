package com.creditcardcomparison.httpserver;

import com.creditcardcomparison.httpserver.config.Configuration;
import com.creditcardcomparison.httpserver.config.ConfigurationManager;
import com.creditcardcomparison.httpserver.core.ServerListenerThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Main Class for the Credit Card Comparison App
 */
public class HttpServer {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);

    public static void main(String[] args) {
        LOGGER.info("Getting the Server Started...");

        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.json");
        LOGGER.info("Configuration Setup Done");

        Configuration configuration = ConfigurationManager.getInstance().getCurrentConfiguration();
        LOGGER.info("Using Port: " + configuration.getPort());
        LOGGER.info("Using Webroot: " + configuration.getWebroot());

        ServerListenerThread serverListenerThread = null;
        try {
            serverListenerThread = new ServerListenerThread(configuration.getPort(), configuration.getWebroot());
        } catch (IOException e) {
            LOGGER.error("Problem with establishing thread", e);
            e.printStackTrace();
        }
        serverListenerThread.start();
    }
}
