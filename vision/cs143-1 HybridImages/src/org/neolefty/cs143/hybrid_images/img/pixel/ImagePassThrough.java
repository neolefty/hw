package org.neolefty.cs143.hybrid_images.img.pixel;

import org.neolefty.cs143.hybrid_images.img.ImageProcessor;

import java.awt.image.BufferedImage;
import java.util.Collection;

public class ImagePassThrough extends ImageProcessor {
    @Override
    public BufferedImage process(Collection<BufferedImage> originals) {
        checkImageCount(originals.size(), 1, 1);
        return originals.iterator().next();
    }

    @Override public String toString() { return "nop"; }
}
