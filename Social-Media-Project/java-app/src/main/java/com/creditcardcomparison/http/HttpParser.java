package com.creditcardcomparison.http;

import com.creditcardcomparison.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class HttpParser {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);

    private static final int SPACE = 0x20; // 32
    private static final int CARRIAGE_RETURN = 0x0D; // 13
    private static final int LINE_FEED = 0x0A; // 10
    private static final int COLON = 0x3A; // 58

    public static HttpRequest parseHttpRequest(InputStream inputStream) throws HttpParsingException {
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII);

        HttpRequest request = new HttpRequest();

        // Parse Request Line
        try {
            parseRequestLine(reader, request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Parse Headers
        try {
            parseHeaders(reader, request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Parse Body
        try {
            parseBody(reader, request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return request;
    }

    private static void parseRequestLine(InputStreamReader reader, HttpRequest request) throws IOException, HttpParsingException {
        StringBuilder processingDataBuffer = new StringBuilder();

        boolean methodParsed = false;
        boolean requestTargetParsed = false;

        int currentByte = 0;
        while (reader.ready()) {
            currentByte = reader.read();
            if(currentByte == CARRIAGE_RETURN) {
                currentByte = reader.read();
                if(currentByte == LINE_FEED) {
                    LOGGER.debug("Request Line VERSION to Process: {}", processingDataBuffer.toString());
                    try {
                        request.setHttpVersion(processingDataBuffer.toString());
                    } catch (BadHttpVersionException e) {
                        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                    }

                    // Create an error if there are LESS than 3 items in the request line
                    if(!methodParsed || !requestTargetParsed) {
                        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                    }

                    return;
                } else {
                    // When next character after Carriage Return is not a Line Feed
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
            }

            // Handle
            if (currentByte == SPACE) {
                if (!methodParsed) {
                    LOGGER.debug("Request Line METHOD to Process: {}", processingDataBuffer.toString());
                    request.setMethod(processingDataBuffer.toString());
                    methodParsed = true;
                } else if (!requestTargetParsed) {
                    LOGGER.debug("Request Line REQUEST TARGET to Process: {}", processingDataBuffer.toString());
                    request.setRequestTarget(processingDataBuffer.toString());
                    requestTargetParsed = true;
                } else {
                    // Create an error if there are MORE than 3 items in the request line
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
                processingDataBuffer.delete(0, processingDataBuffer.length()); // Clear Buffer
            } else {
                processingDataBuffer.append((char)currentByte);

                // Limit the length of the method
                if (!methodParsed) {
                    if (processingDataBuffer.length() > HttpMethod.MAX_LENGTH) {
                        throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
                    }
                }
            }
        }
    }

    // TODO: add bad requests and clean up this code
    // TODO: ask chatgpt if header name without content is a bad request or should be skipped
    private static void parseHeaders(InputStreamReader reader, HttpRequest request) throws  IOException, HttpParsingException {
        StringBuilder processingDataBuffer = new StringBuilder();


        int currentByte = 0;
        while (reader.ready()) {
            currentByte = reader.read();
            if (currentByte == CARRIAGE_RETURN) {
                currentByte = reader.read();
                if (currentByte == LINE_FEED) {

                    String headerLine = processingDataBuffer.toString();
                    int colonIndex = headerLine.indexOf(':');
                    if (colonIndex == -1) {
                        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                    }

                    String headerName = headerLine.substring(0, colonIndex).trim();

                    // Header value is empty if there is nothing after the colon
                    String headerValue = "";
                    if (colonIndex + 1 < headerLine.length()) {
                        headerValue = headerLine.substring(colonIndex + 1).trim();
                    }

                    LOGGER.debug("Header Parsed: {}: {}", headerName, headerValue);
                    request.addHeader(headerName, headerValue);

                    // Clear the buffer for the next header line
                    processingDataBuffer.delete(0, processingDataBuffer.length());
                    currentByte = reader.read();
                } else {
                    // When next character after Carriage Return is not a Line Feed
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
            }

            // End this method if there are 2 CRLF's in a row
            if (currentByte == CARRIAGE_RETURN) {
                currentByte = reader.read();
                if (currentByte == LINE_FEED) {
                    return;
                } else {
                    // When next character after Carriage Return is not a Line Feed
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
            }

            // Add character to buffer
            processingDataBuffer.append((char)currentByte);
        }
    }


    private static void parseBody(InputStreamReader reader, HttpRequest request) throws IOException, HttpParsingException {
        StringBuilder bodyBuilder = new StringBuilder();

        // Read characters from the reader until end of stream
        int currentByte = 0;
        while (reader.ready()) {
            currentByte = reader.read();

            // Append the character to the bodyBuilder
            bodyBuilder.append((char) currentByte);

        }

        request.setRequestBody(bodyBuilder.toString());
    }

}
