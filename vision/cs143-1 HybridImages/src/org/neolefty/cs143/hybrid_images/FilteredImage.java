package org.neolefty.cs143.hybrid_images;

import org.neolefty.cs143.hybrid_images.util.ImageIOKit;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/** Filtered image -- low-pass or high-pass frequency filter. */
public class FilteredImage {
    private BufferedImage original;
    public FilteredImage(File file) throws IOException {
        original = ImageIOKit.loadImage(file);
    }

    public BufferedImage getOriginal() {
        return original;
    }
}
