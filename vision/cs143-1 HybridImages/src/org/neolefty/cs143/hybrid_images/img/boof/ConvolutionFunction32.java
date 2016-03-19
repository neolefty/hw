package org.neolefty.cs143.hybrid_images.img.boof;

import boofcv.alg.filter.convolve.GConvolveImageOps;
import boofcv.alg.filter.kernel.KernelMath;
import boofcv.alg.misc.PixelMath;
import boofcv.core.image.border.BorderType;
import boofcv.core.image.border.FactoryImageBorder;
import boofcv.core.image.border.ImageBorder;
import boofcv.gui.image.ImageGridPanel;
import boofcv.struct.convolve.Kernel2D_F32;
import boofcv.struct.image.ImageFloat32;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import org.neolefty.cs143.hybrid_images.img.HasProcessorParams;
import org.neolefty.cs143.hybrid_images.ui.HasDebugWindow;
import org.neolefty.cs143.hybrid_images.ui.ProcessorParam;

import javax.swing.*;

import static org.neolefty.cs143.hybrid_images.ui.util.DebugVizKit.viz;

/** Convolve an image with a kernel. */
public class ConvolutionFunction32 extends SingleInputBoof32Function implements HasDebugWindow {
    private ReadOnlyObjectWrapper<JComponent> debugWindowProperty = new ReadOnlyObjectWrapper<>();
    private KernelGenerator kernelGenerator;

    private ProcessorParam radius = new ProcessorParam("radius", 0.01, 0, 0.1,
            "Radius of Gaussian basis, as a portion of the whole image.");

    public ConvolutionFunction32(KernelGenerator kernelGenerator) {
        addParam(radius);
        addIfHasParams(kernelGenerator);
        this.kernelGenerator = kernelGenerator;
    }

    @Override
    public void apply(ImageFloat32 input, ImageFloat32 output, int index) {
        int h = input.getHeight(), w = input.getWidth();
        // radius of 0 would be degenerate -- smallest useful 1
        int r = Math.max(1, (int) (radius.doubleValue() * Math.sqrt(h * w)));
        int diameter = r * 2 + 1; // size of square unsharp convolution kernel
        ImageBorder<ImageFloat32> border = FactoryImageBorder.single(output, BorderType.EXTENDED);
        Kernel2D_F32 kernel = kernelGenerator.createKernel(diameter);
        GConvolveImageOps.convolve(kernel, input, output, border);

        if (index == 0) { // red
            ImageFloat32 kernelImage =  KernelMath.convertToImage(kernel);
            PixelMath.plus(kernelImage, 1, kernelImage);
            PixelMath.multiply(kernelImage, 127, kernelImage);
            debugWindowProperty.set(new ImageGridPanel(1, 1, viz(kernelImage)));
        }
    }

    @Override
    public ReadOnlyObjectProperty<JComponent> debugWindowProperty() {
        return debugWindowProperty.getReadOnlyProperty();
    }

    @Override public String toString() { return kernelGenerator.toString(); }
}
