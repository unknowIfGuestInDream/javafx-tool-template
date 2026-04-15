package com.tlcsdm.fxtemplate.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for shared application resources.
 */
class AppResourcesTest {

    @Test
    void testLogoPngExists() {
        assertNotNull(AppResources.class.getResource(AppResources.LOGO_PATH), "logo.png should exist");
    }

    @Test
    void testLogoIcoExistsInScripts() {
        java.nio.file.Path icoPath = java.nio.file.Path.of("scripts", "logo.ico");
        assertTrue(java.nio.file.Files.exists(icoPath), "logo.ico should exist in scripts directory");
    }
}
