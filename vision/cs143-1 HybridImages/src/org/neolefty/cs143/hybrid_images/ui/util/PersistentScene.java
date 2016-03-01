package org.neolefty.cs143.hybrid_images.ui.util;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.util.prefs.Preferences;

/** A scene that remembers its dimensions from run to run. */
public class PersistentScene extends Scene {
    private boolean exitOnClose = false;

    public static final String PREFS_WIDTH = "width3", PREFS_HEIGHT = "height3", PREFS_X = "x3", PREFS_Y = "y3";
    public PersistentScene(Class applicationClass, Parent root, double widthDefault, double heightDefault) {
        super(root,
                getFromPrefs(applicationClass, PREFS_WIDTH, widthDefault, 100),
                getFromPrefs(applicationClass, PREFS_HEIGHT, heightDefault, 100));
        windowProperty().addListener((observable, oldValue, newWindow) -> {
            if (newWindow != null) {
                // update location from prefs
                newWindow.setX(getFromPrefs(applicationClass, PREFS_X, getWindow().getX()));
                newWindow.setY(getFromPrefs(applicationClass, PREFS_Y, getWindow().getY()));

                // save location when window is closed
//                newWindow.showingProperty().addListener((obs, oldVal, newVal) -> {
//                    System.out.println("Showing: " + newVal);
//                    savePrefs(applicationClass);
//                });
//                newWindow.onHiddenProperty().addListener((obs, oldVal, newVal) -> {
//                    System.out.println("Hidden: " + newWindow);
//                    savePrefs(applicationClass);
//                });
                newWindow.focusedProperty().addListener((obs, oldVal, newVal) -> {
//                    System.out.println("Focused: " + newWindow);
                    savePrefs(applicationClass);
                });
                if (exitOnClose)
                    newWindow.setOnCloseRequest(event -> exit());
            }
        });
    }

    private void exit() {
        new Thread() {
            @Override
            public void run() {
                // kludge: let other things finish, like saving window position
                try { sleep(100); } catch (InterruptedException ignored) { }
                Platform.exit();
                System.exit(0);
            }
        }.start();
    }

    private static double getFromPrefs(Class prefsClass, String key, double valueDefault, double min) {
        double result = getFromPrefs(prefsClass, key, valueDefault);
        if (result < min)
            result = min;
        return result;
    }

    private static double getFromPrefs(Class prefsClass, String key, double valueDefault) {
        return Preferences.userNodeForPackage(prefsClass).getDouble(key, valueDefault);
    }

    private void savePrefs(Class prefsClass) {
        Preferences prefs = Preferences.userNodeForPackage(prefsClass);
        prefs.putDouble(PREFS_WIDTH, getWidth());
        prefs.putDouble(PREFS_HEIGHT, getHeight());
        prefs.putDouble(PREFS_X, getWindow().getX());
        prefs.putDouble(PREFS_Y, getWindow().getY());
    }

    public void setExitOnClose(boolean exit) {
        this.exitOnClose = exit;
        if (getWindow() != null) {
            if (exitOnClose)
                getWindow().setOnCloseRequest(event -> exit());
//            else
//                getWindow().setOnCloseRequest(null);
        }
    }
}
