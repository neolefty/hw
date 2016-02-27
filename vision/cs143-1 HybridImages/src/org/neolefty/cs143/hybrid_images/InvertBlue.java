package org.neolefty.cs143.hybrid_images;

import java.awt.image.BufferedImage;

/** Process image: invert all the blue values. For testing ... */
public class InvertBlue extends ProcessedImage {
    @Override
    public BufferedImage process(BufferedImage original) {
        if (original == null)
            return null;
        else {
            BufferedImage result = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);
            long start = System.currentTimeMillis();
            for (int y = 0; y < original.getHeight(); ++y)
                for (int x = 0; x < original.getWidth(); ++x) {
                    int p = original.getRGB(x, y);
                    int p2 = (p & 0xffffff00) + (0xff - p & 0x000000ff);
                    result.setRGB(x, y, p2);
                }
            System.out.println("Invert b: "
                    + (original.getWidth() * original.getHeight()) + " pixels "
                    + (System.currentTimeMillis() - start) + " ms");
            return result;
        }
    }
}
