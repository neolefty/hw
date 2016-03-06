package org.neolefty.cs143.hybrid_images.img.pixel;

import org.neolefty.cs143.hybrid_images.img.ImageProcessor;

import java.awt.image.BufferedImage;

public class ImagePassThrough extends ImageProcessor {
    @Override
    public BufferedImage process(BufferedImage original) {
        return original;
    }

    @Override public String toString() { return "nop"; }
}
