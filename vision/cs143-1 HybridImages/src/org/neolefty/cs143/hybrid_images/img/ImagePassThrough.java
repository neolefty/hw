package org.neolefty.cs143.hybrid_images.img;

import org.neolefty.cs143.hybrid_images.ImageProcessor;

import java.awt.image.BufferedImage;

public class ImagePassThrough extends ImageProcessor {
    @Override
    public BufferedImage process(BufferedImage original) {
        return original;
    }
}
