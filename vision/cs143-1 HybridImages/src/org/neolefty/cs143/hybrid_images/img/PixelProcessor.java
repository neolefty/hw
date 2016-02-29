package org.neolefty.cs143.hybrid_images.img;

import org.neolefty.cs143.hybrid_images.ProcessedImage;
import org.neolefty.cs143.hybrid_images.util.Stopwatch;

import java.awt.image.BufferedImage;

/** Process one pixel at a time. */
public abstract class PixelProcessor extends ProcessedImage {
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
                    result.setRGB(x, y, process(original.getRGB(x, y)));

            System.out.println(getName() + ": " +(w * h) + " pixels "
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

    public String getName() { return getClass().getSimpleName(); }

    public abstract int process(int pixel);
}
