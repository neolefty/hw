package org.neolefty.cs143.hybrid_images.img.boof;

import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.MultiSpectral;
import org.neolefty.cs143.hybrid_images.img.ImageProcessor;
import org.neolefty.cs143.hybrid_images.util.Stopwatch;

import java.awt.image.BufferedImage;

/** Convert to BoofCV image and do something. */
public class BoofUInt8ImageProcessor extends ImageProcessor {
    private ImageUInt8Function function;

    public BoofUInt8ImageProcessor(ImageUInt8Function function) {
        this.function = function;
    }

    @Override
    public BufferedImage process(BufferedImage original) {
        Stopwatch watch = new Stopwatch();
        int w = original.getWidth(), h = original.getHeight();

        MultiSpectral<ImageUInt8> boofImage = new MultiSpectral<>(ImageUInt8.class, w, h, 3);
        watch.mark("create boof");

        ConvertBufferedImage.convertFrom(original, boofImage, true);
        watch.mark("convert to boof");

        MultiSpectral<ImageUInt8> blurred = new MultiSpectral<>(ImageUInt8.class, w, h, 3);
        function.apply(boofImage.getBand(0), blurred.getBand(0), 0);
        watch.mark("plane-1");
        function.apply(boofImage.getBand(1), blurred.getBand(1), 1);
        watch.mark("plane-2");
        function.apply(boofImage.getBand(2), blurred.getBand(2), 2);
        watch.mark("plane-3");

        BufferedImage result = ConvertBufferedImage.convertTo(blurred, null, true);
        watch.mark("convert back");
        double nsPerPixel = (watch.getElapsed() * 1000000) / (w * h);
        System.out.println(getName() + " - " + watch + " - " + nsPerPixel + " ns per pixel" + " - " + getName());
        return result;
    }

    @Override
    public String getName() {
        return "BoofCV " + function.getClass().getSimpleName();
    }

    public interface ImageUInt8Function {
        /** Process a color plane of the image. Put the result into pre-allocated <tt>out</tt>. */
        void apply(ImageUInt8 in, ImageUInt8 out, int index);
    }
}
