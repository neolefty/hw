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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/** Convert to BoofCV MultiSpectral ImageUInt8 and do something. Uses ThreadPool to process RGB bands separately. */
public class BoofProcessor extends ImageProcessor implements HasDebugWindow {
    private final ExecutorService exec;
    private Function function;
    private ReadOnlyObjectWrapper<JComponent> debugWindowProperty = new ReadOnlyObjectWrapper<>();

    public BoofProcessor(Function function, ExecutorService exec) {
        this.function = function;
        if (function != null && function instanceof HasDebugWindow)
            debugWindowProperty.bind(((HasDebugWindow) function).debugWindowProperty());
        this.exec = exec;
    }

    @Override
    public BufferedImage process(Collection<BufferedImage> originals) {
        Stopwatch watch = new Stopwatch();
        try {
            List<MultiSpectral<ImageUInt8>> boofIns = new ArrayList<>();
            int maxW = 0, maxH = 0;
            for (BufferedImage original : originals) {
                int w = original.getWidth(), h = original.getHeight();
                maxW = Math.max(w, maxW);
                maxH = Math.max(h, maxH);
                int bands = original.getRaster().getNumBands();
                MultiSpectral<ImageUInt8> boofImage = new MultiSpectral<>(ImageUInt8.class, w, h, bands);
                ConvertBufferedImage.convertFrom(original, boofImage, true);
                boofIns.add(boofImage);
            }

            watch.mark("convert to boof");

            MultiSpectral<ImageUInt8> processed = new MultiSpectral<>(ImageUInt8.class, maxW, maxH, 3);
            CountDownLatch latch = new CountDownLatch(3); // could use ExecutorService.invokeAll() instead

            for (int i = 0; i < 3; ++i) {
                final int finalI = i;
                exec.execute(() -> {
                    try {
                        // one band at a time, across all inputs
                        List<ImageUInt8> bandInputs = new ArrayList<ImageUInt8>();
                        for (MultiSpectral<ImageUInt8> multiIn : boofIns)
                            bandInputs.add(multiIn.getBand(finalI));
                        function.apply(bandInputs, processed.getBand(finalI), finalI);
                        watch.mark("band " + finalI);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            // wait for all to complete
            try {
                latch.await();

                BufferedImage result = ConvertBufferedImage.convertTo(processed, null, true);
                watch.mark("convert back");
//            double nsPerPixel = (watch.getElapsed() * 1000000) / (w * h);
//            System.out.println(toString() + " - " + watch + " - " + nsPerPixel + " ns per pixel"
//                    + " - " + w + "x" + h + " " + toString());
                return result;
            } catch(InterruptedException ignored) {
                System.out.print(".");
                return null;
            }
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
        /** Process a color plane of the images. Put the result into pre-allocated <tt>out</tt>. */
        void apply(Collection<ImageUInt8> inputs, ImageUInt8 output, int index);
    }
}
