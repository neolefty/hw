package org.neolefty.cs143.hybrid_images.ui.util;

import javafx.beans.property.ObjectProperty;
import org.neolefty.cs143.hybrid_images.img.ImageProcessor;
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
        source.addListener((observable, oldValue, newValue) ->
                ex.submit(() -> setImage(processor.process(newValue))));
    }

    public ProcessedImageView(ImageProcessor processor,
                              HasBufferedImageProperty source, ExecutorService executorService) {
        this(processor, source.bufferedImageProperty(), executorService);
    }

    public ProcessedImageView(IntToIntFunction pixelFunction,
                              HasBufferedImageProperty source, ExecutorService executorService)
    {
        this(new ThreadedPixelProcessor(pixelFunction), source, executorService);
    }

//    public ProcessedImageView(IntToIntFunction pixelFunction, int n,
//                              HasBufferedImageProperty source, ExecutorService executorService)
//    {
//        this(new ThreadedPixelProcessor(pixelFunction, n), source, executorService);
//    }
}
