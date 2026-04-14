package com.tlcsdm.fxtemplate.config;

import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Enables drag-to-resize on an undecorated {@link Stage}.
 * <p>
 * Detects when the mouse is near the window edges or corners and changes the cursor
 * to indicate the resize direction. Dragging then resizes the window accordingly.
 */
public final class WindowResizeHelper {

    private static final int RESIZE_BORDER = 6;

    private double startX;
    private double startY;
    private double startWidth;
    private double startHeight;
    private double startStageX;
    private double startStageY;
    private ResizeDirection direction = ResizeDirection.NONE;

    private WindowResizeHelper() {
    }

    /**
     * Attaches resize behavior to the given stage's scene.
     * Must be called after the scene has been set on the stage.
     */
    public static void addResizeListener(Stage stage) {
        if (stage == null || stage.getScene() == null) {
            return;
        }
        WindowResizeHelper helper = new WindowResizeHelper();
        Scene scene = stage.getScene();
        scene.addEventFilter(MouseEvent.MOUSE_MOVED, event -> helper.onMouseMoved(event, stage));
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> helper.onMousePressed(event, stage));
        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> helper.onMouseDragged(event, stage));
        scene.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> helper.onMouseReleased(scene));
    }

    private void onMouseMoved(MouseEvent event, Stage stage) {
        if (stage.isMaximized()) {
            return;
        }
        ResizeDirection dir = detectDirection(event, stage);
        stage.getScene().setCursor(dir.getCursor());
    }

    private void onMousePressed(MouseEvent event, Stage stage) {
        if (stage.isMaximized()) {
            return;
        }
        direction = detectDirection(event, stage);
        if (direction != ResizeDirection.NONE) {
            startX = event.getScreenX();
            startY = event.getScreenY();
            startWidth = stage.getWidth();
            startHeight = stage.getHeight();
            startStageX = stage.getX();
            startStageY = stage.getY();
            event.consume();
        }
    }

    private void onMouseDragged(MouseEvent event, Stage stage) {
        if (direction == ResizeDirection.NONE || stage.isMaximized()) {
            return;
        }

        double dx = event.getScreenX() - startX;
        double dy = event.getScreenY() - startY;
        double minWidth = stage.getMinWidth() > 0 ? stage.getMinWidth() : RESIZE_BORDER * 2;
        double minHeight = stage.getMinHeight() > 0 ? stage.getMinHeight() : RESIZE_BORDER * 2;

        if (direction.isRight()) {
            double newWidth = Math.max(minWidth, startWidth + dx);
            stage.setWidth(newWidth);
        }
        if (direction.isBottom()) {
            double newHeight = Math.max(minHeight, startHeight + dy);
            stage.setHeight(newHeight);
        }
        if (direction.isLeft()) {
            double newWidth = Math.max(minWidth, startWidth - dx);
            if (newWidth > minWidth) {
                stage.setX(startStageX + dx);
            }
            stage.setWidth(newWidth);
        }
        if (direction.isTop()) {
            double newHeight = Math.max(minHeight, startHeight - dy);
            if (newHeight > minHeight) {
                stage.setY(startStageY + dy);
            }
            stage.setHeight(newHeight);
        }

        event.consume();
    }

    private void onMouseReleased(Scene scene) {
        direction = ResizeDirection.NONE;
        scene.setCursor(Cursor.DEFAULT);
    }

    static ResizeDirection detectDirection(MouseEvent event, Stage stage) {
        double x = event.getSceneX();
        double y = event.getSceneY();
        double width = stage.getScene().getWidth();
        double height = stage.getScene().getHeight();

        boolean top = y < RESIZE_BORDER;
        boolean bottom = y > height - RESIZE_BORDER;
        boolean left = x < RESIZE_BORDER;
        boolean right = x > width - RESIZE_BORDER;

        if (top && left) {
            return ResizeDirection.NW;
        }
        if (top && right) {
            return ResizeDirection.NE;
        }
        if (bottom && left) {
            return ResizeDirection.SW;
        }
        if (bottom && right) {
            return ResizeDirection.SE;
        }
        if (top) {
            return ResizeDirection.N;
        }
        if (bottom) {
            return ResizeDirection.S;
        }
        if (left) {
            return ResizeDirection.W;
        }
        if (right) {
            return ResizeDirection.E;
        }
        return ResizeDirection.NONE;
    }

    /**
     * Resize direction constants.
     */
    enum ResizeDirection {
        NONE(Cursor.DEFAULT, false, false, false, false),
        N(Cursor.N_RESIZE, true, false, false, false),
        S(Cursor.S_RESIZE, false, true, false, false),
        W(Cursor.W_RESIZE, false, false, true, false),
        E(Cursor.E_RESIZE, false, false, false, true),
        NW(Cursor.NW_RESIZE, true, false, true, false),
        NE(Cursor.NE_RESIZE, true, false, false, true),
        SW(Cursor.SW_RESIZE, false, true, true, false),
        SE(Cursor.SE_RESIZE, false, true, false, true);

        private final Cursor cursor;
        private final boolean top;
        private final boolean bottom;
        private final boolean left;
        private final boolean right;

        ResizeDirection(Cursor cursor, boolean top, boolean bottom, boolean left, boolean right) {
            this.cursor = cursor;
            this.top = top;
            this.bottom = bottom;
            this.left = left;
            this.right = right;
        }

        Cursor getCursor() {
            return cursor;
        }

        boolean isTop() {
            return top;
        }

        boolean isBottom() {
            return bottom;
        }

        boolean isLeft() {
            return left;
        }

        boolean isRight() {
            return right;
        }
    }
}
