package org.neolefty.cs143.hybrid_images.img.pixel;

/** Rotate R,G,B. */
public class RotateGBR implements IntToIntFunction {
    @Override
    public int apply(int p) {
        return ((p & 0xffff) << 8) // blue & green
                + ((p & 0xff0000) >> 16) // red
                + (p & 0xff000000); // alpha
    }
}
