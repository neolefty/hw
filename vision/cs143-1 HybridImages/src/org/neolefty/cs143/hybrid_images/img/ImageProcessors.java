package org.neolefty.cs143.hybrid_images.img;

import org.neolefty.cs143.hybrid_images.img.boof.*;
import org.neolefty.cs143.hybrid_images.img.pixel.*;
import org.neolefty.cs143.hybrid_images.img.two.AddTwoWeighted;
import org.neolefty.cs143.hybrid_images.img.two.ImagePairPixelProcessor;

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
                new BoofProcessor(new DftVisualize(DftVisualize.Part.magnitude), x),
                new BoofProcessor(new DftVisualize(DftVisualize.Part.phase), x),
                // Laplacian sharpen
                kernel(new LaplaceSharpenKernel(), x),
                kernel(new RandomKernel(), x),
                // Gaussian blur
                new BoofProcessor(new GaussBlur8(), x),
                // simple frequency filter
                boof8(new SimpleFilterGenerator(), x),
                // butterworth filter
                boof8(new ButterworthGenerator(), x));
    }

    public static List<ImageProcessor> getTwoImageProcessors(ExecutorService x) {
        return Arrays.asList(
                new ImagePairPixelProcessor(new AddTwoWeighted(), x)
        );
    }

    private static ImageProcessor kernel(KernelGenerator kernelGenerator, ExecutorService threadPool) {
        return new BoofProcessor(new ConvolutionFunction32(kernelGenerator), threadPool);
    }

    private static ImageProcessor boof8(FilterGenerator gen, ExecutorService threadPool) {
        return new BoofProcessor(new DftFilter(gen), threadPool);
    }
}
