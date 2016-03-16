package org.neolefty.cs143.hybrid_images.img.boof;

import boofcv.abst.transform.fft.DiscreteFourierTransform;
import boofcv.alg.misc.ImageStatistics;
import boofcv.alg.misc.PixelMath;
import boofcv.alg.transform.fft.DiscreteFourierTransformOps;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.InterleavedF32;
import org.neolefty.cs143.hybrid_images.ui.ProcessorParam;

import java.util.Collection;
import java.util.Collections;

/** Discrete Fourier Transform of an image, for visualization. Can show either the magnitude or phase. */
public class Dft32 extends SingleInputBoof32Function {
    private ProcessorParam logGain = new ProcessorParam("log gain", -3, -5, 0, "Log of fft magnitude gain.");

    public enum Part { magnitude, phase }

    private Part part;

    public Dft32(Part part) {
        this.part = part;
    }

    @Override
    public void apply(ImageFloat32 input, ImageFloat32 output, int index) {
        int w = input.getWidth(), h = input.getHeight();

        // do the FFT
        InterleavedF32 outFft = new InterleavedF32(w, h, 2);
        DiscreteFourierTransform<ImageFloat32, InterleavedF32> dft = DiscreteFourierTransformOps.createTransformF32();
        dft.forward(input, outFft);

        // convert to output
        DiscreteFourierTransformOps.shiftZeroFrequency(outFft, true);
        if (part == Part.magnitude) {
            DiscreteFourierTransformOps.magnitude(outFft, output);
            float gain = (float) Math.pow(10, logGain.doubleValue());
            PixelMath.multiply(output, gain, output); // rescale for visualizability
//            System.out.println("FFT mag brightness: " + ImageStatistics.min(output) + " to " + ImageStatistics.max(output));
            PixelMath.boundImage(output, 0, 1);
        }
        else if (part == Part.phase) {
            DiscreteFourierTransformOps.phase(outFft, output);
            PixelMath.multiply(output, (float) (0.5f / Math.PI), output); // normalize to -.5 to 0.5
            PixelMath.plus(output, 0.5f, output); // normalize to 0 to 1
            System.out.println("FFT phase brightness: " + ImageStatistics.min(output) + " to " + ImageStatistics.max(output));
            System.out.println();
        }
        else
            throw new UnsupportedOperationException(part.toString());
    }

    @Override public String toString() { return "FFT " + part; }

    @Override public Collection<ProcessorParam> getProcessorParams() {
        return part == Part.magnitude ? Collections.singleton(logGain) : null;
    }
}
