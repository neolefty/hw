package org.neolefty.cs143.hybrid_images.img;

/** Process image: invert all the blue values. For testing ... */
public class InvertBlue extends FasterPixelProcessor {
    @Override
    public int process(int pixel) {
        return (pixel & 0xffffff00) + (0xff - pixel & 0x000000ff);
    }
}
