package org.neolefty.cs143.hybrid_images.img.boof;

import boofcv.alg.filter.blur.BlurImageOps;
import boofcv.struct.image.ImageUInt8;

/** A Gaussian blur. */
public class GaussBlur8 implements Boof8Processor.Function {
    private int radius;

    public GaussBlur8(int radius) {
        this.radius = radius;
    }

    @Override
    public void apply(ImageUInt8 in, ImageUInt8 out, int index) {
        BlurImageOps.gaussian(in, out, -1, radius, null);
    }

    @Override
    public String toString() {
        return "Gaussian blur " + radius;
    }
}
