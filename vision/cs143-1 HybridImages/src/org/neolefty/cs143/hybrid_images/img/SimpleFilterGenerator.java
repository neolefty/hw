package org.neolefty.cs143.hybrid_images.img;

import boofcv.alg.filter.blur.BlurImageOps;
import boofcv.alg.misc.PixelMath;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.ImageFloat32;

import java.awt.*;
import java.awt.image.BufferedImage;

/** Generates simple FFT filter images, optionally with a Gaussian blur to reduce ringing. */
public class SimpleFilterGenerator extends FilterGenerator {
    private double filterBlur;

    /** Construct a new filter generator.
     *  @param fraction The fraction of area in the center of the FFT, between 0 and 1.
     *                  Smaller numbers for a smaller circle.
     *  @param filterBlur How much to blur the filter, to cut down on ringing, as a fraction of the filter.
     *                    For example, if the filter radius ends up being 20, and the filterBlur is 5, then we
     *                    apply a 20/5 = 4-pixel gaussian blur to the filter before using it.
     *                    Higher numbers = less blurring, more ringing.
     *                    0 or less = ignored. */
    public SimpleFilterGenerator(double fraction, double filterBlur, Type type) {
        super(type, fraction);
        this.filterBlur = filterBlur;
    }

    /** Construct a new filter generator with no filter blur (there will be ringing)
     *  @param fraction The fraction of area in the center of the FFT, between 0 and 1.
     *                  Smaller numbers for a smaller circle. */
    public SimpleFilterGenerator(double fraction, Type type) {
        this(fraction, -1, type);
    }

    @Override
    public ImageFloat32 generate(int w, int h) {
        // draw in a BufferedImage for speed
        BufferedImage resultBI = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics g = resultBI.getGraphics();

        // 1. background fill
        g.setColor(getFilterType() == Type.lowPass ? Color.BLACK : Color.WHITE);
        g.fillRect(0, 0, w, h);

        // 2. circle fill
        int r = (int) computeRadius(w, h);
        g.setColor(getFilterType() == Type.lowPass ? Color.WHITE : Color.BLACK);
        g.fillOval(w/2 - r, h/2 - r, r + r, r + r);

        // copy to a float image
        ImageFloat32 result32 = new ImageFloat32(w, h);
        ConvertBufferedImage.convertFrom(resultBI, result32);

        // blur, if requested & more than 1 pixel of blur
        if (filterBlur > 0) {
            int blurRadius = (int) (r / filterBlur);
            if (blurRadius >= 2)
                result32 = BlurImageOps.gaussian(result32, null, -1, blurRadius, null);
        }

        // normalize -- all pixels between 0 and 1
        PixelMath.divide(result32, 255f, result32);

        return result32;
    }

    @Override
    public String toString() {
        return super.toString() + (filterBlur > 0 ? " blur " + filterBlur : "");
    }
}
