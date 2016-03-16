package org.neolefty.cs143.hybrid_images.img;

import org.neolefty.cs143.hybrid_images.img.boof.*;
import org.neolefty.cs143.hybrid_images.img.pixel.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

/** Create a list of image processors. */
public class ImageProcessors {
    /** All the useful single-image processors.
     *  Creates a new set of objects each time, to isolate parameters. */
    public static List<ImageProcessor> getSingleImageProcessors(ExecutorService x) {
        return Arrays.asList(
                // pixel-by-pixel
                new PixelProcessor(new Dimmer(), x),
                new PixelProcessor(new RotateGBR(), x),
                new PixelProcessor(new InvertBlue(), x),
                new ImagePassThrough(),
                // fft mag & phase
                new Boof8Processor(new Dft32(Dft32.Part.magnitude), x),
                new Boof8Processor(new Dft32(Dft32.Part.phase), x),
                // gaussian blur
                new Boof8Processor(new GaussBlur8(), x),
                // simple frequency filter
                boof8(new SimpleFilterGenerator(), x),
                // butterworth filter
                boof8(new ButterworthGenerator(), x));
    }

    private static ImageProcessor boof8(FilterGenerator gen, ExecutorService threadPool) {
        return new Boof8Processor(new DftFilter32(gen), threadPool);
    }
}
