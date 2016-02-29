package org.neolefty.cs143.hybrid_images.img;

import org.neolefty.cs143.hybrid_images.util.Stopwatch;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Multithreaded pixel processor. */
public class ThreadedPixelProcessor extends PixelProcessor {
    private ExecutorService threadPool;
    private int nPieces;

    public ThreadedPixelProcessor(IntToIntFunction pixelFunction) {
        this(pixelFunction, Runtime.getRuntime().availableProcessors());
    }

    public ThreadedPixelProcessor(IntToIntFunction pixelFunction, int n) {
        this(pixelFunction, Executors.newCachedThreadPool(), n);
    }

    public ThreadedPixelProcessor(IntToIntFunction pixelFunction, ExecutorService executorService, int nPieces) {
        super(pixelFunction);
        this.threadPool = executorService;
        this.nPieces = nPieces;
    }

    @Override
    public BufferedImage process(BufferedImage original) {
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

//            DataBufferByte inBuf = (DataBufferByte) original.getRaster().getDataBuffer();
            DataBufferInt inBuf = (DataBufferInt) copyOrig.getRaster().getDataBuffer();
//            watch.mark("input buffer"); // always 0
            int[] in = inBuf.getData();
//            watch.mark("input array"); // always 0

            // output buffer
            BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            DataBufferInt outBuf = (DataBufferInt) result.getRaster().getDataBuffer();
            watch.mark("create tmp");
            int[] out = outBuf.getData();
//            watch.mark("output array"); // always 0

            // do the processing
            int chunk = in.length / nPieces;
            CountDownLatch latch = new CountDownLatch(nPieces);
            for (int i = 0; i < nPieces; ++i) {
                // from a to b-1; ensure that we go right up to the end
                final int a = i * chunk, b = (i == nPieces - 1 ? in.length : a + chunk);
                threadPool.submit(() -> {
                    for (int j = a; j < b; ++j)
                        out[j] = getPixelFunction().apply(in[j]);
                    latch.countDown();
                });
            }

            try { latch.await(); } catch (InterruptedException e) { e.printStackTrace(); }

            watch.mark("process");
            copyOrig.flush();

//            for (int y = 0; y < original.getHeight(); ++y)
//                for (int x = 0; x < original.getWidth(); ++x)
//                    result.setRGB(x, y, process(original.getRGB(x, y)));

            BufferedImage accel = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            accel.getGraphics().drawImage(result, 0, 0, null);
            result.flush();
            watch.mark("copy");

            System.out.println(getName() + " - " + (w * h) + " pixels - " + watch
                     + " -- " + (1000000 * watch.getElapsed() / (w * h)) + " ns per pixel - " + getName());
            return accel;
        }
    }

    @Override
    public String getName() {
        return super.getName() + "-" + nPieces;
    }
}