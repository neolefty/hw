package org.neolefty.cs143.hybrid_images.img.boof;

import boofcv.struct.convolve.Kernel2D_F32;

/** Generate a kernel for convolution, of a specified diameter.
 *  Type is Float32, with pixel values between -1 and 1. */
public interface KernelGenerator {
    Kernel2D_F32 createKernel(int diameter);
}
