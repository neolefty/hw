package org.neolefty.cs143.hybrid_images.img.pixel;

import org.neolefty.cs143.hybrid_images.img.ImageProcessor;
import org.neolefty.cs143.hybrid_images.util.Stopwatch;

import java.awt.image.BufferedImage;

/** Process one pixel at a time, using image pixel accessor methods. */
public abstract class SlowPixelProcessor extends ImageProcessor {
    private IntToIntFunction pixelFunction;

    public SlowPixelProcessor(IntToIntFunction pixelFunction) {
        this.pixelFunction = pixelFunction;
    }

    public IntToIntFunction getPixelFunction() {
        return pixelFunction;
    }

    @Override
    public String toString() {
        return pixelFunction.toString();
    }

    @Override
    public BufferedImage process(BufferedImage original) {
        if (original == null)
            return null;
        else {
            Stopwatch watch = new Stopwatch();
            int w = original.getWidth(), h = original.getHeight();

            BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < h; ++y)
                for (int x = 0; x < w; ++x)
                    result.setRGB(x, y, pixelFunction.apply(original.getRGB(x, y)));

            System.out.println(toString() + ": " +(w * h) + " pixels "
                    + watch + " (" + (1000000 * watch.getElapsed() / (w * h)) + " ns per pixel)");
            return result;
        }
    }

    // This made it take a few minutes instead of a second for a large photo
//    @Override
//    public BufferedImage process(BufferedImage original) {
//        long start = System.currentTimeMillis();
//        System.out.print("rotating");
//        int w = original.getWidth(), h = original.getHeight();
//        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
//        int[] from = new int[w], to = new int[w];
//        for (int y = 0; y < h; ++y) {
//            original.getRGB(0, y, w, 1, from, 0, w);
//            for (int x = 0; x < w; ++x) {
//                int p = from[x];
//                to[x] = ((p & 0xffff) << 8) + ((p & 0xff0000) >> 16);
//                result.setRGB(0, y, w, 1, to, 0, w);
//            }
//            System.out.print(".");
//        }
//        System.out.println((System.currentTimeMillis() - start) + " ms");
//        return result;
//    }
}
