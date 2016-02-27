package org.neolefty.cs143.hybrid_images;

import javafx.application.Application;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.neolefty.cs143.hybrid_images.ui.LoadImageView;
import org.neolefty.cs143.hybrid_images.ui.PersistentScene;
import org.neolefty.cs143.hybrid_images.ui.StrictHBox;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("CS143 #1 Hybrid Images");

        LoadImageView left = new LoadImageView(getClass(), "left");
        LoadImageView right = new LoadImageView(getClass(), "right");
        LoadImageView blueDisplay = new LoadImageView(getClass(), "blue");

        // bind middle image to left image
        InvertBlue blue = new InvertBlue();

        left.bufferedImageProperty().addListener((observable, oldValue, newValue) -> {
            blueDisplay.bufferedImageProperty().setValue(blue.process(newValue));
        });

        // TODO figure out how to make binding work
//        blue.unprocessedImageProperty().bind(left.bufferedImageProperty());
//        blueDisplay.bufferedImageProperty().bind(blue.processedImageProperty());

        HBox inner = new StrictHBox(left, blueDisplay, right);
//        VBox outer = new VBox(inner, mid);

        primaryStage.setScene(new PersistentScene(getClass(), inner, 600, 250));
        primaryStage.show();
    }
}
