package org.neolefty.cs143.hybrid_images.img.boof;

import boofcv.abst.transform.fft.DiscreteFourierTransform;
import boofcv.alg.misc.PixelMath;
import boofcv.alg.transform.fft.DiscreteFourierTransformOps;
import boofcv.core.image.ConvertImage;
import boofcv.gui.image.ImageGridPanel;
import boofcv.gui.image.ShowImages;
import boofcv.gui.image.VisualizeImageData;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.InterleavedF32;

import java.awt.image.BufferedImage;

/** FFT of an image. Works best if x & y are powers of 2. */
public class FftUInt8 implements Boof8Processor.Function {
    public enum Part { magnitude, phase }

    private Part part;

    public FftUInt8(Part part) {
        this.part = part;
    }

    @Override
    public void apply(ImageUInt8 in, ImageUInt8 out, int index) {
        int w = in.getWidth(), h = in.getHeight();

        // make a float copy
        ImageFloat32 in32 = new ImageFloat32(w, h);
        ConvertImage.convert(in, in32);
        PixelMath.divide(in32, 255f, in32); // rescale image brightness 0 to 1

//        if (index == 0) ShowImages.showWindow(new ImageGridPanel(1, 1, VisualizeImageData.grayMagnitude(in32, null, 1)), "red");

        // do the FFT
        InterleavedF32 outFft32 = new InterleavedF32(w, h, 2);
        DiscreteFourierTransform<ImageFloat32, InterleavedF32> dft = DiscreteFourierTransformOps.createTransformF32();
        dft.forward(in32, outFft32);

        // convert to output
        ImageFloat32 out32 = new ImageFloat32(w, h);
        DiscreteFourierTransformOps.shiftZeroFrequency(outFft32, true);
        if (part == Part.magnitude) {

            DiscreteFourierTransformOps.magnitude(outFft32, out32);
            BufferedImage visualMag = VisualizeImageData.grayMagnitude(out32, null, 20);
            ImageGridPanel show = new ImageGridPanel(1, 1, visualMag);
            ShowImages.showWindow(show, "Magnitude " + index);

            PixelMath.multiply(out32, 70f, out32); // rescale brightness to 0 to 255
        }
        else if (part == Part.phase) {
            DiscreteFourierTransformOps.phase(outFft32, out32);

            BufferedImage visualPhase = VisualizeImageData.colorizeSign(out32, null, Math.PI);
            ImageGridPanel show = new ImageGridPanel(1, 1, visualPhase);
            ShowImages.showWindow(show, "Phase " + index);

            PixelMath.multiply(out32, (float) (255f / Math.PI), out32); // rescale brightness to 0 to 255
        }
        else
            throw new UnsupportedOperationException("" + part);
        ConvertImage.convert(out32, out);
    }
}
