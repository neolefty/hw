package org.neolefty.cs143.hybrid_images.img.boof;

import boofcv.struct.convolve.Kernel2D_F32;
import org.neolefty.cs143.hybrid_images.ui.ProcessorParam;

/** A kernel to sharpen an image using a Laplacian of Gaussian function.
 *  @see <a href="http://homepages.inf.ed.ac.uk/rbf/HIPR2/log.htm">Spatial Filters
 *  - Laplacian of Gaussian</a> */
public class LaplaceSharpenKernel extends HasProcessorParamsBase implements KernelGenerator {
    private ProcessorParam sigma = new ProcessorParam("sigma", 1, 0, 5, "The Gaussian's standard deviation");

    public LaplaceSharpenKernel() {
        addParam(sigma);
    }

    @Override
    public Kernel2D_F32 createKernel(int diameter) {
        int r = diameter / 2 + 1;
        Kernel2D_F32 result = new Kernel2D_F32(diameter);
        for (int y = 0; y < diameter; ++y)
            for (int x = 0; x < diameter; ++x)
                result.set(x, y, (float) laplaceOfGaussian(x-r, y-r, sigma.doubleValue()));
        return result;
    }

    private double laplaceOfGaussian(int x, int y, double sigma) {
        double s2 = sigma * sigma, s4 = s2 * s2;
        double d2 = x * x + y * y;
        double a = -1 / (Math.PI * s4);
        double b = -d2/(2 * s2);
        double c = Math.exp(b);
        return a * (1 + b) * c;
    }

    @Override public String toString() { return "Laplace Sharpen"; }
}
