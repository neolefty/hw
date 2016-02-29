package org.neolefty.cs143.hybrid_images.img;

/** Rotate R,G,B. */
public class RotateGBR extends FasterPixelProcessor {
    @Override
    public int process(int p) {
        return ((p & 0xffff) << 8) // blue & green
                + ((p & 0xff0000) >> 16) // red
                + (p & 0xff000000); // alpha
    }
}
