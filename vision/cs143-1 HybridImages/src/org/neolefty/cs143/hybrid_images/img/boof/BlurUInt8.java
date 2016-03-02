package org.neolefty.cs143.hybrid_images.img.boof;

import boofcv.alg.filter.blur.BlurImageOps;
import boofcv.struct.image.ImageUInt8;

/** A Gaussian blur. */
public class BlurUInt8 implements BoofUInt8ImageProcessor.ImageUInt8Function {
    private int radius;

    public BlurUInt8(int radius) {
        this.radius = radius;
    }

    @Override
    public void apply(ImageUInt8 in, ImageUInt8 out, int index) {
        BlurImageOps.gaussian(in, out, -1, radius, null);
    }
}
