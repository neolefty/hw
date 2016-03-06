package org.neolefty.cs143.hybrid_images.img;

import org.neolefty.cs143.hybrid_images.img.boof.Boof8Processor;
import org.neolefty.cs143.hybrid_images.img.boof.DftFilter;
import org.neolefty.cs143.hybrid_images.img.boof.GaussBlur8;
import org.neolefty.cs143.hybrid_images.img.pixel.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

/** Create a list of image processors. */
public class ImageProcessors {
    public static List<ImageProcessor> list(ExecutorService x) {
        return Arrays.asList(
                // pixel
                new PixelProcessor(new Dimmer(), x),
                new PixelProcessor(new RotateGBR(), x),
                new PixelProcessor(new InvertBlue(), x),
                new ImagePassThrough(),
                // gaussian blur
                new Boof8Processor(new GaussBlur8(4), x),
                new Boof8Processor(new GaussBlur8(10), x),
                new Boof8Processor(new GaussBlur8(32), x),
                // simple low- and high-pass
                boof8(new SimpleFilterGenerator(10, FilterGenerator.Type.lowPass), x),
                boof8(new SimpleFilterGenerator(0.2, FilterGenerator.Type.lowPass), x),
                boof8(new SimpleFilterGenerator(0.2, 1, FilterGenerator.Type.lowPass), x),
                boof8(new SimpleFilterGenerator(0.2, FilterGenerator.Type.highPass), x),
                boof8(new SimpleFilterGenerator(0.2, 1, FilterGenerator.Type.highPass), x),
                // butterworth low- and high-pass
                boof8(new ButterworthGenerator(10, FilterGenerator.Type.lowPass, 1), x), // passthrough
                boof8(new ButterworthGenerator(0.2, FilterGenerator.Type.lowPass, 1), x),
                boof8(new ButterworthGenerator(0.2, FilterGenerator.Type.lowPass, 2), x),
                boof8(new ButterworthGenerator(0.2, FilterGenerator.Type.lowPass, 4), x),
                boof8(new ButterworthGenerator(0.2, FilterGenerator.Type.lowPass, 6), x),
                boof8(new ButterworthGenerator(0.2, FilterGenerator.Type.lowPass, 8), x),
                boof8(new ButterworthGenerator(0.2, FilterGenerator.Type.lowPass, 10), x),
                boof8(new ButterworthGenerator(0.2, FilterGenerator.Type.highPass, 1), x),
                boof8(new ButterworthGenerator(0.2, FilterGenerator.Type.highPass, 2), x),
                boof8(new ButterworthGenerator(0.2, FilterGenerator.Type.highPass, 4), x),
                boof8(new ButterworthGenerator(0.2, FilterGenerator.Type.highPass, 6), x),
                boof8(new ButterworthGenerator(0.2, FilterGenerator.Type.highPass, 8), x),
                boof8(new ButterworthGenerator(0.2, FilterGenerator.Type.highPass, 10), x));
    }

    private static ImageProcessor boof8(FilterGenerator gen, ExecutorService threadPool) {
        return new Boof8Processor(new DftFilter(gen), threadPool);
    }
}
