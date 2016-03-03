package org.neolefty.cs143.hybrid_images.img.boof;

import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.MultiSpectral;
import org.neolefty.cs143.hybrid_images.img.ImageProcessor;
import org.neolefty.cs143.hybrid_images.util.Stopwatch;

import java.awt.image.BufferedImage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Convert to BoofCV MultiSpectral ImageUInt8 and do something. Uses ThreadPool to process RGB bands separately. */
public class Boof8Processor extends ImageProcessor {
    private Function function;
    private ExecutorService threadPool;

    public Boof8Processor(Function function, ExecutorService threadPool) {
        this.function = function;
        if (threadPool == null)
            threadPool = Executors.newSingleThreadExecutor();
        this.threadPool = threadPool;
    }

    @Override
    public BufferedImage process(BufferedImage original) {
        Stopwatch watch = new Stopwatch();
        try {
            int w = original.getWidth(), h = original.getHeight();

            int bands = original.getRaster().getNumBands();
            int r = 0, g = 1, b = 2; // alpha is band 3, if it is present
            MultiSpectral<ImageUInt8> boofImage = new MultiSpectral<>(ImageUInt8.class, w, h, bands);
            watch.mark("create boof");

            ConvertBufferedImage.convertFrom(original, boofImage, true);
            watch.mark("convert to boof");

//            BufferedImage redBI =  ConvertBufferedImage.convertTo(boofImage.getBand(r), null);
//            BufferedImage greenBI =  ConvertBufferedImage.convertTo(boofImage.getBand(g), null);
//            BufferedImage blueBI =  ConvertBufferedImage.convertTo(boofImage.getBand(b), null);
//            if (bands == 4) {
//                BufferedImage alphaBI = ConvertBufferedImage.convertTo(boofImage.getBand(0), null);
//                ShowImages.showWindow(new ImageGridPanel(2, 2, redBI, greenBI, blueBI, alphaBI), "RGBA");
//            }
//            else
//                ShowImages.showWindow(new ImageGridPanel(2, 2, redBI, greenBI, blueBI), "RGB");

            MultiSpectral<ImageUInt8> processed = new MultiSpectral<>(ImageUInt8.class, w, h, 3);
            // which band is which in the converted image?
            CountDownLatch latch = new CountDownLatch(3);
            for (int i = 0; i < 3; ++i) {
                final int finalI = i;
                threadPool.submit(() -> {
                    function.apply(boofImage.getBand(finalI), processed.getBand(finalI), finalI);
                    latch.countDown();
                    watch.mark("band " + finalI);
                });
            }
            latch.await();

            BufferedImage result = ConvertBufferedImage.convertTo(processed, null, true);
            watch.mark("convert back");
            double nsPerPixel = (watch.getElapsed() * 1000000) / (w * h);
            System.out.println(getName() + " - " + watch + " - " + nsPerPixel + " ns per pixel" + " - " + getName());
            return result;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getName() {
        return "BoofCV " + function.getClass().getSimpleName();
    }

    public interface Function {
        /** Process a color plane of the image. Put the result into pre-allocated <tt>out</tt>. */
        void apply(ImageUInt8 in, ImageUInt8 out, int index);
    }
}