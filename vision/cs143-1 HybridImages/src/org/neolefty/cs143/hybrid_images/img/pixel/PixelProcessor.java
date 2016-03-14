package org.neolefty.cs143.hybrid_images.img.pixel;

import org.neolefty.cs143.hybrid_images.util.Stopwatch;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/** Multithreaded pixel processor. */
public class PixelProcessor extends UnthreadedPixelProcessor {
    private ExecutorService exec;
    private int nPieces;

    public PixelProcessor(IntToIntFunction pixelFunction, ExecutorService exec) {
        this(pixelFunction, exec, Runtime.getRuntime().availableProcessors());
    }

    public PixelProcessor(IntToIntFunction pixelFunction, ExecutorService exec, int nPieces) {
        super(pixelFunction);
        this.exec = exec;
        this.nPieces = nPieces;
    }

    @Override
    public BufferedImage processSingle(BufferedImage original) {
        if (original == null)
            return null;
        else {
            Stopwatch watch = new Stopwatch();
            int h = original.getHeight(), w = original.getWidth();

            // copy original for two reasons
            // 1. avoid messing up its acceleration
            // 2. we can control the pixel format
            BufferedImage copy = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            copy.getGraphics().drawImage(original, 0, 0, null);
            int[] work = ((DataBufferInt) copy.getRaster().getDataBuffer()).getData();
            watch.mark("copy");

            // do the processing
            int chunk = work.length / nPieces;
            CountDownLatch latch = new CountDownLatch(nPieces);
            for (int i = 0; i < nPieces; ++i) {
                // from a to b-1; ensure that we go right up to the end
                final int a = i * chunk, b = (i == nPieces - 1 ? work.length : a + chunk);
                final int finalI = i;
                exec.execute(() -> {
                    for (int j = a; j < b; ++j)
                        work[j] = getPixelFunction().apply(work[j]);
                    if (latch.getCount() == nPieces)
                        watch.mark("first=" + finalI);
                    latch.countDown();
                });
            }
            try {
                latch.await();
                watch.mark("lag");
                copy.flush();

                BufferedImage accel = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                accel.getGraphics().drawImage(copy, 0, 0, null);
                copy.flush();
                watch.mark("copy");

//            System.out.println(toString() + " - " + (w * h) + " pixels - " + watch
//                     + " -- " + (1000000 * watch.getElapsed() / (w * h)) + " ns per pixel - " + toString());
                return accel;
            } catch (InterruptedException ignored) {
                System.out.print(",");
                return original; // processing was interrupted, so leave it the same
            }
        }
    }
}
