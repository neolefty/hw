package org.neolefty.cs143.hybrid_images;

import javafx.application.Application;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.neolefty.cs143.hybrid_images.img.PowerOfTwoPadder;
import org.neolefty.cs143.hybrid_images.img.boof.BlurUInt8;
import org.neolefty.cs143.hybrid_images.img.boof.Boof8Processor;
import org.neolefty.cs143.hybrid_images.img.boof.FftUInt8;
import org.neolefty.cs143.hybrid_images.ui.LoadImageView;
import org.neolefty.cs143.hybrid_images.ui.StackImageView;
import org.neolefty.cs143.hybrid_images.ui.util.PersistentScene;
import org.neolefty.cs143.hybrid_images.ui.util.ProcessedImageView;
import org.neolefty.cs143.hybrid_images.ui.util.StrictGrid;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application {
    private ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//    private ExecutorService threadPool = Executors.newSingleThreadExecutor();

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("CS143 #1 Hybrid Images");

        LoadImageView left = new LoadImageView(getClass(), "left");
        LoadImageView mid = new LoadImageView(getClass(), "mid");
//        LoadImageView right = new LoadImageView(getClass(), "right");

//        StackImageView left2 = new ProcessedImageView(new InvertBlue(), left, threadPool);
//        StackImageView left2 = new ProcessedImageView(new Dimmer(), mid, threadPool);
//        StackImageView mid2 = new ProcessedImageView(new Dimmer(), mid, threadPool);
//        StackImageView right2 = new ProcessedImageView(new Dimmer(), mid, threadPool);
//        StackImageView right2 = new ProcessedImageView(new RotateGBR(), right, threadPool);

        StackImageView right = new ProcessedImageView(new PowerOfTwoPadder(), left, threadPool);
        Boof8Processor fftMag = new Boof8Processor(new FftUInt8(FftUInt8.Part.magnitude), threadPool);
        Boof8Processor fftPhase = new Boof8Processor(new FftUInt8(FftUInt8.Part.phase), threadPool);
        StackImageView left2 = new ProcessedImageView(fftMag, right, threadPool);
        StackImageView mid2 = new ProcessedImageView(fftPhase, right, threadPool);
        Boof8Processor blur = new Boof8Processor(new BlurUInt8(10), threadPool);
        StackImageView right2 = new ProcessedImageView(blur, right, threadPool);

        right.bufferedImageProperty().addListener((observable, oldValue, newValue)
                -> System.out.println("Right is now " + newValue.getWidth() + "x" + newValue.getHeight()));

        // TODO figure out how to make binding work
//        blue.unprocessedImageProperty().bind(left.bufferedImageProperty());
//        blueDisplay.bufferedImageProperty().bind(blue.processedImageProperty());

        GridPane outer = new StrictGrid();
        outer.setGridLinesVisible(true);

        outer.add(left, 0, 0);
        outer.add(mid, 1, 0);
        outer.add(right, 2, 0);

        outer.add(left2, 0, 1);
        outer.add(mid2, 1, 1);
        outer.add(right2, 2, 1);

        PersistentScene scene = new PersistentScene(getClass(), outer, 600, 250);
        scene.setExitOnClose(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
