package org.neolefty.cs143.hybrid_images.img;

import org.neolefty.cs143.hybrid_images.ProcessedImage;

import java.awt.image.BufferedImage;

public class ImagePassThrough extends ProcessedImage {
    @Override
    public BufferedImage process(BufferedImage original) {
        return original;
    }
}
