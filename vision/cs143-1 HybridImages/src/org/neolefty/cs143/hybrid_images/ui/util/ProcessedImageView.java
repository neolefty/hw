package org.neolefty.cs143.hybrid_images.ui.util;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Tooltip;
import org.neolefty.cs143.hybrid_images.img.ImageProcessor;
import org.neolefty.cs143.hybrid_images.img.boof.Boof8Processor;
import org.neolefty.cs143.hybrid_images.img.pixel.IntToIntFunction;
import org.neolefty.cs143.hybrid_images.img.pixel.ThreadedPixelProcessor;
import org.neolefty.cs143.hybrid_images.ui.HasBufferedImageProperty;
import org.neolefty.cs143.hybrid_images.ui.StackImageView;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Display a processed image. */
public class ProcessedImageView extends StackImageView {
    public ProcessedImageView(ImageProcessor processor,
                              ObjectProperty<BufferedImage> source, ExecutorService executorService)
    {
        if (executorService == null)
            executorService = Executors.newSingleThreadExecutor();
        final ExecutorService ex = executorService;
        Tooltip.install(this, new Tooltip(processor.getName()));
        source.addListener((observable, oldValue, newValue) -> {
//            System.out.println("receiving new image: " + newValue.getWidth() + "x" + newValue.getHeight());
            ex.submit(() -> {
//                Stopwatch watch = new Stopwatch();
                BufferedImage processed = processor.process(newValue);
//                System.out.println("Processed to " + processed.getWidth() + "x" + processed.getHeight() + ": " + watch);
                setImage(processed);
            });
        });
    }

    public ProcessedImageView(ImageProcessor processor,
                              HasBufferedImageProperty source, ExecutorService threadPool) {
        this(processor, source.bufferedImageProperty(), threadPool);
    }

    public ProcessedImageView(IntToIntFunction pixelFunction,
                              HasBufferedImageProperty source, ExecutorService threadPool)
    {
        this(new ThreadedPixelProcessor(pixelFunction, threadPool), source, threadPool);
    }

    public ProcessedImageView(Boof8Processor.Function function,
                              HasBufferedImageProperty source, ExecutorService threadPool)
    {
        this(new Boof8Processor(function, threadPool), source, threadPool);
    }

//    public ProcessedImageView(IntToIntFunction pixelFunction, int n,
//                              HasBufferedImageProperty source, ExecutorService executorService)
//    {
//        this(new ThreadedPixelProcessor(pixelFunction, n), source, executorService);
//    }
}
