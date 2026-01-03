package com.creditcardcomparison.http;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;



@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HttpParserTest {

    //private HttpParser target;

    @BeforeAll
    public void before_tests() {
        //
    }

    @Test
    void parse_valid_http_GET_request() {
        HttpRequest request = null;
        try {
            request = HttpParser.parseHttpRequest(
                    generateValidGetTestCase()
            );
        } catch (HttpParsingException e) {
            fail(e); // Fail if exception is given
        }

        assertEquals(HttpMethod.GET, request.getMethod());
        assertEquals("/", request.getRequestTarget());
        assertEquals(HttpVersion.HTTP_1_1, request.getBestCompatibleHttpVersion());
        assertEquals("", request.getBody());
    }

    @Test
    void parse_http_request_with_invalid_method_token() {
        try {
            HttpRequest request = HttpParser.parseHttpRequest(
                    generateInvalidMethodTokenTestCase()
            );
            fail(); // Fail the test if it parses a request with an invalid METHOD TOKEN
        } catch (HttpParsingException e) {
            assertEquals(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED, e.getErrorCode());
        }
    }

    @Test
    void request_should_not_parse_if_more_than_3_items_in_the_request_line() {
        try {
            HttpRequest request = HttpParser.parseHttpRequest(
                    generateRequestWithMoreThan3ItemsInTheRequestLine()
            );
            fail(); // Fail the test if it parses a request with MORE than 3 items in the request line
        } catch (HttpParsingException e) {
            assertEquals(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST, e.getErrorCode());
        }
    }

    @Test
    void request_should_not_parse_if_less_than_3_items_in_the_request_line() {
        try {
            HttpRequest request = HttpParser.parseHttpRequest(
                    generateRequestWithLessThan3ItemsInTheRequestLine()
            );
            fail(); // Fail the test if it parses a request with LESS than 3 items in the request line
        } catch (HttpParsingException e) {
            assertEquals(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST, e.getErrorCode());
        }
    }

    @Test
    void request_should_not_parse_if_only_CR_and_no_LF() {
        try {
            HttpRequest request = HttpParser.parseHttpRequest(
                    generateRequestWithOnlyCrAndNoLf()
            );
            fail(); // Fail the test if it parses a request with CR and not LF
        } catch (HttpParsingException e) {
            assertEquals(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST, e.getErrorCode());
        }
    }

    @Test
    void request_should_not_parse_with_bad_http_version_format() {
        try {
            HttpRequest request = HttpParser.parseHttpRequest(
                    generateRequestWithBadHttpVersionFormatting()
            );
            fail("The Request Should Not Be Parsed Given Bad HTTP Version Formatting");
        } catch (HttpParsingException e) {
            assertEquals(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST, e.getErrorCode());
        }
    }

    @Test
    void request_should_not_parse_unsupported_http_version() {
        try {
            HttpRequest request = HttpParser.parseHttpRequest(
                    generateRequestWithUnsupportedHttpVersion()
            );
            fail("The Request Should Not Be Parsed Given An Unsupported HTTP Version");
        } catch (HttpParsingException e) {
            assertEquals(HttpStatusCode.SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED, e.getErrorCode());
        }
    }

    @Test
    void request_gives_highest_supported_version_with_matching_major_and_different_minor() {
        try {
            HttpRequest request = HttpParser.parseHttpRequest(
                    generateRequestWithSupportedMajorAndHigherMinor()
            );

            assertEquals(HttpVersion.HTTP_1_1, request.getBestCompatibleHttpVersion());
        } catch (HttpParsingException e) {
            fail("The Minor Of The Version Should Default To A Lower Version When The Majors Are Matching");
        }

    }


    @Test
    void parse_valid_http_POST_request_with_string_body() {
        HttpRequest request = null;
        try {
            request = HttpParser.parseHttpRequest(
                    generateValidPostTestCaseWithStringBody()
            );
        } catch (HttpParsingException e) {
            fail(e); // Fail if exception is given
        }

        assertEquals(HttpMethod.POST, request.getMethod());
        assertEquals("/", request.getRequestTarget());
        assertEquals(HttpVersion.HTTP_1_1, request.getBestCompatibleHttpVersion());
        assertEquals("Hello World!", request.getBody());
    }

    @Test
    void parse_valid_http_POST_request_with_no_body() {
        HttpRequest request = null;
        try {
            request = HttpParser.parseHttpRequest(
                    generateValidPostTestCaseWithNoBody()
            );
        } catch (HttpParsingException e) {
            fail(e); // Fail if exception is given
        }

        assertEquals(HttpMethod.POST, request.getMethod());
        assertEquals("/", request.getRequestTarget());
        assertEquals(HttpVersion.HTTP_1_1, request.getBestCompatibleHttpVersion());
        assertEquals("", request.getBody());
    }

    @Test
    void get_headers_from_valid_POST_request_with_a_body() {
        HttpRequest request = null;
        try {
            request = HttpParser.parseHttpRequest(
                    generateValidPostTestCaseWithStringBody()
            );
        } catch (HttpParsingException e) {
            fail(e); // Fail if exception is given
        }

        assertEquals("localhost:8081", request.getHeader("Host"));
        assertEquals("keep-alive", request.getHeader("Connection"));
        assertEquals("max-age=0", request.getHeader("Cache-Control"));
        assertEquals("\"Chromium\";v=\"124\", \"Google Chrome\";v=\"124\", \"Not-A.Brand\";v=\"99\"", request.getHeader("sec-ch-ua"));
        assertEquals("?0", request.getHeader("sec-ch-ua-mobile"));
        assertEquals("\"Windows\"", request.getHeader("sec-ch-ua-platform"));
        assertEquals("1", request.getHeader("Upgrade-Insecure-Requests"));
        assertEquals("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36", request.getHeader("User-Agent"));
        assertEquals("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7", request.getHeader("Accept"));
        assertEquals("none", request.getHeader("Sec-Fetch-Site"));
        assertEquals("navigate", request.getHeader("Sec-Fetch-Mode"));
        assertEquals("?1+", request.getHeader("Sec-Fetch-User"));
        assertEquals("document", request.getHeader("Sec-Fetch-Dest"));
        assertEquals("gzip, deflate, br, zstd", request.getHeader("Accept-Encoding"));
        assertEquals("en-US,en;q=0.9,ko;q=0.8", request.getHeader("Accept-Language"));
    }

    @Test
    void header_without_a_colon_should_not_parse() {
        HttpRequest request = null;
        try {
            request = HttpParser.parseHttpRequest(
                    generateRequestWithImproperlyFormattedHeader()
            );
            fail("The Request Should Not Be Parsed Given A Header Without a Colon");
        } catch (HttpParsingException e) {
            assertEquals(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST, e.getErrorCode());
        }
    }

    @Test
    void request_with_a_header_with_no_value_and_a_colon_should_parse() {
        HttpRequest request = null;
        try {
            request = HttpParser.parseHttpRequest(
                    generateRequestWithAHeaderWithNoValue()
            );
        } catch (HttpParsingException e) {
            fail(e); // Fail if exception is given
        }

        assertEquals("", request.getHeader("Host"));
    }

    /** TODO: Make test for
     * request with no headers
     * request with bad formatting in the headers
     * request without a string body can be parsed
     */


    /** --- Non-Test Methods --- **/

    private InputStream dataToInputStream(String data) {
        InputStream inputStream = new ByteArrayInputStream(
                data.getBytes(
                        StandardCharsets.US_ASCII
                )
        );
        return inputStream;
    }

    private InputStream generateValidGetTestCase() {
        final String CRLF = "\r\n";

        String data = "GET / HTTP/1.1" + CRLF +
                "Host: localhost:8081" + CRLF +
                "Connection: keep-alive" + CRLF +
                "Cache-Control: max-age=0" + CRLF +
                "sec-ch-ua: \"Chromium\";v=\"124\", \"Google Chrome\";v=\"124\", \"Not-A.Brand\";v=\"99\"" + CRLF +
                "sec-ch-ua-mobile: ?0" + CRLF +
                "sec-ch-ua-platform: \"Windows\"" + CRLF +
                "Upgrade-Insecure-Requests: 1" + CRLF +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36" + CRLF +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7" + CRLF +
                "Sec-Fetch-Site: none" + CRLF +
                "Sec-Fetch-Mode: navigate" + CRLF +
                "Sec-Fetch-User: ?1+" + CRLF +
                "Sec-Fetch-Dest: document" + CRLF +
                "Accept-Encoding: gzip, deflate, br, zstd" + CRLF +
                "Accept-Language: en-US,en;q=0.9,ko;q=0.8" + CRLF +
                CRLF;

        return dataToInputStream(data);
    }

    private InputStream generateInvalidMethodTokenTestCase() {
        final String CRLF = "\r\n";

        String data = "INVALID_METHOD / HTTP/1.1" + CRLF +
                "Host: localhost:8081" + CRLF +
                "Accept-Language: en-US,en;q=0.9,ko;q=0.8" + CRLF +
                CRLF;

        return dataToInputStream(data);
    }

    private InputStream generateRequestWithMoreThan3ItemsInTheRequestLine() {
        final String CRLF = "\r\n";

        String data = "GET / EXTRA_ITEM HTTP/1.1" + CRLF +
                "Host: localhost:8081" + CRLF +
                "Accept-Language: en-US,en;q=0.9,ko;q=0.8" + CRLF +
                CRLF;

        return dataToInputStream(data);
    }

    private InputStream generateRequestWithLessThan3ItemsInTheRequestLine() {
        final String CRLF = "\r\n";

        String data = "GET /" + CRLF +
                "Host: localhost:8081" + CRLF +
                "Accept-Language: en-US,en;q=0.9,ko;q=0.8" + CRLF +
                CRLF;

        return dataToInputStream(data);
    }

    private InputStream generateRequestWithOnlyCrAndNoLf() {
        final String CRLF = "\r\n";
        final String CR = "\r";

        String data = "GET / HTTP/1.1" + CR +
                "Host: localhost:8081" + CRLF +
                "Accept-Language: en-US,en;q=0.9,ko;q=0.8" + CRLF +
                CRLF;

        return dataToInputStream(data);
    }

    private InputStream generateRequestWithBadHttpVersionFormatting() {
        final String CRLF = "\r\n";

        String data = "GET / HttP/1.1" + CRLF +
                "Host: localhost:8081" + CRLF +
                "Accept-Language: en-US,en;q=0.9,ko;q=0.8" + CRLF +
                CRLF;

        return dataToInputStream(data);
    }

    private InputStream generateRequestWithUnsupportedHttpVersion() {
        final String CRLF = "\r\n";

        String data = "GET / HTTP/500.1" + CRLF +
                "Host: localhost:8081" + CRLF +
                "Accept-Language: en-US,en;q=0.9,ko;q=0.8" + CRLF +
                CRLF;

        return dataToInputStream(data);
    }

    private InputStream generateRequestWithSupportedMajorAndHigherMinor() {
        final String CRLF = "\r\n";

        String data = "GET / HTTP/1.500" + CRLF +
                "Host: localhost:8081" + CRLF +
                "Accept-Language: en-US,en;q=0.9,ko;q=0.8" + CRLF +
                CRLF;

        return dataToInputStream(data);
    }


    private InputStream generateValidPostTestCaseWithStringBody() {
        final String CRLF = "\r\n";

        String data = "POST / HTTP/1.1" + CRLF +
                "Host: localhost:8081" + CRLF +
                "Connection: keep-alive" + CRLF +
                "Content-Length: 12" + CRLF +
                "Cache-Control: max-age=0" + CRLF +
                "sec-ch-ua: \"Chromium\";v=\"124\", \"Google Chrome\";v=\"124\", \"Not-A.Brand\";v=\"99\"" + CRLF +
                "sec-ch-ua-mobile: ?0" + CRLF +
                "sec-ch-ua-platform: \"Windows\"" + CRLF +
                "Upgrade-Insecure-Requests: 1" + CRLF +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36" + CRLF +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7" + CRLF +
                "Sec-Fetch-Site: none" + CRLF +
                "Sec-Fetch-Mode: navigate" + CRLF +
                "Sec-Fetch-User: ?1+" + CRLF +
                "Sec-Fetch-Dest: document" + CRLF +
                "Accept-Encoding: gzip, deflate, br, zstd" + CRLF +
                "Accept-Language: en-US,en;q=0.9,ko;q=0.8" + CRLF +
                CRLF +
                "Hello World!";

        return dataToInputStream(data);
    }


    private InputStream generateValidPostTestCaseWithNoBody() {
        final String CRLF = "\r\n";

        String data = "POST / HTTP/1.1" + CRLF +
                "Host: localhost:8081" + CRLF +
                "Connection: keep-alive" + CRLF +
                "Content-Length: 12" + CRLF +
                "Cache-Control: max-age=0" + CRLF +
                "sec-ch-ua: \"Chromium\";v=\"124\", \"Google Chrome\";v=\"124\", \"Not-A.Brand\";v=\"99\"" + CRLF +
                "sec-ch-ua-mobile: ?0" + CRLF +
                "sec-ch-ua-platform: \"Windows\"" + CRLF +
                "Upgrade-Insecure-Requests: 1" + CRLF +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36" + CRLF +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7" + CRLF +
                "Sec-Fetch-Site: none" + CRLF +
                "Sec-Fetch-Mode: navigate" + CRLF +
                "Sec-Fetch-User: ?1+" + CRLF +
                "Sec-Fetch-Dest: document" + CRLF +
                "Accept-Encoding: gzip, deflate, br, zstd" + CRLF +
                "Accept-Language: en-US,en;q=0.9,ko;q=0.8" + CRLF +
                CRLF;

        return dataToInputStream(data);
    }

    private InputStream generateRequestWithImproperlyFormattedHeader() {
        final String CRLF = "\r\n";

        String data = "GET / HTTP/1.1" + CRLF +
                "Host" + CRLF +
                CRLF;

        return dataToInputStream(data);
    }

    private InputStream generateRequestWithAHeaderWithNoValue() {
        final String CRLF = "\r\n";

        String data = "GET / HTTP/1.1" + CRLF +
                "Host:" + CRLF +
                CRLF;

        return dataToInputStream(data);
    }



}