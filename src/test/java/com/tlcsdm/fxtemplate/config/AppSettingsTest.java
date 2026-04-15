package com.tlcsdm.fxtemplate.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.prefs.Preferences;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for application settings persistence.
 */
class AppSettingsTest {

    private static final Preferences PREFS = Preferences.userNodeForPackage(AppSettings.class);

    @BeforeEach
    void setUp() {
        cleanUp();
    }

    @AfterEach
    void cleanUp() {
        PREFS.remove("window.x");
        PREFS.remove("window.y");
        PREFS.remove("window.width");
        PREFS.remove("window.height");
    }

    @Test
    void testSaveAndReadMainWindowBounds() {
        AppSettings settings = AppSettings.getInstance();
        settings.saveMainWindowBounds(100, 120, 960, 720);

        Optional<AppSettings.WindowBounds> bounds = settings.getMainWindowBounds();
        assertTrue(bounds.isPresent(), "Saved bounds should be readable");
        assertEquals(100, bounds.get().x());
        assertEquals(120, bounds.get().y());
        assertEquals(960, bounds.get().width());
        assertEquals(720, bounds.get().height());
    }

    @Test
    void testInvalidBoundsAreIgnored() {
        AppSettings settings = AppSettings.getInstance();
        settings.saveMainWindowBounds(100, 120, -1, 720);

        assertTrue(settings.getMainWindowBounds().isEmpty(), "Invalid size should not be persisted");
    }
}
