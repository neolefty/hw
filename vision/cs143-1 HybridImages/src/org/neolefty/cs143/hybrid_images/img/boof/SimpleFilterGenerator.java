package org.neolefty.cs143.hybrid_images.img.boof;

import boofcv.alg.filter.blur.BlurImageOps;
import boofcv.alg.misc.PixelMath;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.ImageFloat32;
import org.neolefty.cs143.hybrid_images.ui.ProcessorParam;

import java.awt.*;
import java.awt.image.BufferedImage;

/** Generates simple FFT filter images, optionally with a Gaussian blur to reduce ringing. */
public class SimpleFilterGenerator extends FilterGenerator {
    private ProcessorParam filterBlur = new ProcessorParam("filter blur", 1, 0, 8,
            "Blurring the filter reduces ringing. Higher number = less blurring, more ringing."
                    + " 0 = no blurring, maximum ringing.");

    /** Create a simple filter generator. */
    public SimpleFilterGenerator() {
        super.addParam(filterBlur);
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
        if (filterBlur.doubleValue() > 0) {
            int blurRadius = (int) (r / filterBlur.doubleValue());
            if (blurRadius >= 2)
                result32 = BlurImageOps.gaussian(result32, null, -1, blurRadius, null);
        }

        // normalize -- all pixels between 0 and 1
        PixelMath.divide(result32, 255f, result32);

        return result32;
    }

//    @Override
//    public String toString() {
//        return super.toString() + (filterBlur.doubleValue() > 0 ? " blur " + filterBlur.doubleValue() : "");
//    }


    @Override
    public String toString() {
        return "DFT circle filter";
    }
}
