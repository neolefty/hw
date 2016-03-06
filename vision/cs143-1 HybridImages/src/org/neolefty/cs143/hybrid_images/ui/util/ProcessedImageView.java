package org.neolefty.cs143.hybrid_images.ui.util;

import javafx.beans.property.ObjectProperty;
import javafx.embed.swing.SwingNode;
import javafx.scene.control.Tooltip;
import org.neolefty.cs143.hybrid_images.img.ImageProcessor;
import org.neolefty.cs143.hybrid_images.img.boof.Boof8Processor;
import org.neolefty.cs143.hybrid_images.ui.HasBufferedImageProperty;
import org.neolefty.cs143.hybrid_images.ui.HasDebugWindow;
import org.neolefty.cs143.hybrid_images.ui.StackImageView;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Display a processed image. */
public class ProcessedImageView extends StackImageView {
    private ImageProcessor processor;
    private BufferedImage unprocessedImage;
//    private ObjectProperty<ImageProcessor> processorProperty = new SimpleObjectProperty<>();

    public ProcessedImageView(ImageProcessor processor,
                              ObjectProperty<BufferedImage> source, ExecutorService executorService)
    {
        if (executorService == null)
            executorService = Executors.newSingleThreadExecutor();
        final ExecutorService ex = executorService;
        setImageProcessor(processor);
        source.addListener((observable, oldValue, newValue) -> {
            ex.submit(() -> setUnprocessedImage(newValue));
        });
    }

    public ProcessedImageView(ImageProcessor processor,
                              HasBufferedImageProperty source, ExecutorService threadPool) {
        this(processor, source.bufferedImageProperty(), threadPool);
    }

    public ProcessedImageView(Boof8Processor.Function function,
                              HasBufferedImageProperty source, ExecutorService threadPool)
    {
        this(new Boof8Processor(function, threadPool), source, threadPool);
    }

    public void setImageProcessor(ImageProcessor processor) {
        if (processor != this.processor) {

            // show debug info in tooltip
            Tooltip tip = new Tooltip(processor.toString());
            if (processor instanceof HasDebugWindow) {
                SwingNode node = new SwingNode();
                ((HasDebugWindow) processor).debugWindowProperty().addListener((observable, oldValue, newValue) -> {
                    node.setContent(newValue);
                    tip.graphicProperty().setValue(node);
                });
            }
            Tooltip.install(this, tip);

            this.processor = processor;

            // reprocess the current image
            if (unprocessedImage != null)
                setImage(getImageProcessor().process(unprocessedImage));
        }
    }

    public void setUnprocessedImage(BufferedImage image) {
        if (image != this.unprocessedImage) {
            // System.out.println("receiving new image: " + newValue.getWidth() + "x" + newValue.getHeight());
            this.unprocessedImage = image;
            // Stopwatch watch = new Stopwatch();
            if (image != null)
                setImage(getImageProcessor().process(image));
            // System.out.println("Processed to " + processed.getWidth() + "x" + processed.getHeight() + ": " + watch);
        }
    }

    public ImageProcessor getImageProcessor() { return processor; }

//    public ProcessedImageView(IntToIntFunction pixelFunction,
//                              HasBufferedImageProperty source, ExecutorService threadPool)
//    {
//        this(new ThreadedPixelProcessor(pixelFunction, threadPool), source, threadPool);
//    }

//    public ProcessedImageView(IntToIntFunction pixelFunction, int n,
//                              HasBufferedImageProperty source, ExecutorService executorService)
//    {
//        this(new ThreadedPixelProcessor(pixelFunction, n), source, executorService);
//    }
}
