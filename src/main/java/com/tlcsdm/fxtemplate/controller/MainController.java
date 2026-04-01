package com.tlcsdm.fxtemplate.controller;

import com.tlcsdm.fxtemplate.config.AppSettings;
import com.tlcsdm.fxtemplate.config.I18N;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main controller for the template application.
 */
public class MainController {

    private static final Logger LOG = LoggerFactory.getLogger(MainController.class);

    @FXML
    private Label statusLabel;

    @FXML
    private Label windowTitleLabel;

    @FXML
    private Button maximizeButton;

    private Stage primaryStage;
    private double dragOffsetX;
    private double dragOffsetY;

    @FXML
    public void initialize() {
        statusLabel.setText(I18N.get("status.ready"));
    }

    /**
     * Set the primary stage reference.
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
        this.primaryStage.maximizedProperty().addListener((obs, oldVal, newVal) -> updateMaximizeButtonText());
        updateMaximizeButtonText();
    }

    /**
     * Open the settings dialog.
     */
    @FXML
    public void openSettings() {
        AppSettings.getInstance().getPreferencesFx().show(true);
    }

    /**
     * Exit the application.
     */
    @FXML
    public void exitApplication() {
        shutdown();
        closePrimaryStage();
    }

    @FXML
    public void onTitleBarMousePressed(MouseEvent event) {
        if (primaryStage == null) {
            return;
        }
        dragOffsetX = event.getScreenX() - primaryStage.getX();
        dragOffsetY = event.getScreenY() - primaryStage.getY();
    }

    @FXML
    public void onTitleBarMouseDragged(MouseEvent event) {
        if (primaryStage == null || primaryStage.isMaximized()) {
            return;
        }
        primaryStage.setX(event.getScreenX() - dragOffsetX);
        primaryStage.setY(event.getScreenY() - dragOffsetY);
    }

    @FXML
    public void onTitleBarMouseClicked(MouseEvent event) {
        if (event.getClickCount() == 2) {
            toggleMaximizeWindow();
        }
    }

    @FXML
    public void onMinimizeWindow() {
        if (primaryStage != null) {
            primaryStage.setIconified(true);
        }
    }

    @FXML
    public void onToggleMaximizeWindow() {
        toggleMaximizeWindow();
    }

    @FXML
    public void onCloseWindow() {
        closePrimaryStage();
    }

    /**
     * Show about dialog.
     */
    @FXML
    public void showAbout() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(I18N.get("menu.about"));
        alert.setHeaderText(I18N.get("app.title"));
        alert.setContentText(I18N.get("about.description"));
        alert.showAndWait();
    }

    /**
     * Shutdown and cleanup resources.
     */
    public void shutdown() {
        LOG.info("Application shutting down");
    }

    private void closePrimaryStage() {
        if (primaryStage != null) {
            primaryStage.close();
        }
    }

    private void toggleMaximizeWindow() {
        if (primaryStage == null) {
            return;
        }
        primaryStage.setMaximized(!primaryStage.isMaximized());
        updateMaximizeButtonText();
    }

    private void updateMaximizeButtonText() {
        if (maximizeButton != null && primaryStage != null) {
            maximizeButton.setText(primaryStage.isMaximized() ? "❐" : "□");
        }
    }
}
