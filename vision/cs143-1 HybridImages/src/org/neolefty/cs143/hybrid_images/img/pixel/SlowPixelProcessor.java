package org.neolefty.cs143.hybrid_images.img.pixel;

import org.neolefty.cs143.hybrid_images.img.ImageProcessor;
import org.neolefty.cs143.hybrid_images.util.Stopwatch;

import java.awt.image.BufferedImage;
import java.util.Collection;

/** Process one pixel at a time, using image pixel accessor methods. */
public abstract class SlowPixelProcessor extends ImageProcessor {
    private IntToIntFunction pixelFunction;

    public SlowPixelProcessor(IntToIntFunction pixelFunction) {
        this.pixelFunction = pixelFunction;
        addInput();
    }

    public IntToIntFunction getPixelFunction() {
        return pixelFunction;
    }

    @Override
    public String toString() {
        return pixelFunction.toString();
    }

    @Override
    public BufferedImage process(Collection<BufferedImage> originals) {
        checkImageCount(originals.size(), 1, 1);
        BufferedImage original = originals.iterator().next();
        return processSingle(original);
    }

    public BufferedImage processSingle(BufferedImage original) {
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
}
