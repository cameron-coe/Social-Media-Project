package com.creditcardcomparison.httpserver.config;

/**
 * This is the class I mapped the hhtp.json to
 */

public class Configuration {
    private int port;
    private String webroot;


    /**
     * Getters
     */
    public int getPort() {
        return port;
    }

    public String getWebroot() {
        return webroot;
    }


    /**
     * Setters
     */
    public void setPort(int port) {
        this.port = port;
    }

    public void setWebroot(String webroot) {
        this.webroot = webroot;
    }
}
