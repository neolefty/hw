package org.neolefty.cs143.hybrid_images.img.boof;

import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.MultiSpectral;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import org.neolefty.cs143.hybrid_images.img.HasProcessorParams;
import org.neolefty.cs143.hybrid_images.img.ImageProcessor;
import org.neolefty.cs143.hybrid_images.ui.HasDebugWindow;
import org.neolefty.cs143.hybrid_images.ui.ProcessorParam;
import org.neolefty.cs143.hybrid_images.util.Stopwatch;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/** Convert to BoofCV MultiSpectral ImageUInt8 and do something. Uses ThreadPool to process RGB bands separately. */
public class Boof8Processor extends ImageProcessor implements HasDebugWindow {
    private Function function;
    private ExecutorService threadPool;
    private ReadOnlyObjectWrapper<JComponent> debugWindowProperty = new ReadOnlyObjectWrapper<>();

    public Boof8Processor(Function function, ExecutorService threadPool) {
        this.function = function;
        if (function != null && function instanceof HasDebugWindow)
            debugWindowProperty.bind(((HasDebugWindow) function).debugWindowProperty());
        if (threadPool == null)
            threadPool = Executors.newSingleThreadExecutor();
        this.threadPool = threadPool;
    }

    // the operations that are unresolved
    private final Set<Future> inFlight = Collections.synchronizedSet(new HashSet<>());

    @Override
    public BufferedImage process(BufferedImage original) {
        Stopwatch watch = new Stopwatch();
        try {
            int w = original.getWidth(), h = original.getHeight();

            int bands = original.getRaster().getNumBands();
            MultiSpectral<ImageUInt8> boofImage = new MultiSpectral<>(ImageUInt8.class, w, h, bands);
            ConvertBufferedImage.convertFrom(original, boofImage, true);
            watch.mark("convert to boof");

            MultiSpectral<ImageUInt8> processed = new MultiSpectral<>(ImageUInt8.class, w, h, 3);
            CountDownLatch latch = new CountDownLatch(3); // could use ExecutorService.invokeAll() instead

            // cancel any stale operations
            synchronized (inFlight) {
                if (!inFlight.isEmpty())
                    System.out.println(inFlight.size() + " in flight -- " + toString());
                while (!inFlight.isEmpty()) {
                    Future f = inFlight.iterator().next();
                    f.cancel(false);
                    inFlight.remove(f);
                }
//                inFlight.removeIf(future -> { // this seems clever enough to be confusing
//                    future.cancel(true);
//                    return true;
//                });
            }

            Future[] futures = new Future[3];
            for (int i = 0; i < 3; ++i) {
                final int finalI = i;
                futures[i] = threadPool.submit(() -> {
                    try {
                        function.apply(boofImage.getBand(finalI), processed.getBand(finalI), finalI);
                        watch.mark("band " + finalI);
                    } catch(Exception e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                        inFlight.remove(futures[finalI]);
                    }
                });
                inFlight.add(futures[i]);
            }

            // wait for all to complete
            latch.await();

            BufferedImage result = ConvertBufferedImage.convertTo(processed, null, true);
            watch.mark("convert back");
            double nsPerPixel = (watch.getElapsed() * 1000000) / (w * h);
            System.out.println(toString() + " - " + watch + " - " + nsPerPixel + " ns per pixel"
                    + " - " + w + "x" + h + " " + toString());
            return result;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Collection<ProcessorParam> getProcessorParams() {
        return function.getProcessorParams();
    }

    @Override
    public ReadOnlyObjectProperty<JComponent> debugWindowProperty() {
        return debugWindowProperty.getReadOnlyProperty();
    }

    @Override
    public String toString() {
        return function.toString();
    }

    public interface Function extends HasProcessorParams {
        /** Process a color plane of the image. Put the result into pre-allocated <tt>out</tt>. */
        void apply(ImageUInt8 in, ImageUInt8 out, int index);
    }
}
