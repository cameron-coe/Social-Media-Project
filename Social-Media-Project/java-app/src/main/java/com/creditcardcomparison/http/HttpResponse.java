package com.creditcardcomparison.http;

import com.creditcardcomparison.controller.MainController;

import java.util.ArrayList;
import java.util.List;

public class HttpResponse extends HttpMessage {

    private HttpRequest request;
    private String responseBody;
    private MainController mainController = new MainController();
    private List<Cookie> cookies = new ArrayList<>();

    /** --- Constructor --- **/
    public HttpResponse(HttpRequest request) {
        this.request = request;
        this.setHttpStatusCode(HttpStatusCode.SUCCESS_RESPONSE_200_OK);
        this.responseBody = mainController.getResponseBody(this);
    }

    /** --- Getters --- **/
    public HttpRequest getRequest() {
        return this.request;
    }

    public String getHttpVersionLiteral() {
        return request.getHttpVersionLiteral();
    }

    public HttpVersion getBestCompatibleHttpVersion() {
        return request.getBestCompatibleHttpVersion();
    }

    public int getHttpStatusCode() {
        return request.getHttpStatusCode();
    }

    public String getHttpStatusMessage() {
        return request.getHttpStatusMessage();
    }

    public String getResponseBody() {
        return responseBody;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    /** --- Setters --- **/
    public void setHttpStatusCode(HttpStatusCode httpStatusCode) {
        request.setHttpStatusCode(httpStatusCode);
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public void addCookie(Cookie cookie) {
        this.cookies.add(cookie);
    }
}
