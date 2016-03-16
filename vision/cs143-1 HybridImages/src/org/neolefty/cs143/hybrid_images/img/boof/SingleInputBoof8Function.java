package org.neolefty.cs143.hybrid_images.img.boof;

import boofcv.struct.image.ImageUInt8;
import org.neolefty.cs143.hybrid_images.img.ImageProcessor;

import java.util.Collection;

/** Convenience class for implementing single-input functions. */
public abstract class SingleInputBoof8Function implements BoofProcessor.Function {
    @Override
    public void apply(Collection<ImageUInt8> inputs, ImageUInt8 output, int index) {
        ImageProcessor.checkImageCount(inputs.size(), 1, 1);
        apply(inputs.iterator().next(), output, index);
    }

    /** Implement this. */
    public abstract void apply(ImageUInt8 input, ImageUInt8 output, int index);
}
