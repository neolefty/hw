package org.neolefty.cs143.hybrid_images.img.boof;

import boofcv.alg.misc.PixelMath;
import boofcv.core.image.ConvertImage;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageUInt8;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** A function that operates on 32-bit floats, normalized to 0-1. */
public abstract class Boof32Function implements Boof8Processor.Function {
    @Override
    public void apply(Collection<ImageUInt8> originals, ImageUInt8 output, int index) {
        // convert to float
        List<ImageFloat32> orig32s = new ArrayList<>();
        int maxW = 0, maxH = 0;
        for (ImageUInt8 orig8 : originals) {
            int w = orig8.getWidth(), h = orig8.getHeight();
            maxW = Math.max(w, maxW);
            maxH = Math.max(h, maxH);
            ImageFloat32 orig32 = new ImageFloat32(w, h);
            ConvertImage.convert(orig8, orig32);
            PixelMath.divide(orig32, 255f, orig32);
            orig32s.add(orig32);
        }

        // do the processing
        ImageFloat32 output32 = new ImageFloat32(maxW, maxH);
        apply(orig32s, output32, index);

        // convert back to byte
        PixelMath.multiply(output32, 255f, output32);
        ConvertImage.convert(output32, output);
    }

    /** Inputs & outputs normalized to range = 1 */
    abstract public void apply(Collection<ImageFloat32> inputs, ImageFloat32 output, int index);
}
