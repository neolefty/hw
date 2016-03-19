package org.neolefty.cs143.hybrid_images.img.geom;

import org.neolefty.cs143.hybrid_images.img.ImageProcessor;
import org.neolefty.cs143.hybrid_images.util.Stopwatch;

import java.awt.image.BufferedImage;
import java.util.Collection;

/** Pad images so that their dimensions are a power of 2. */
public class PowerOfTwoPadder extends ImageProcessor {
    boolean square = true;

    public PowerOfTwoPadder(boolean square) { this.square = square; }

    @Override
    public BufferedImage process(Collection<BufferedImage> originals) {
        checkImageCount(originals.size(), 1, 1);
        Stopwatch watch = new Stopwatch();
        BufferedImage original = originals.iterator().next();
        BufferedImage result = ImagePadKit.padPowerOfTwo(original, square);
        System.out.println("Pad " + original.getWidth() + "x" + original.getHeight()
                + " to " + result.getWidth() + "x" + result.getHeight() + ": " + watch);
        return result;
    }
}
