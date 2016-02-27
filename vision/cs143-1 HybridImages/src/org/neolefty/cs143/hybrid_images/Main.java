package org.neolefty.cs143.hybrid_images;

import javafx.application.Application;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("CS143 #1 Hybrid Images");

        LoadImageView left = new LoadImageView(getClass(), "left");
        LoadImageView right = new LoadImageView(getClass(), "right");

        HBox outer = new StrictHBox(left, right);

        primaryStage.setScene(new PersistentScene(getClass(), outer, 600, 250));
        primaryStage.show();
    }
}
