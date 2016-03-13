package org.neolefty.cs143.hybrid_images.img;

import org.neolefty.cs143.hybrid_images.img.boof.*;
import org.neolefty.cs143.hybrid_images.img.pixel.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

/** Create a list of image processors. */
public class ImageProcessors {
    public static List<ImageProcessor> getList(ExecutorService x) {
        return Arrays.asList(
                // pixel-by-pixel
                new PixelProcessor(new Dimmer(), x),
                new PixelProcessor(new RotateGBR(), x),
                new PixelProcessor(new InvertBlue(), x),
                new ImagePassThrough(),
                // fft mag & phase
                new Boof8Processor(new FftUInt8(FftUInt8.Part.magnitude), x),
                new Boof8Processor(new FftUInt8(FftUInt8.Part.phase), x),
                // gaussian blur
                new Boof8Processor(new GaussBlur8(), x),
                // simple frequency filter
                boof8(new SimpleFilterGenerator(), x),
                // butterworth filter
                boof8(new ButterworthGenerator(), x));
    }

    private static ImageProcessor boof8(FilterGenerator gen, ExecutorService threadPool) {
        return new Boof8Processor(new DftFilter(gen), threadPool);
    }
}
