package org.neolefty.cs143.hybrid_images.img.geom;

import org.neolefty.cs143.hybrid_images.img.ImageProcessor;

import java.awt.*;
import java.awt.image.BufferedImage;

// TODO parameterize
/** Scale down an image if it is too large */
public class ImageShrinker extends ImageProcessor {
    private int maxPixels;

    public ImageShrinker(int maxPixels) {
        this.maxPixels = maxPixels;
    }

    /** Shrink to have no more than 512 * 512 pixels. */
    public ImageShrinker() { this(512 * 512); }

    @Override
    public BufferedImage process(BufferedImage original) {
        int w = original.getWidth(), h = original.getHeight();
        if (w * h <= maxPixels)
            return original;
        else {
            double fraction = Math.sqrt(((double) maxPixels) / (w * h));
            int newW = (int) (w * fraction), newH = (int) (h * fraction);
            BufferedImage result = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) result.getGraphics();
            g.scale(((double) newW) / w, ((double) newH) / h);
            g.drawImage(original, 0, 0, null);
            return result;
        }
    }
}
