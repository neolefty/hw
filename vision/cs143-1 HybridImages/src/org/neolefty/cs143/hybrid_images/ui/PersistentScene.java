package org.neolefty.cs143.hybrid_images.ui;

import javafx.scene.Parent;
import javafx.scene.Scene;

import java.util.prefs.Preferences;

/** A scene that remembers its dimensions from run to run. */
public class PersistentScene extends Scene {
    public static final String PREFS_WIDTH = "width", PREFS_HEIGHT = "height", PREFS_X = "x", PREFS_Y = "y";
    public PersistentScene(Class applicationClass, Parent root, double widthDefault, double heightDefault) {
        super(root,
                getFromPrefs(applicationClass, PREFS_WIDTH, widthDefault),
                getFromPrefs(applicationClass, PREFS_HEIGHT, heightDefault));
        windowProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // update location from prefs
                newValue.setX(getFromPrefs(applicationClass, PREFS_X, getWindow().getX()));
                newValue.setY(getFromPrefs(applicationClass, PREFS_Y, getWindow().getY()));

                // save location when window is closed
//                newValue.showingProperty().addListener((obs, oldVal, newVal) -> {
//                    System.out.println("Showing: " + newVal);
//                    savePrefs(applicationClass);
//                });
//                newValue.onHiddenProperty().addListener((obs, oldVal, newVal) -> {
//                    System.out.println("Hidden: " + newValue);
//                    savePrefs(applicationClass);
//                });
                newValue.focusedProperty().addListener((obs, oldVal, newVal) -> {
//                    System.out.println("Focused: " + newValue);
                    savePrefs(applicationClass);
                });
            }
        });
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
}
