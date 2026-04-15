package com.tlcsdm.fxtemplate.config;

import com.dlsc.preferencesfx.PreferencesFx;
import com.dlsc.preferencesfx.model.Category;
import com.dlsc.preferencesfx.model.Group;
import com.dlsc.preferencesfx.model.Setting;
import com.tlcsdm.fxtemplate.model.DisplayLocale;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;

import java.util.Optional;
import java.util.prefs.Preferences;
import java.util.Arrays;
import java.util.List;

/**
 * Application settings management using PreferencesFX.
 */
public class AppSettings {

    private static final String WINDOW_X_KEY = "window.x";
    private static final String WINDOW_Y_KEY = "window.y";
    private static final String WINDOW_WIDTH_KEY = "window.width";
    private static final String WINDOW_HEIGHT_KEY = "window.height";
    private static final Preferences PREFS = Preferences.userNodeForPackage(AppSettings.class);

    private static AppSettings instance;

    private final ObjectProperty<DisplayLocale> languageProperty;
    private final ObjectProperty<AppTheme> themeProperty;

    private PreferencesFx preferencesFx;
    private boolean suppressRebuild;

    private AppSettings() {
        languageProperty = new SimpleObjectProperty<>(new DisplayLocale(I18N.getCurrentLocale()));
        themeProperty = new SimpleObjectProperty<>(AppTheme.getSavedTheme());

        languageProperty.addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals(oldVal) && !suppressRebuild) {
                I18N.setLocale(newVal.getLocale());
                rebuildPreferences();
            }
        });

        themeProperty.addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals(oldVal)) {
                newVal.apply();
                AppTheme.saveTheme(newVal);
            }
        });
    }

    /**
     * Get the singleton instance.
     */
    public static AppSettings getInstance() {
        if (instance == null) {
            instance = new AppSettings();
        }
        return instance;
    }

    /**
     * Get the language property.
     */
    public ObjectProperty<DisplayLocale> languageProperty() {
        return languageProperty;
    }

    /**
     * Get the theme property.
     */
    public ObjectProperty<AppTheme> themeProperty() {
        return themeProperty;
    }

    /**
     * Create and get the PreferencesFx instance.
     */
    public PreferencesFx getPreferencesFx() {
        if (preferencesFx == null) {
            buildPreferences();
        }
        return preferencesFx;
    }

    private void buildPreferences() {
        DisplayLocale savedLocale = new DisplayLocale(I18N.getCurrentLocale());
        List<DisplayLocale> supportedLocales = Arrays.stream(I18N.getSupportedLocales())
            .map(DisplayLocale::new)
            .toList();
        List<AppTheme> themes = Arrays.asList(AppTheme.values());

        suppressRebuild = true;
        try {
            preferencesFx = PreferencesFx.of(AppSettings.class,
                Category.of(I18N.get("settings.general"),
                    Group.of(I18N.get("settings.languageAndTheme"),
                        Setting.of(I18N.get("settings.language"),
                            FXCollections.observableArrayList(supportedLocales),
                            languageProperty),
                        Setting.of(I18N.get("settings.theme"),
                            FXCollections.observableArrayList(themes),
                            themeProperty)
                    )
                )
            ).persistWindowState(true)
                .saveSettings(true)
                .debugHistoryMode(false)
                .buttonsVisibility(false)
                .instantPersistent(true)
                .dialogIcon(AppResources.getLogo());

            if (!savedLocale.equals(languageProperty.get())) {
                languageProperty.set(savedLocale);
            }
        } finally {
            suppressRebuild = false;
        }
    }

    private void rebuildPreferences() {
        preferencesFx = null;
    }

    /**
     * Apply initial settings (called at application startup).
     */
    public void applyInitialSettings() {
        AppTheme.applySavedTheme();
    }

    public void saveMainWindowBounds(double x, double y, double width, double height) {
        if (!Double.isFinite(x) || !Double.isFinite(y)
            || !Double.isFinite(width) || !Double.isFinite(height)
            || width <= 0 || height <= 0) {
            return;
        }
        PREFS.putDouble(WINDOW_X_KEY, x);
        PREFS.putDouble(WINDOW_Y_KEY, y);
        PREFS.putDouble(WINDOW_WIDTH_KEY, width);
        PREFS.putDouble(WINDOW_HEIGHT_KEY, height);
    }

    public Optional<WindowBounds> getMainWindowBounds() {
        String widthValue = PREFS.get(WINDOW_WIDTH_KEY, null);
        String heightValue = PREFS.get(WINDOW_HEIGHT_KEY, null);
        String xValue = PREFS.get(WINDOW_X_KEY, null);
        String yValue = PREFS.get(WINDOW_Y_KEY, null);

        if (widthValue == null || heightValue == null || xValue == null || yValue == null) {
            return Optional.empty();
        }

        double width = PREFS.getDouble(WINDOW_WIDTH_KEY, -1);
        double height = PREFS.getDouble(WINDOW_HEIGHT_KEY, -1);
        double x = PREFS.getDouble(WINDOW_X_KEY, Double.NaN);
        double y = PREFS.getDouble(WINDOW_Y_KEY, Double.NaN);

        if (!Double.isFinite(x) || !Double.isFinite(y)
            || !Double.isFinite(width) || !Double.isFinite(height)
            || width <= 0 || height <= 0) {
            return Optional.empty();
        }
        return Optional.of(new WindowBounds(x, y, width, height));
    }

    public record WindowBounds(double x, double y, double width, double height) {
    }
}
