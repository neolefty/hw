package org.neolefty.cs143.hybrid_images.ui.util;

import boofcv.alg.transform.fft.DiscreteFourierTransformOps;
import boofcv.gui.image.VisualizeImageData;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.InterleavedF32;

import java.awt.image.BufferedImage;

/** Utility functions for visualizing debug info. */
public class DebugVizKit {
    public static BufferedImage viz(ImageFloat32 image32) {
        return viz(image32, -1);
    }

    public static BufferedImage viz(ImageFloat32 image32, double normalize) {
        return VisualizeImageData.grayMagnitude(image32, null, normalize);
    }

    public static BufferedImage vizMag(InterleavedF32 fft) {
        fft = fft.clone();
        DiscreteFourierTransformOps.shiftZeroFrequency(fft, true);
        ImageFloat32 viz32 = new ImageFloat32(fft.getWidth(), fft.getHeight());
        DiscreteFourierTransformOps.magnitude(fft, viz32);
        return VisualizeImageData.grayMagnitude(viz32, null, 50);
    }
}
