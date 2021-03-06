package org.neolefty.cs143.hybrid_images.img.pixel;

import org.neolefty.cs143.hybrid_images.img.HasProcessorParams;
import org.neolefty.cs143.hybrid_images.ui.ProcessorParam;
import org.neolefty.cs143.hybrid_images.util.Stopwatch;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Collection;

/** Process one pixel at a time. */
public class UnthreadedPixelProcessor extends SlowPixelProcessor {
    public UnthreadedPixelProcessor(IntToIntFunction pixelFunction) {
        super(pixelFunction);
    }

    @Override
    public BufferedImage processSingle(BufferedImage original) {
        if (original == null)
            return null;
        else {
            Stopwatch watch = new Stopwatch();
            int h = original.getHeight(), w = original.getWidth();

            // copy original to avoid messing up its acceleration
            // (also, now we know its pixel format)
            BufferedImage copyOrig = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            copyOrig.getGraphics().drawImage(original, 0, 0, null);
            watch.mark("copy");

            DataBufferInt inBuf = (DataBufferInt) copyOrig.getRaster().getDataBuffer();
            int[] in = inBuf.getData();

            // output buffer
            BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            DataBufferInt outBuf = (DataBufferInt) result.getRaster().getDataBuffer();
            watch.mark("create tmp");
            int[] out = outBuf.getData();

            // do the processing
            for (int i = 0; i < in.length; ++i)
                out[i] = getPixelFunction().apply(in[i]);
            watch.mark("process");
            copyOrig.flush();

            BufferedImage accel = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            accel.getGraphics().drawImage(result, 0, 0, null);
            result.flush();
            watch.mark("copy");

//            System.out.println(toString() + ": " + (w * h) + " pixels -- " + watch
//                     + " -- " + (1000000 * watch.getElapsed() / (w * h)) + " ns per pixel");
            return accel;
        }
    }

    @Override
    public Collection<ProcessorParam> getProcessorParams() {
        IntToIntFunction f = getPixelFunction();
        if (f instanceof HasProcessorParams)
            return ((HasProcessorParams) f).getProcessorParams();
        else
            return null;
    }
}
