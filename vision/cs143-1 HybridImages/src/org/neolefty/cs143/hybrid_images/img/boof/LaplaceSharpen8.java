package org.neolefty.cs143.hybrid_images.img.boof;

import boofcv.struct.convolve.Kernel2D_F32;
import boofcv.struct.image.ImageUInt8;
import org.neolefty.cs143.hybrid_images.ui.ProcessorParam;

import java.util.*;

/** Sharpen an image using a Laplacian of Gaussian function. */
public class LaplaceSharpen8 extends SingleInputBoof8Function {
    // see http://homepages.inf.ed.ac.uk/rbf/HIPR2/log.htm
    private ProcessorParam sigma = new ProcessorParam("sigma", 1, 0, 5, "Something something?"),
        radius = new ProcessorParam("radius", 0.01, 0, 0.1, "Radius of Gaussian basis, as a portion of the whole image.");

    private List<ProcessorParam> params = Collections.unmodifiableList(Arrays.asList(sigma, radius));

    @Override
    public void apply(ImageUInt8 input, ImageUInt8 output, int index) {
        int h = input.getHeight(), w = input.getWidth();
        // radius of 0 would be degenerate -- smallest useful 1
        int r = Math.max(1, (int) (radius.doubleValue() * Math.sqrt(h * w)));
        int diameter = r * 2 + 1; // size of square unsharp convolution kernel
        Kernel2D_F32 kernel = createKernel(diameter);

    }

    private Kernel2D_F32 createKernel(int diameter) {
        Kernel2D_F32 result = new Kernel2D_F32(diameter);
        Random r = new Random();
        for (int y = 0; y < diameter; ++y)
            for (int x = 0; x < diameter; ++x)
                result.set(x, y, r.nextFloat() * 2 - 1);
        return result;
    }

    @Override
    public Collection<ProcessorParam> getProcessorParams() {
        return params;
    }
}
