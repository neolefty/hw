package org.neolefty.cs143.hybrid_images.img.boof;

import boofcv.alg.filter.blur.BlurImageOps;
import boofcv.alg.misc.ImageMiscOps;
import boofcv.struct.image.ImageUInt8;
import org.neolefty.cs143.hybrid_images.ui.ProcessorParam;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/** A Gaussian blur. */
public class GaussBlur8 extends SingleInputBoof8Function {
    private ProcessorParam radius = new ProcessorParam("radius", 0.01, 0, 0.2,
            "Radius of the blur as a proportion of the image.");
    private List<ProcessorParam> params = Collections.singletonList(radius);

    @Override
    public void apply(ImageUInt8 in, ImageUInt8 out, int index) {
        // geometric average of h & w, times [0 to 1]
        int r = (int) (Math.sqrt(in.getWidth() * in.getHeight()) * radius.doubleValue());
        if (r == 0)
            ImageMiscOps.copy(0, 0, 0, 0, in.getWidth(), in.getHeight(), in, out);
        else
            BlurImageOps.gaussian(in, out, -1, r, null);
    }

    @Override
    public Collection<ProcessorParam> getProcessorParams() { return params; }

    @Override
    public String toString() {
        return "Gaussian blur"; // + " " + radius;
    }
}
