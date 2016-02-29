package org.neolefty.cs143.hybrid_images.img;

/** A simple dimmer. */
public class Dimmer extends FasterPixelProcessor {
    @Override
    public int process(int pixel) {
        return ((pixel & 0xff) / 2) // blue
                + ((((pixel & 0xff00) >> 8) / 2) << 8) // green
                + ((((pixel & 0xff0000) >> 16) / 2) << 16) // red
                + (pixel & 0xff000000); // alpha
    }
}
