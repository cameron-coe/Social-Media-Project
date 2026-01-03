package com.creditcardcomparison.http;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest extends HttpMessage {

    private HttpMethod method; // GET, POST, PUT, DELETE, ETC.
    private String requestTarget; // The Path
    private Map<String, String> headers = new HashMap<>(); // Headers

    private int httpStatusCode;
    private String httpStatusMessage;
    private String httpVersionLiteral;  // literal (string version) from the request
    private HttpVersion bestCompatibleHttpVersion;

    private String requestBody = "";


    HttpRequest() {
        this.setHttpStatusCode(HttpStatusCode.SUCCESS_RESPONSE_200_OK);
    }


    /** --- GETTERS --- **/

    public HttpMethod getMethod() {
        return this.method;
    }

    public String getTarget() {
        return requestTarget;
    }

    public HttpVersion getBestCompatibleHttpVersion() {
        return bestCompatibleHttpVersion;
    }

    public String getHttpVersionLiteral() {
        return httpVersionLiteral;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getHttpStatusMessage() {
        return httpStatusMessage;
    }

    public String getBody() {
        return requestBody;
    }

    public String[] getArrayOfTarget() {
        return this.getTarget().split("/");
    }

    public String getHeader(String name) {
        return headers.get(name);
    }


    /** --- SETTERS --- **/

    public void setMethod(String methodName) throws HttpParsingException {
        for (HttpMethod method : HttpMethod.values()) {
            if (methodName.equals(method.name())) {
                this.method = method;
                return;
            }
        }
        throw new HttpParsingException(
                HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED
        );
    }

    public void setRequestTarget(String requestTarget) throws HttpParsingException {
        if (requestTarget == null) {
            throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        }
        if (requestTarget.isEmpty()) {
            requestTarget = "/";  // Default
        }
        this.requestTarget = requestTarget;
    }

    public String getRequestTarget() {
        return requestTarget;
    }

    public void setHttpVersion(String httpVersionLiteral) throws BadHttpVersionException, HttpParsingException {
        this.httpVersionLiteral = httpVersionLiteral;
        this.bestCompatibleHttpVersion = HttpVersion.getBestCompatibleVersion(httpVersionLiteral);
        if (this.bestCompatibleHttpVersion == null) {
            throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED);
        }
    }

    public void setHttpStatusCode(HttpStatusCode httpStatusCode) {
        this.httpStatusCode = httpStatusCode.STATUS_CODE;
        this.httpStatusMessage = httpStatusCode.MESSAGE;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }
}
