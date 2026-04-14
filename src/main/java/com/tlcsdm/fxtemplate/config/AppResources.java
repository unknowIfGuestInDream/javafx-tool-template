package com.tlcsdm.fxtemplate.config;

import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.Objects;

/**
 * Shared application resources.
 */
public final class AppResources {

    public static final String LOGO_PATH = "/com/tlcsdm/fxtemplate/logo.png";

    private static final Image LOGO = loadImage(LOGO_PATH);

    private AppResources() {
        // Utility class
    }

    public static Image getLogo() {
        return LOGO;
    }

    public static ImageView createLogoView(double size) {
        ImageView imageView = new ImageView(LOGO);
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        return imageView;
    }

    public static void applyWindowIcon(Stage stage) {
        if (stage != null && !stage.getIcons().contains(LOGO)) {
            stage.getIcons().add(LOGO);
        }
    }

    public static void applyDialogLogo(Dialog<?> dialog, Stage owner) {
        if (dialog == null) {
            return;
        }
        if (owner != null) {
            dialog.initOwner(owner);
        }
        dialog.setGraphic(createLogoView(48));
        applyWindowIconWhenReady(dialog.getDialogPane().getScene());
        dialog.getDialogPane().sceneProperty().addListener((obs, oldScene, newScene) -> applyWindowIconWhenReady(newScene));
    }

    private static void applyWindowIconWhenReady(Scene scene) {
        if (scene == null) {
            return;
        }
        if (scene.getWindow() instanceof Stage stage) {
            applyWindowIcon(stage);
            return;
        }
        scene.windowProperty().addListener((obs, oldWindow, newWindow) -> {
            if (newWindow instanceof Stage stage) {
                applyWindowIcon(stage);
            }
        });
    }

    private static Image loadImage(String path) {
        try (InputStream inputStream = AppResources.class.getResourceAsStream(path)) {
            return new Image(Objects.requireNonNull(inputStream, () -> "Missing resource: " + path));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load image resource: " + path, e);
        }
    }
}
