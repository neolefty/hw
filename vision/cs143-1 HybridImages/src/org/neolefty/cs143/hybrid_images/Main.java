package org.neolefty.cs143.hybrid_images;

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.neolefty.cs143.hybrid_images.img.ImageProcessors;
import org.neolefty.cs143.hybrid_images.img.geom.ImageShrinker;
import org.neolefty.cs143.hybrid_images.img.two.AddTwoWeighted;
import org.neolefty.cs143.hybrid_images.img.two.ImagePairPixelProcessor;
import org.neolefty.cs143.hybrid_images.img.two.TwoIntegerRGBSplitter;
import org.neolefty.cs143.hybrid_images.ui.ChooseFileImageView;
import org.neolefty.cs143.hybrid_images.ui.ChooseProcessorView;
import org.neolefty.cs143.hybrid_images.ui.HasBufferedImageProperty;
import org.neolefty.cs143.hybrid_images.ui.ProcessedImageView;
import org.neolefty.cs143.hybrid_images.ui.util.PersistentScene;
import org.neolefty.cs143.hybrid_images.ui.util.PrefStuff;
import org.neolefty.cs143.hybrid_images.ui.util.ProcessedBI;
import org.neolefty.cs143.hybrid_images.ui.util.StrictGrid;
import org.neolefty.cs143.hybrid_images.util.CancellingExecutor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application {
    /** Low-level image-processing Runnables can't be cancelled because they separate the channels and
     *  wait for all the pieces to complete. If one doesn't, the image processor will wait forever.
     *  So just let them run to completion. */
    private ExecutorService lowThreads;
    /** High-level ImageProcessor runnables can be cancelled though, because nothing depends on them
     *  and if they never run, it just means that the UI doesn't get updated. */
    private CancellingExecutor highExec;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        lowThreads = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        int numHighThreads = (Runtime.getRuntime().availableProcessors() / 3) + 1;
        // Want a low number of threads, since we don't want a ton in flight at a time,
        // since they'd be mostly waiting for low-level threads.
        // But want enough to take advantage of all the processors.
        // Divide by 3 because the most intensive jobs get one task for each color channel
        ExecutorService highThreads = Executors.newFixedThreadPool(numHighThreads);
        highExec = new CancellingExecutor(highThreads);

        // if we allow interruption, it actually hurts performance, since the low-level processes
        // get interrupted and immediately a new one is started
        highExec.setMayInterrupt(false);

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
        ChooseFileImageView left = new ChooseFileImageView(getClass(), "left");
        ChooseFileImageView right = new ChooseFileImageView(getClass(), "right");
        left.setPreprocessor(new ImageShrinker());
        right.setPreprocessor(new ImageShrinker());

        ChooseProcessorView filterLeft1a = createFilterChooser(left, "left 1a");
        ChooseProcessorView filterLeft1b = createFilterChooser(filterLeft1a, "left 1b");
        ChooseProcessorView filterLeft1c = createFilterChooser(filterLeft1b, "left 1c");
        ChooseProcessorView filterLeft2a = createFilterChooser(left, "left 2a");
        ChooseProcessorView filterLeft2b = createFilterChooser(filterLeft2a, "left 2b");
        ChooseProcessorView filterLeft2c = createFilterChooser(filterLeft2b, "left 2c");

        ChooseProcessorView filterRight1a = createFilterChooser(right, "right 1a");
        ChooseProcessorView filterRight1b = createFilterChooser(filterRight1a, "right 1b");
        ChooseProcessorView filterRight1c = createFilterChooser(filterRight1b, "right 1c");
        ChooseProcessorView filterRight2a = createFilterChooser(right, "right 2a");
        ChooseProcessorView filterRight2b = createFilterChooser(filterRight2a, "right 2b");
        ChooseProcessorView filterRight2c = createFilterChooser(filterRight2b, "right 2c");

        List<ObjectProperty<ProcessedBI>>
                sumLeftSources = Arrays.asList(filterLeft1c.imageProperty(), filterRight2c.imageProperty()),
                sumRightSources = Arrays.asList(filterLeft2c.imageProperty(), filterRight1c.imageProperty());
        ProcessedImageView sumLeft = new ProcessedImageView(getPref("sum").createChild("left"),
                new ImagePairPixelProcessor(new TwoIntegerRGBSplitter(new AddTwoWeighted()), lowThreads),
                sumLeftSources, highExec);
        ProcessedImageView sumRight = new ProcessedImageView(getPref("sum").createChild("right"),
                new ImagePairPixelProcessor(new TwoIntegerRGBSplitter(new AddTwoWeighted()), lowThreads),
                sumRightSources, highExec);

        GridPane outer = new StrictGrid();
        outer.setGridLinesVisible(true);

        addToGrid(outer, 4, 4,
                left, sumLeft, sumRight, right,
                filterLeft1a, filterLeft2a, filterRight2a, filterRight1a,
                filterLeft1b, filterLeft2b, filterRight2b, filterRight1b,
                filterLeft1c, filterLeft2c, filterRight2c, filterRight1c);

        PersistentScene scene = new PersistentScene(getClass(), outer, 600, 250);
        scene.setExitOnClose(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private PrefStuff getPref(String key) {
        return new PrefStuff(getClass(), key);
    }

    private ChooseProcessorView createFilterChooser
            (HasBufferedImageProperty source, String prefsSuffix)
    {
        return new ChooseProcessorView(
                getPref("filter").createChild(prefsSuffix),
                ImageProcessors.getSingleImageProcessors(lowThreads),
                Collections.singleton(source.imageProperty()),
                highExec);
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
