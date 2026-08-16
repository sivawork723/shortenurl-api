package com.siva.shortenurlapi.helper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnterpriseUrlValidationHelperTest {

    @Test
    void shouldAcceptValidUrl() {
        EnterpriseUrlValidationHelper helper = new EnterpriseUrlValidationHelper();
        assertTrue(helper.isValid("http://google.com/v1/whetherstreaming/today/time=12"));
    }

    @Test
    void shouldRejectInvalidUrl() {
        EnterpriseUrlValidationHelper helper = new EnterpriseUrlValidationHelper();
        assertFalse(helper.isValid("http:///bad-url"));
    }

}
