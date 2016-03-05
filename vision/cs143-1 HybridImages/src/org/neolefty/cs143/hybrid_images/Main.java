package org.neolefty.cs143.hybrid_images;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.neolefty.cs143.hybrid_images.img.ButterworthGenerator;
import org.neolefty.cs143.hybrid_images.img.FilterGenerator;
import org.neolefty.cs143.hybrid_images.img.ImageShrinker;
import org.neolefty.cs143.hybrid_images.img.SimpleFilterGenerator;
import org.neolefty.cs143.hybrid_images.img.boof.Boof8Processor;
import org.neolefty.cs143.hybrid_images.img.boof.DftFilter;
import org.neolefty.cs143.hybrid_images.img.boof.GaussBlur8;
import org.neolefty.cs143.hybrid_images.ui.LoadImageView;
import org.neolefty.cs143.hybrid_images.ui.StackImageView;
import org.neolefty.cs143.hybrid_images.ui.util.PersistentScene;
import org.neolefty.cs143.hybrid_images.ui.util.ProcessedImageView;
import org.neolefty.cs143.hybrid_images.ui.util.StrictGrid;
import org.neolefty.cs143.hybrid_images.util.ThrowablePrintingExecutorService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application {
    private ExecutorService threadPool;
//    private ExecutorService threadPool = Executors.newSingleThreadExecutor();

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        ExecutorService procThreads = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        threadPool = new ThrowablePrintingExecutorService(procThreads);

        primaryStage.setTitle("CS143 #1 Hybrid Images");

        try {
            reallyStart(primaryStage);
        } catch(Throwable t) {
            t.printStackTrace();
            t.fillInStackTrace();
            throw t;
        }
    }

    private void reallyStart(Stage primaryStage) {
        LoadImageView left = new LoadImageView(getClass(), "left");
        LoadImageView right = new LoadImageView(getClass(), "right");
        left.setPreprocessor(new ImageShrinker());
        right.setPreprocessor(new ImageShrinker());

//        StackImageView viewPadded = new ProcessedImageView(new PowerOfTwoPadder(false), left, threadPool);
        StackImageView leftBlur = new ProcessedImageView(new GaussBlur8(10), left, threadPool);
        StackImageView rightBlur = new ProcessedImageView(new GaussBlur8(10), right, threadPool);

//        Boof8Processor fftPassThrough = new Boof8Processor(new LowPassUInt8(10, 5), threadPool);
//        StackImageView far = new ProcessedImageView(fftPassThrough, viewPadded, threadPool);

        FilterGenerator lowSimpleGen = new SimpleFilterGenerator(0.2, FilterGenerator.Type.lowPass);
        FilterGenerator lowGaussGen = new SimpleFilterGenerator(0.2, 1, FilterGenerator.Type.lowPass);
        FilterGenerator highSimpleGen = new SimpleFilterGenerator(0.2, FilterGenerator.Type.highPass);
        FilterGenerator highGaussGen = new SimpleFilterGenerator(0.2, 1, FilterGenerator.Type.highPass);

        Boof8Processor lowSimple = new Boof8Processor(new DftFilter(lowSimpleGen), threadPool);
        Boof8Processor lowGauss = new Boof8Processor(new DftFilter(lowGaussGen), threadPool);
        Boof8Processor highSimple = new Boof8Processor(new DftFilter(highSimpleGen), threadPool);
        Boof8Processor highGauss = new Boof8Processor(new DftFilter(highGaussGen), threadPool);

        StackImageView leftLow = new ProcessedImageView(lowSimple, left, threadPool);
        StackImageView rightLow = new ProcessedImageView(lowSimple, right, threadPool);
        StackImageView leftLow1 = new ProcessedImageView(lowGauss, left, threadPool);
        StackImageView rightLow1 = new ProcessedImageView(lowGauss, right, threadPool);
        StackImageView leftHigh = new ProcessedImageView(highSimple, left, threadPool);
        StackImageView rightHigh = new ProcessedImageView(highSimple, right, threadPool);
        StackImageView leftHigh1 = new ProcessedImageView(highGauss, left, threadPool);
        StackImageView rightHigh1 = new ProcessedImageView(highGauss, right, threadPool);

        FilterGenerator lowButter1Gen = new ButterworthGenerator(0.2, FilterGenerator.Type.lowPass, 1);
        FilterGenerator lowButter2Gen = new ButterworthGenerator(0.2, FilterGenerator.Type.lowPass, 2);
        FilterGenerator highButter1Gen = new ButterworthGenerator(0.2, FilterGenerator.Type.highPass, 1);
        FilterGenerator highButter2Gen = new ButterworthGenerator(0.2, FilterGenerator.Type.highPass, 2);
        Boof8Processor lowButter1 = new Boof8Processor(new DftFilter(lowButter1Gen), threadPool);
        Boof8Processor lowButter2 = new Boof8Processor(new DftFilter(lowButter2Gen), threadPool);
        Boof8Processor highButter1 = new Boof8Processor(new DftFilter(highButter1Gen), threadPool);
        Boof8Processor highButter2 = new Boof8Processor(new DftFilter(highButter2Gen), threadPool);

        StackImageView leftButter1Low = new ProcessedImageView(lowButter1, left, threadPool);
        StackImageView leftButter2Low = new ProcessedImageView(lowButter2, left, threadPool);
        StackImageView rightButter1Low = new ProcessedImageView(lowButter1, right, threadPool);
        StackImageView rightButter2Low = new ProcessedImageView(lowButter2, right, threadPool);

        GridPane outer = new StrictGrid();
        outer.setGridLinesVisible(true);

        addToGrid(outer, 4, 4,
                left, leftBlur, rightBlur, right,
                leftLow1, leftLow, rightLow, rightLow1,
                leftHigh1, leftHigh, rightHigh, rightHigh1,
                leftButter1Low, leftButter2Low, rightButter2Low, rightButter1Low);

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
