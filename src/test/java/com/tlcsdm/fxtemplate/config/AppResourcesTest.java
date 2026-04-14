package com.tlcsdm.fxtemplate.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for shared application resources.
 */
class AppResourcesTest {

    @Test
    void testLogoPngExists() {
        assertNotNull(AppResources.class.getResource(AppResources.LOGO_PATH), "logo.png should exist");
    }

    @Test
    void testLogoIcoExists() {
        assertNotNull(AppResources.class.getResource("/com/tlcsdm/fxtemplate/logo.ico"), "logo.ico should exist");
    }
}
