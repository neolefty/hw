package org.neolefty.cs143.hybrid_images;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.neolefty.cs143.hybrid_images.img.ImageShrinker;
import org.neolefty.cs143.hybrid_images.img.boof.Boof8Processor;
import org.neolefty.cs143.hybrid_images.img.boof.GaussBlur8;
import org.neolefty.cs143.hybrid_images.img.boof.LowPass8;
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
        LoadImageView right = new LoadImageView(getClass(), "right");
        left.setPreprocessor(new ImageShrinker());
        right.setPreprocessor(new ImageShrinker());

//        StackImageView viewPadded = new ProcessedImageView(new PowerOfTwoPadder(false), left, threadPool);
        StackImageView leftBlur = new ProcessedImageView(new GaussBlur8(10), left, threadPool);
        StackImageView rightBlur = new ProcessedImageView(new GaussBlur8(10), right, threadPool);

//        Boof8Processor fftPassThrough = new Boof8Processor(new LowPassUInt8(10, 5), threadPool);
//        StackImageView far = new ProcessedImageView(fftPassThrough, viewPadded, threadPool);

        Boof8Processor lowPass = new Boof8Processor(new LowPass8(0.2), threadPool);
        Boof8Processor lowPass11 = new Boof8Processor(new LowPass8(0.2, 11), threadPool);
        Boof8Processor lowPass1Point5 = new Boof8Processor(new LowPass8(0.2, 1.5), threadPool);
        Boof8Processor lowPass1 = new Boof8Processor(new LowPass8(0.2, 1), threadPool);

        StackImageView leftLow = new ProcessedImageView(lowPass, left, threadPool);
        StackImageView rightLow = new ProcessedImageView(lowPass, right, threadPool);
        StackImageView leftLow1 = new ProcessedImageView(lowPass1, left, threadPool);
        StackImageView rightLow1 = new ProcessedImageView(lowPass1, right, threadPool);

//        Boof8Processor fftMag = new Boof8Processor(new FftUInt8(FftUInt8.Part.magnitude), threadPool);
//        Boof8Processor fftPhase = new Boof8Processor(new FftUInt8(FftUInt8.Part.phase), threadPool);
//        StackImageView viewLowPadded = new ProcessedImageView(fftMag, right2, threadPool);
//        StackImageView mid2 = new ProcessedImageView(fftPhase, right2, threadPool);

        GridPane outer = new StrictGrid();
        outer.setGridLinesVisible(true);

        addToGrid(outer, 4, 2,
                left, leftBlur, rightBlur, right,
                leftLow1, leftLow, rightLow, rightLow1);

        PersistentScene scene = new PersistentScene(getClass(), outer, 600, 250);
        scene.setExitOnClose(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addToGrid(GridPane outer, int w, int h, Node ... nodes) {
        int i = 0;
        for (int y = 0; y < h; ++y)
            for (int x = 0; x < w; ++x)
                if (i < nodes.length) {
                    Node node = nodes[i++];
                    if (node != null)
                        outer.add(node, x, y);
                }
    }
}
