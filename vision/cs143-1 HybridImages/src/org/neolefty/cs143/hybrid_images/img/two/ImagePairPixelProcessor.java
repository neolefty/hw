package org.neolefty.cs143.hybrid_images.img.two;

import org.neolefty.cs143.hybrid_images.img.ImageProcessor;
import org.neolefty.cs143.hybrid_images.img.boof.HasProcessorParamsBase;
import org.neolefty.cs143.hybrid_images.img.geom.ImagePadKit;
import org.neolefty.cs143.hybrid_images.ui.ProcessorParam;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/** Add two images together. */
public class ImagePairPixelProcessor extends ImageProcessor {
    private BinaryIntFunction function;
    private HasProcessorParamsBase params = new HasProcessorParamsBase();

    private int nPieces = Runtime.getRuntime().availableProcessors();
    private ExecutorService exec;

    public ImagePairPixelProcessor(BinaryIntFunction function, ExecutorService exec) {
        this.function = function;
        params.addIfHasParams(function);
        this.exec = exec;
        addInput();
        addInput();
    }

    @Override
    public Collection<ProcessorParam> getProcessorParams() {
        return params.getProcessorParams();
    }

    @Override
    public BufferedImage process(Collection<BufferedImage> originals) {
        checkImageCount(originals.size(), 2, 2);
        Iterator<BufferedImage> iter = originals.iterator();
        BufferedImage aOrig = iter.next(), bOrig = iter.next();

        // pad them to be the same size
        int w = Math.max(aOrig.getWidth(), bOrig.getWidth()), h = Math.max(aOrig.getHeight(), bOrig.getHeight());
        BufferedImage aPadded = ImagePadKit.pad(aOrig, w, h, true), // force a copy to ensure 32-bit pixel format
                bPadded = ImagePadKit.pad(bOrig, w, h, true);

        // set up work image
        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        int[] workA = ((DataBufferInt) aPadded.getRaster().getDataBuffer()).getData(),
                workB = ((DataBufferInt) bPadded.getRaster().getDataBuffer()).getData(),
                workResult = ((DataBufferInt) result.getRaster().getDataBuffer()).getData();
        int chunk = workA.length / nPieces;
        CountDownLatch latch = new CountDownLatch(nPieces);

        // do the work
        for (int i = 0; i < nPieces; ++i) {
            final int start = i * chunk, stop = (i == nPieces - 1 ? workA.length : start + chunk);
            exec.execute(() -> {
                try {
                    for (int j = start; j < stop; ++j)
                        workResult[j] = function.apply(workA[j], workB[j]);
                } finally {
                    latch.countDown();
                }
            });
        }

        // wait for it to finish
        try {
            latch.await();
            aPadded.flush();
            bPadded.flush();
        } catch (InterruptedException ignored) {
            System.out.print(":");
            return aOrig;
        }

        // return an accelerated image
        BufferedImage accel = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
        accel.getGraphics().drawImage(result, 0, 0, null);
        result.flush();
        return accel;
    }

    @Override
    public String toString() {
        return function.toString();
    }
}
