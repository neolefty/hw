package org.neolefty.cs143.hybrid_images.img.boof;

import boofcv.struct.image.ImageFloat32;
import org.neolefty.cs143.hybrid_images.img.ImageProcessor;

import java.util.Collection;

/** Convenience class for implementing single-input functions. */
public abstract class SingleInputBoof32Function extends Boof32Function {
    @Override
    public void apply(Collection<ImageFloat32> inputs, ImageFloat32 output, int index) {
        ImageProcessor.checkImageCount(inputs.size(), 1, 1);
        apply(inputs.iterator().next(), output, index);
    }

    /** Implement this in your function. */
    public abstract void apply(ImageFloat32 input, ImageFloat32 output, int index);
}
