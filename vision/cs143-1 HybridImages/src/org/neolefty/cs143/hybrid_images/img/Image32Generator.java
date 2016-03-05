package org.neolefty.cs143.hybrid_images.img;

import boofcv.struct.image.ImageFloat32;

/** Generates an ImageFloat32 of a requested size. */
public interface Image32Generator {
    ImageFloat32 generate(int w, int h);
}
