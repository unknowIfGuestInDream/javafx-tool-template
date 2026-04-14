package com.tlcsdm.fxtemplate.controller;

import com.tlcsdm.fxtemplate.Launcher;
import com.tlcsdm.fxtemplate.config.AppResources;
import com.tlcsdm.fxtemplate.config.AppSettings;
import com.tlcsdm.fxtemplate.config.I18N;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.lang.management.ManagementFactory;

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

    @FXML
    private Button closeButton;

    private Stage primaryStage;
    private double dragOffsetX;
    private double dragOffsetY;

    private static final String TITLE_BUTTON_HOVER_STYLE = "-fx-background-color: -color-bg-default;";
    private static final String TITLE_BUTTON_CLOSE_HOVER_STYLE =
        "-fx-background-color: -color-danger-emphasis; -fx-text-fill: -color-fg-emphasis;";

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

    /**
     * Restart the application.
     */
    @FXML
    public void restartApplication() {
        try {
            List<String> restartCommand = buildRestartCommand(
                System.getProperty("java.home"),
                resolveLaunchPath(),
                System.getProperty("java.class.path"),
                ManagementFactory.getRuntimeMXBean().getInputArguments());
            new ProcessBuilder(restartCommand).start();
            shutdown();
            closePrimaryStage();
        } catch (IOException e) {
            LOG.error("Failed to restart application", e);
            statusLabel.setText(I18N.get("status.restartFailed"));
        }
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

    @FXML
    public void onWindowButtonMouseEntered(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            String baseStyle = button.getStyle();
            button.getProperties().put("baseStyle", baseStyle);
            button.setStyle(baseStyle + (button == closeButton ? TITLE_BUTTON_CLOSE_HOVER_STYLE : TITLE_BUTTON_HOVER_STYLE));
        }
    }

    @FXML
    public void onWindowButtonMouseExited(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            Object baseStyle = button.getProperties().get("baseStyle");
            button.setStyle(baseStyle instanceof String style ? style : "");
        }
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
        AppResources.applyDialogLogo(alert, primaryStage);
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

    static List<String> buildRestartCommand(String javaHome, Path launchPath, String classPath, List<String> vmArguments) {
        List<String> command = new ArrayList<>();
        command.add(getJavaExecutable(javaHome));
        command.addAll(filterRestartVmArguments(vmArguments));
        if (launchPath != null && launchPath.toString().endsWith(".jar")) {
            command.add("-jar");
            command.add(launchPath.toString());
        } else {
            command.add("-cp");
            command.add(classPath);
            command.add(Launcher.class.getName());
        }
        return command;
    }

    static List<String> filterRestartVmArguments(List<String> vmArguments) {
        List<String> filteredArguments = new ArrayList<>();
        if (vmArguments == null) {
            return filteredArguments;
        }
        for (String argument : vmArguments) {
            if (argument.startsWith("-agentlib")
                || argument.startsWith("-agentpath")
                || argument.startsWith("-javaagent")) {
                continue;
            }
            filteredArguments.add(argument);
        }
        return filteredArguments;
    }

    private static String getJavaExecutable(String javaHome) {
        String executable = System.getProperty("os.name", "").toLowerCase().contains("win") ? "java.exe" : "java";
        return Paths.get(javaHome, "bin", executable).toString();
    }

    private Path resolveLaunchPath() throws IOException {
        CodeSource codeSource = Launcher.class.getProtectionDomain().getCodeSource();
        if (codeSource == null) {
            throw new IOException("Unable to determine application launch path: code source is unavailable");
        }
        try {
            return Path.of(codeSource.getLocation().toURI());
        } catch (URISyntaxException e) {
            throw new IOException("Failed to resolve launch path from code source URI", e);
        } catch (SecurityException e) {
            throw new IOException("Access denied while resolving application launch path", e);
        }
    }
}
