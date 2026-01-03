package com.creditcardcomparison.http;

public enum HttpMethod {
    GET, POST, PUT, DELETE, OPTIONS, HEAD;
    public static final int MAX_LENGTH;


    static {

        // Automatically sets the value of max length to the longest method
        int maxLength = 0;
        for (HttpMethod method : values()) {
            if (method.name().length() > maxLength) {
                maxLength = method.name().length();
            }
        }
        MAX_LENGTH = maxLength;
    }
}
