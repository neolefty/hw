package org.neolefty.cs143.hybrid_images;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private static final String PREFS_FILE_HISTORY = "file_history";

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("CS143 #1 Hybrid Images");
        primaryStage.setScene(new Scene(new LoadImageView(getClass(), PREFS_FILE_HISTORY), 300, 250));
        primaryStage.show();
    }
}
