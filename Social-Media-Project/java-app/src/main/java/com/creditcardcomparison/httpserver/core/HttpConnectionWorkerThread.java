package com.creditcardcomparison.httpserver.core;

import com.creditcardcomparison.http.*;
import com.creditcardcomparison.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class HttpConnectionWorkerThread extends Thread {

    private final String CRLF = "\r\n";

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);
    private Socket socket;

    public HttpConnectionWorkerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        BufferedReader reader = null;

        OutputStream outputStream = null;
        PrintWriter writer = null;

        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            writer = new PrintWriter(outputStream, true);

            String responseBody = "";

            String httpVersionLiteral = HttpVersion.HTTP_1_1.LITERAL;
            String httpStatusCode = "" + HttpStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR.STATUS_CODE;
            String httpStatusMessage = HttpStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR.MESSAGE;

            String allowedOrigin = "*"; // Change this to your desired origin or "*" for any origin
            String corsHeaders =
                    "Access-Control-Allow-Origin: " + allowedOrigin + CRLF +
                            "Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS" + CRLF +
                            "Access-Control-Allow-Headers: Content-Type, Authorization" + CRLF;

            try {
                HttpRequest request = HttpParser.parseHttpRequest(inputStream);

                // Handle OPTIONS requests for CORS preflight
                if (request.getMethod() == HttpMethod.OPTIONS) {
                    String responseLine = httpVersionLiteral + " 200 OK";
                    String headers =
                            responseLine + CRLF +
                                    corsHeaders +
                                    "Content-Length: 0" + CRLF +
                                    CRLF;

                    writer.print(headers);
                    writer.flush();
                    return; // Early return since we handled the preflight request
                }

                HttpResponse response = new HttpResponse(request);

                httpVersionLiteral = response.getHttpVersionLiteral();
                httpStatusCode = "" + response.getHttpStatusCode();
                httpStatusMessage = response.getHttpStatusMessage();
                responseBody = response.getResponseBody();
            } catch (HttpParsingException e) {
                httpStatusCode = "" + e.getErrorCode().STATUS_CODE;
                httpStatusMessage = e.getErrorCode().MESSAGE;
            }

            String responseLine = httpVersionLiteral + " " + httpStatusCode + " " + httpStatusMessage;
            String headers =
                    responseLine + CRLF +
                            corsHeaders +
                            "Content-Length: " + responseBody.getBytes().length + CRLF +
                            "Content-Type: text/html" + CRLF +
                            CRLF;

            writer.print(headers + responseBody + CRLF + CRLF);
            writer.flush();

            LOGGER.info("Connection Processing Finished");
        } catch (IOException e) {
            LOGGER.error("Problem with communication", e);
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {}
            }
            if (writer != null) {
                writer.close();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {}
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {}
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {}
            }
        }
    }
}
