package com.tlcsdm.fxtemplate;

import com.tlcsdm.fxtemplate.config.AppResources;
import com.tlcsdm.fxtemplate.config.AppSettings;
import com.tlcsdm.fxtemplate.config.I18N;
import com.tlcsdm.fxtemplate.config.WindowResizeHelper;
import com.tlcsdm.fxtemplate.controller.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Main JavaFX Application for the template tool.
 */
public class TemplateApplication extends Application {

    private static final Logger LOG = LoggerFactory.getLogger(TemplateApplication.class);

    private MainController controller;
    private Stage primaryStage;

    @Override
    public void init() {
        // Apply saved theme before UI is created
        AppSettings.getInstance().applyInitialSettings();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        primaryStage.initStyle(StageStyle.UNDECORATED);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        loader.setResources(I18N.getBundle());
        Parent root = loader.load();
        controller = loader.getController();
        controller.setPrimaryStage(primaryStage);

        Scene scene = new Scene(root, 900, 700);

        primaryStage.setTitle(I18N.get("app.title"));
        AppResources.applyWindowIcon(primaryStage);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        applySavedWindowBounds(primaryStage);

        primaryStage.setOnCloseRequest(event -> {
            persistWindowBounds(primaryStage);
            if (controller != null) {
                controller.shutdown();
            }
            Platform.exit();
        });

        WindowResizeHelper.addResizeListener(primaryStage);

        primaryStage.show();
        LOG.info("Application started");
    }

    @Override
    public void stop() {
        persistWindowBounds(primaryStage);
        if (controller != null) {
            controller.shutdown();
        }
    }

    private void applySavedWindowBounds(Stage primaryStage) {
        AppSettings.getInstance().getMainWindowBounds().ifPresent(bounds -> {
            primaryStage.setWidth(Math.max(bounds.width(), primaryStage.getMinWidth()));
            primaryStage.setHeight(Math.max(bounds.height(), primaryStage.getMinHeight()));
            primaryStage.setX(bounds.x());
            primaryStage.setY(bounds.y());
        });
    }

    private void persistWindowBounds(Stage primaryStage) {
        if (primaryStage == null || primaryStage.isMaximized() || primaryStage.isIconified()) {
            return;
        }
        AppSettings.getInstance().saveMainWindowBounds(
            primaryStage.getX(),
            primaryStage.getY(),
            primaryStage.getWidth(),
            primaryStage.getHeight());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
