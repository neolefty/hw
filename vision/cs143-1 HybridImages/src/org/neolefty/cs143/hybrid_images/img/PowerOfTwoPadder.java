package org.neolefty.cs143.hybrid_images.img;

import org.neolefty.cs143.hybrid_images.util.Stopwatch;

import java.awt.image.BufferedImage;

/** Pad images so that their dimensions are a power of 2. */
public class PowerOfTwoPadder extends ImageProcessor {
    boolean square = true;

    public PowerOfTwoPadder(boolean square) { this.square = square; }

    @Override
    public BufferedImage process(BufferedImage original) {
        Stopwatch watch = new Stopwatch();
        BufferedImage result = PowerOfTwo.pad(original, square);
        System.out.println("Pad " + original.getWidth() + "x" + original.getHeight()
                + " to " + result.getWidth() + "x" + result.getHeight() + ": " + watch);
        return result;
    }
}
