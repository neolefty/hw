package org.neolefty.cs143.hybrid_images.img.boof;

import boofcv.struct.image.ImageFloat32;
import org.neolefty.cs143.hybrid_images.img.HasProcessorParams;

/** Generates an ImageFloat32 of a requested size. */
public interface Image32Generator extends HasProcessorParams {
    ImageFloat32 generate(int w, int h);
}
