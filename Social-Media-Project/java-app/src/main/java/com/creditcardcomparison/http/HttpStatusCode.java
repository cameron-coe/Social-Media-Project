package com.creditcardcomparison.http;

public enum HttpStatusCode {

    /** --- SUCCESSFUL RESPONSES --- **/
    SUCCESS_RESPONSE_200_OK(200, "OK"),
    SUCCESS_RESPONSE_201_CREATED(201, "Created"),
    SUCCESS_RESPONSE_202_ACCEPTED(202, "Accepted"),
    SUCCESS_RESPONSE_204_NO_CONTENT(204, "No Content"),

    /** --- CLIENT ERRORS --- **/
    CLIENT_ERROR_400_BAD_REQUEST(400, "Bad Request"),
    CLIENT_ERROR_401_UNAUTHORIZED(401, "Unauthorized"),
    CLIENT_ERROR_403_FORBIDDEN(403, "Forbidden"),
    CLIENT_ERROR_404_NOT_FOUND(404, "Not Found"),
    CLIENT_ERROR_405_METHOD_NOT_ALLOWED(405, "Method Not Allowed"),

    /** --- SERVER ERRORS --- **/
    SERVER_ERROR_500_INTERNAL_SERVER_ERROR(500, "Internal Error"),
    SERVER_ERROR_501_NOT_IMPLEMENTED(501, "Method Not Implemented"),
    SERVER_ERROR_503_SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    SERVER_ERROR_504_GATEWAY_TIMEOUT(504, "Gateway Timeout"),
    SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),

    ;

    public final int STATUS_CODE;
    public final String MESSAGE;

    HttpStatusCode(int STATUS_CODE, String MESSAGE) {
        this.STATUS_CODE = STATUS_CODE;
        this.MESSAGE = MESSAGE;
    }
}
