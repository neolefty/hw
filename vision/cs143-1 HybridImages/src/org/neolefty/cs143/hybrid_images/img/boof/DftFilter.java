package org.neolefty.cs143.hybrid_images.img.boof;

import boofcv.abst.transform.fft.DiscreteFourierTransform;
import boofcv.alg.misc.PixelMath;
import boofcv.alg.transform.fft.DiscreteFourierTransformOps;
import boofcv.core.image.ConvertImage;
import boofcv.gui.image.ImageGridPanel;
import boofcv.gui.image.VisualizeImageData;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.InterleavedF32;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import org.neolefty.cs143.hybrid_images.img.FilterGenerator;
import org.neolefty.cs143.hybrid_images.img.Image32Generator;
import org.neolefty.cs143.hybrid_images.ui.HasDebugWindow;
import org.neolefty.cs143.hybrid_images.ui.ProcessorParam;
import org.neolefty.cs143.hybrid_images.util.Stopwatch;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.Collection;

/** Filter an image in the frequency domain, using a DFT.
 *  Most efficient if x & y are powers of 2, but not essential. */
public class DftFilter implements Boof8Processor.Function, HasDebugWindow {
    private Image32Generator filterGenerator;
    private ReadOnlyObjectWrapper<JComponent> debugPanelProperty = new ReadOnlyObjectWrapper<>();

    /** Construct a new filter. */
    public DftFilter(FilterGenerator filterGenerator) {
        this.filterGenerator = filterGenerator;
    }

    @Override
    public void apply(ImageUInt8 orig8, ImageUInt8 result8, int index) {
        int w = orig8.getWidth(), h = orig8.getHeight();
        Stopwatch watch = new Stopwatch();

        // make a float copy
        ImageFloat32 orig32 = new ImageFloat32(w, h);
        ConvertImage.convert(orig8, orig32);
        PixelMath.divide(orig32, 255f, orig32); // rescale image brightness 0 to 1
        watch.mark("float copy");

        // do the FFT to the image and the filter
        DiscreteFourierTransform<ImageFloat32, InterleavedF32> dft = DiscreteFourierTransformOps.createTransformF32();
        InterleavedF32 fftOrig32 = new InterleavedF32(w, h, 2);
        dft.forward(orig32, fftOrig32);
        watch.mark("dft");

        // filter template: a circle
        ImageFloat32 filter32 = filterGenerator.generate(w, h);
        watch.mark("make filter");

        // TODO equalize brightness in output image?
        // apply the filter to the image's FFT
        InterleavedF32 fftResult32 = new InterleavedF32(w, h, 2);
        DiscreteFourierTransformOps.shiftZeroFrequency(fftOrig32, true); // shift because the filter is zero-centered
        DiscreteFourierTransformOps.multiplyRealComplex(filter32, fftOrig32, fftResult32);
        DiscreteFourierTransformOps.shiftZeroFrequency(fftResult32, false); // undo the shift
        DiscreteFourierTransformOps.shiftZeroFrequency(fftOrig32, false); // undo the shift
        watch.mark("multiply filter");

        // reverse the fft to get back the filtered image
        ImageFloat32 result32 = new ImageFloat32(w, h);
        dft.inverse(fftResult32, result32);
        watch.mark("inverse dft");

        // convert back to 8-bit
        PixelMath.multiply(result32, 255f, result32); // restore original brightness
        ConvertImage.convert(result32, result8);
        watch.mark("byte copy");

        if (index == 0) { // red
            ImageFloat32 reconstruct32 = new ImageFloat32(w, h);
            dft.inverse(fftOrig32, reconstruct32);
            debugPanelProperty.setValue(new ImageGridPanel(2, 3,
                    viz(orig32, 1), vizMag(fftOrig32), viz(reconstruct32),
                    viz(filter32, 1), vizMag(fftResult32), viz(result32)
            ) {
                @Override public String toString() {
                    return filterGenerator + " - original / filter - fft / multiplied - reconstructed / result";
                }
            });
            watch.mark("visualize");
        }

        System.out.println(" --> " + watch);
    }

    @Override
    public ReadOnlyObjectProperty<JComponent> debugWindowProperty() {
        return debugPanelProperty.getReadOnlyProperty();
    }

    private BufferedImage viz(ImageFloat32 image32) {
        return viz(image32, -1);
    }
    private BufferedImage viz(ImageFloat32 image32, double normalize) {
        return VisualizeImageData.grayMagnitude(image32, null, normalize);
    }

    @Override
    public Collection<ProcessorParam> getProcessorParams() {
        return filterGenerator.getProcessorParams();
    }

    public static BufferedImage vizMag(InterleavedF32 fft) {
        fft = fft.clone();
        DiscreteFourierTransformOps.shiftZeroFrequency(fft, true);
        ImageFloat32 viz32 = new ImageFloat32(fft.getWidth(), fft.getHeight());
        DiscreteFourierTransformOps.magnitude(fft, viz32);
        return VisualizeImageData.grayMagnitude(viz32, null, 50);
    }

    @Override
    public String toString() {
        return filterGenerator.toString();
    }
}
