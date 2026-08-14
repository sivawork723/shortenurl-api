package com.siva.shortenurlapi.helper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UrlValidationHelperTest {

    @Test
    void shouldAcceptValidUrl() {
        UrlValidationHelper helper = new UrlValidationHelper();
        assertTrue(helper.isValidUrl("http://google.com/v1/whetherstreaming/today/time=12"));
    }

    @Test
    void shouldRejectInvalidUrl() {
        UrlValidationHelper helper = new UrlValidationHelper();
        assertFalse(helper.isValidUrl("http:///bad-url"));
    }

}
