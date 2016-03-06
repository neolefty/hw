package org.neolefty.cs143.hybrid_images;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.neolefty.cs143.hybrid_images.img.ImageProcessors;
import org.neolefty.cs143.hybrid_images.img.ImageShrinker;
import org.neolefty.cs143.hybrid_images.ui.ChooseProcessorView;
import org.neolefty.cs143.hybrid_images.ui.HasBufferedImageProperty;
import org.neolefty.cs143.hybrid_images.ui.LoadImageView;
import org.neolefty.cs143.hybrid_images.ui.util.PersistentScene;
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

        ChooseProcessorView filterLeft1a = createFilterChooser(left, "left 1a");
        ChooseProcessorView filterLeft1b = createFilterChooser(filterLeft1a, "left 1b");
        ChooseProcessorView filterLeft2a = createFilterChooser(left, "left 2a");
        ChooseProcessorView filterLeft2b = createFilterChooser(filterLeft2a, "left 2b");

        ChooseProcessorView filterRight1a = createFilterChooser(right, "right 1a");
        ChooseProcessorView filterRight1b = createFilterChooser(filterRight1a, "right 1b");
        ChooseProcessorView filterRight2a = createFilterChooser(right, "right 2a");
        ChooseProcessorView filterRight2b = createFilterChooser(filterRight2a, "right 2b");

        GridPane outer = new StrictGrid();
        outer.setGridLinesVisible(true);

        addToGrid(outer, 4, 3,
                left, null, null, right,
                filterLeft1a, filterLeft2a, filterRight2a, filterRight1a,
                filterLeft1b, filterLeft2b, filterRight2b, filterRight1b);

        PersistentScene scene = new PersistentScene(getClass(), outer, 600, 250);
        scene.setExitOnClose(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private ChooseProcessorView createFilterChooser
            (HasBufferedImageProperty source, String prefsSuffix)
    {
        return new ChooseProcessorView(getClass(), "filter " + prefsSuffix,
                ImageProcessors.list(threadPool), source.bufferedImageProperty(), threadPool);
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
