package com.creditcardcomparison.http;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class HttpVersionTest {


    @Test
    void get_best_compatible_version_exact_match() {
        HttpVersion version = null;
        try {
            version = HttpVersion.getBestCompatibleVersion("HTTP/1.1");
        } catch (BadHttpVersionException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(HttpVersion.HTTP_1_1, version);
    }

    @Test
    void get_best_compatible_version_on_bad_request() {
        HttpVersion version = null;
        try {
            version = HttpVersion.getBestCompatibleVersion("http/1.1");
            fail(); // Fail if incompatible version is detected
        } catch (BadHttpVersionException ignored) {}
    }

    @Test
    void get_best_compatible_version_for_compatible_major_and_higher_minor() {
        HttpVersion version = null;
        try {
            version = HttpVersion.getBestCompatibleVersion("HTTP/1.200");
        } catch (BadHttpVersionException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(HttpVersion.HTTP_1_1, version);
    }



}
