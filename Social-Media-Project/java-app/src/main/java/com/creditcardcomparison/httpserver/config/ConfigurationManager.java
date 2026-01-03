package com.creditcardcomparison.httpserver.config;

import com.creditcardcomparison.httpserver.util.Json;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class ConfigurationManager {
    private static ConfigurationManager myConfigurationmanager;
    private static Configuration myCurrentConfiguration;

    private ConfigurationManager() {
        //
    }

    public static ConfigurationManager getInstance() {
        if (myConfigurationmanager == null) {
            myConfigurationmanager = new ConfigurationManager();
        }
        return myConfigurationmanager;
    }

    /**
     * Loads the configuration with the path provided
     */
    public void loadConfigurationFile(String filepath){
        File jsonFile = new File(filepath);
        StringBuffer buffer = new StringBuffer();

        try(Scanner input = new Scanner(jsonFile)) {
            while(input.hasNext()) {
                String currentLine = input.nextLine();
                buffer.append(currentLine);
            }
        } catch (FileNotFoundException e) {
            throw new HttpConfigurationException("JSON file not found: " + e);
        }

        JsonNode config = null;
        try {
            config = Json.parse(buffer.toString());
        } catch (JsonProcessingException e) {
            throw new HttpConfigurationException("Error parsing the JSON configuration file: " + e);
        }

        try {
            myCurrentConfiguration = Json.fromJson(config, Configuration.class);
        } catch (JsonProcessingException e) {
            throw new HttpConfigurationException("Error parsing the JSON configuration file, internal: " + e);
        }
    }

    /**
     * Returns the currently loaded configuration
     */
    public Configuration getCurrentConfiguration() {
        if (myCurrentConfiguration == null) {
            throw new HttpConfigurationException("No current configuration set.");
        }
        return myCurrentConfiguration;
    }
}
