package org.neolefty.cs143.hybrid_images.img.pixel;

/** Process image: invert all the blue values. For testing ... */
public class InvertBlue implements IntToIntFunction {
    @Override
    public int apply(int pixel) {
        return (pixel & 0xffffff00) + (0xff - pixel & 0x000000ff);
    }

    @Override
    public String toString() {
        return "Invert blue channel";
    }
}
