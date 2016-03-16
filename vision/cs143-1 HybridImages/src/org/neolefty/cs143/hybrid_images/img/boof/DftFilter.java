package org.neolefty.cs143.hybrid_images.img.boof;

import boofcv.abst.transform.fft.DiscreteFourierTransform;
import boofcv.alg.transform.fft.DiscreteFourierTransformOps;
import boofcv.gui.image.ImageGridPanel;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.InterleavedF32;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import org.neolefty.cs143.hybrid_images.ui.HasDebugWindow;
import org.neolefty.cs143.hybrid_images.ui.ProcessorParam;

import javax.swing.*;
import java.util.Collection;

import static org.neolefty.cs143.hybrid_images.ui.util.DebugVizKit.viz;
import static org.neolefty.cs143.hybrid_images.ui.util.DebugVizKit.vizMag;

/** Filter an image in the frequency domain, using a DFT. */
public class DftFilter extends SingleInputBoof32Function implements HasDebugWindow {
    private Image32Generator filterGenerator;
    private ReadOnlyObjectWrapper<JComponent> debugPanelProperty = new ReadOnlyObjectWrapper<>();

    /** Construct a new filter using a filter generator. */
    public DftFilter(Image32Generator filterGenerator) { this.filterGenerator = filterGenerator; }

    @Override
    public void apply(ImageFloat32 input, ImageFloat32 output, int index) {
        // perform the DFT
        int w = input.getWidth(), h = input.getHeight();
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
                    viz(input, 1), vizMag(fftInput), viz(reconstruct),
                    viz(filter, 1), vizMag(fftOutput), viz(output)
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

    @Override public String toString() { return filterGenerator.toString(); }
}
