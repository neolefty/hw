package org.neolefty.cs143.hybrid_images.img.boof;

import boofcv.abst.transform.fft.DiscreteFourierTransform;
import boofcv.alg.transform.fft.DiscreteFourierTransformOps;
import boofcv.gui.image.ImageGridPanel;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.InterleavedF32;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import org.neolefty.cs143.hybrid_images.img.ImageProcessor;
import org.neolefty.cs143.hybrid_images.ui.HasDebugWindow;
import org.neolefty.cs143.hybrid_images.ui.ProcessorParam;

import javax.swing.*;
import java.util.Collection;

/** Filter an image in the frequency domain, using a DFT. */
public class DftFilter32 extends Boof32Function implements HasDebugWindow {
    private Image32Generator filterGenerator;
    private ReadOnlyObjectWrapper<JComponent> debugPanelProperty = new ReadOnlyObjectWrapper<>();

    /** Construct a new filter using a filter generator. */
    public DftFilter32(Image32Generator filterGenerator) { this.filterGenerator = filterGenerator; }

    @Override
    public void apply(Collection<ImageFloat32> inputs, ImageFloat32 output, int index) {
        // look at inputs
        ImageProcessor.checkImageCount(inputs.size(), 1, 1);
        ImageFloat32 input = inputs.iterator().next();
        int w = input.getWidth(), h = input.getHeight();

        // perform the DFT
        DiscreteFourierTransform<ImageFloat32, InterleavedF32> dft = DiscreteFourierTransformOps.createTransformF32();
        InterleavedF32 fftInput = new InterleavedF32(w, h, 2);
        dft.forward(input, fftInput);

        // filter the transformed image
        ImageFloat32 filter = filterGenerator.generate(w, h);
        InterleavedF32 fftOutput = new InterleavedF32(w, h, 2);
        DiscreteFourierTransformOps.shiftZeroFrequency(fftInput, true); // shift because the filter is zero-centered
        DiscreteFourierTransformOps.multiplyRealComplex(filter, fftInput, fftOutput);
        DiscreteFourierTransformOps.shiftZeroFrequency(fftOutput, false); // undo the shift
        DiscreteFourierTransformOps.shiftZeroFrequency(fftInput, false); // undo the shift

        // reverse DFT
        dft.inverse(fftOutput, output);

        // debug -- visualize red
        if (index == 0) {
            ImageFloat32 reconstruct = new ImageFloat32(w, h);
            dft.inverse(fftInput, reconstruct);
            debugPanelProperty.setValue(new ImageGridPanel(2, 3,
                    DftFilter.viz(input, 1), DftFilter.vizMag(fftInput), DftFilter.viz(reconstruct),
                    DftFilter.viz(filter, 1), DftFilter.vizMag(fftOutput), DftFilter.viz(output)
            ) {
                @Override public String toString() {
                    return filterGenerator + " - original / filter - fft / multiplied - reconstructed / result";
                }
            });
        }
    }

    @Override
    public ReadOnlyObjectProperty<JComponent> debugWindowProperty() {
        return debugPanelProperty.getReadOnlyProperty();
    }

    @Override
    public Collection<ProcessorParam> getProcessorParams() {
        return filterGenerator.getProcessorParams();
    }

    @Override public String toString() { return filterGenerator.toString() + "32"; }
}
