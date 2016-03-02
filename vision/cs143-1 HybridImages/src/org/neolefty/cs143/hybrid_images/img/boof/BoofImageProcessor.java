package org.neolefty.cs143.hybrid_images.img.boof;

import boofcv.alg.filter.blur.BlurImageOps;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.MultiSpectral;
import org.neolefty.cs143.hybrid_images.img.ImageProcessor;
import org.neolefty.cs143.hybrid_images.util.Stopwatch;

import java.awt.image.BufferedImage;

/** Convert to BoofCV image and do something. */
public class BoofImageProcessor extends ImageProcessor {
    @Override
    public BufferedImage process(BufferedImage original) {
        Stopwatch watch = new Stopwatch();
        int w = original.getWidth(), h = original.getHeight();

        MultiSpectral<ImageUInt8> boofImage = new MultiSpectral<>(ImageUInt8.class, w, h, 3);
        watch.mark("create boof");

        ConvertBufferedImage.convertFrom(original, boofImage, true);
        watch.mark("convert to boof");

        MultiSpectral<ImageUInt8> blurred = new MultiSpectral<>(ImageUInt8.class, w, h, 3);
        BlurImageOps.gaussian(boofImage.getBand(0), blurred.getBand(0), -1, 10, null);
        watch.mark("blur");
        BlurImageOps.gaussian(boofImage.getBand(1), blurred.getBand(1), -1, 10, null);
        watch.mark("blur");
        BlurImageOps.gaussian(boofImage.getBand(2), blurred.getBand(2), -1, 10, null);
        watch.mark("blur");

        BufferedImage result = ConvertBufferedImage.convertTo(blurred, null, true);
        watch.mark("convert back");
        System.out.println(watch);
        return result;
    }
}
