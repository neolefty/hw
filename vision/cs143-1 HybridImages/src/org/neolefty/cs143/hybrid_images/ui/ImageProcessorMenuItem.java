package org.neolefty.cs143.hybrid_images.ui;

import org.neolefty.cs143.hybrid_images.img.ImageProcessor;

/** A menu item representing an {@link ImageProcessor}. */
public class ImageProcessorMenuItem implements Comparable {
    private ImageProcessor processor;

    public ImageProcessorMenuItem(ImageProcessor processor) {
        this.processor = processor;
    }

    @Override
    public int compareTo(Object o) {
        return toString().compareTo(o.toString());
    }

    public ImageProcessor getProcessor() { return processor; }

    @Override public String toString() { return processor.toString(); }
}
