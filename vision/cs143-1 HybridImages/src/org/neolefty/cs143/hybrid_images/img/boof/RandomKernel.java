package org.neolefty.cs143.hybrid_images.img.boof;

import boofcv.struct.convolve.Kernel2D_F32;

import java.util.Random;

/** A pseudo-random kernel. Always seeded the same, for repeatability. */
public class RandomKernel implements KernelGenerator {
    @Override
    public Kernel2D_F32 createKernel(int diameter) {
        Kernel2D_F32 result = new Kernel2D_F32(diameter);
        Random r = new Random(42);
        for (int y = 0; y < diameter; ++y)
            for (int x = 0; x < diameter; ++x)
                result.set(x, y, r.nextFloat() * 2f - 1);
        return result;
    }

    @Override
    public String toString() {
        return "Random Kernel";
    }
}
