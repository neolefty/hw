package org.neolefty.cs143.hybrid_images.img.geom;

import org.neolefty.cs143.hybrid_images.img.ImageProcessor;
import org.neolefty.cs143.hybrid_images.ui.ProcessorParam;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

/** Scale down an image if it is too large */
public class ImageShrinker extends ImageProcessor {
    private ProcessorParam size = new ProcessorParam("size", 512, 128, 2048, true,
            "Resized dimension -- geometric average of h & w.");
    private java.util.List<ProcessorParam> params = Collections.singletonList(size);

    @Override
    public BufferedImage process(Collection<BufferedImage> originals) {
        checkImageCount(originals.size(), 1, 1);
        BufferedImage original = originals.iterator().next();
        int w = original.getWidth(), h = original.getHeight();
        int maxPixels = (int) (size.doubleValue() * size.doubleValue());
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

    /** Set the size of this image. The geometric average of the height and
     *  width will be close to <tt>sideSize</tt>. */
    public void setSize(int sideSize) {
        size.setValue(sideSize);
    }
}
