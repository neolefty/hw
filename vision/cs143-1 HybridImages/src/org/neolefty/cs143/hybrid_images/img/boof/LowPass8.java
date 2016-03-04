package org.neolefty.cs143.hybrid_images.img.boof;

import boofcv.abst.transform.fft.DiscreteFourierTransform;
import boofcv.alg.filter.blur.BlurImageOps;
import boofcv.alg.misc.PixelMath;
import boofcv.alg.transform.fft.DiscreteFourierTransformOps;
import boofcv.core.image.ConvertImage;
import boofcv.gui.image.VisualizeImageData;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.InterleavedF32;
import org.neolefty.cs143.hybrid_images.util.Stopwatch;

import java.awt.*;
import java.awt.image.BufferedImage;

/** Low-pass filter an image. Works best if x & y are powers of 2. */
public class LowPass8 implements Boof8Processor.Function {
    // how much of the FFT area do we remove? 0 to 1
    private double fraction;
    private double filterBlur;

    /** Construct a new low-pass filter.
     *  @param fraction The fraction of area to conserve in the FFT, between 0 and 1.
     *                  Smaller numbers for heavier filtering.
     *  @param filterBlur How much to blur the filter, to cut down on ringing, as a fraction of the filter.
     *                    For example, if the filter radius ends up being 20, and the filterBlur is 5, then we
     *                    apply a 20/5 = 4-pixel gaussian blur to the filter before using it.
     *                    Higher numbers = less blurring, more ringing. */
    public LowPass8(double fraction, double filterBlur) {
        this.fraction = fraction;
        this.filterBlur = filterBlur;
    }

    /** Construct a new low-pass filter, with an unsoftened cutoff, so there will be ringing.
     *  @param fraction The fraction of area to conserve in the FFT, between 0 and 1.
     *                  Smaller numbers for heavier filtering. */
    public LowPass8(double fraction) {
        this(fraction, Double.MAX_VALUE);
    }

    @Override
    public void apply(ImageUInt8 orig8, ImageUInt8 result8, int index) {
        int w = orig8.getWidth(), h = orig8.getHeight();
        Stopwatch watch = new Stopwatch();

        // make a float copy
        ImageFloat32 orig32 = new ImageFloat32(w, h);
        ConvertImage.convert(orig8, orig32);
        PixelMath.divide(orig32, 255f, orig32); // rescale image brightness 0 to 1
        watch.mark("float copy");

        // filter template: a circle
        BufferedImage spatialFilterBI = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        // to simplify the math, assume that the circle doesn't reach the edge of the image.
        // circle area / rectangle area = fraction --> solve for radius
        double r = Math.sqrt(fraction * w * h / Math.PI);
        Graphics g = spatialFilterBI.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, w, h);
        g.setColor(Color.WHITE);
        int rd = (int) r;
        g.fillOval(w/2 - rd, h/2 - rd, rd + rd, rd + rd);
        ImageFloat32 spatialFilter32 = new ImageFloat32(w, h);
        // TODO try pure gray scale?
        ConvertBufferedImage.convertFrom(spatialFilterBI, spatialFilter32);
        int blurRadius = (int) (rd / filterBlur);
        if (blurRadius >= 2)
            spatialFilter32 = BlurImageOps.gaussian(spatialFilter32, null, -1, blurRadius, null);
        PixelMath.divide(spatialFilter32, 255f, spatialFilter32);
        watch.mark("make filter");

        // do the FFT to the image and the filter
        DiscreteFourierTransform<ImageFloat32, InterleavedF32> dft = DiscreteFourierTransformOps.createTransformF32();
        InterleavedF32 fftOrig32 = new InterleavedF32(w, h, 2);
        dft.forward(orig32, fftOrig32);
        watch.mark("dft");
//        InterleavedF32 fftFilter32 = new InterleavedF32(w, h, 2);
//        dft.forward(spatialFilter32, fftFilter32);
        watch.mark("dft filter");

        // TODO equalize brightness in output image?
        // apply the filter to the image's FFT
        InterleavedF32 fftResult32 = new InterleavedF32(w, h, 2);
        DiscreteFourierTransformOps.shiftZeroFrequency(fftOrig32, true); // shift because the filter is zero-centered
        DiscreteFourierTransformOps.multiplyRealComplex(spatialFilter32, fftOrig32, fftResult32);
        DiscreteFourierTransformOps.shiftZeroFrequency(fftResult32, false); // undo the shift
        DiscreteFourierTransformOps.shiftZeroFrequency(fftOrig32, false); // undo the shift
//        DiscreteFourierTransformOps.multiplyComplex(fftOrig32, fftFilter32, fftResult32);
        watch.mark("multiply filter");

        // reverse the fft to get back the filtered image
        ImageFloat32 result32 = new ImageFloat32(w, h);
        dft.inverse(fftResult32, result32);
        watch.mark("inverse dft");

        // convert back to 8-bit
        PixelMath.multiply(result32, 255f, result32); // restore original brightness
        ConvertImage.convert(result32, result8);
        watch.mark("byte copy");

//        if (index == 0) {
//            ImageFloat32 reconstruct32 = new ImageFloat32(w, h);
//            dft.inverse(fftOrig32, reconstruct32);
//            ImageGridPanel show = new ImageGridPanel(2, 3,
//                    viz(orig32, 1), vizMag(fftOrig32), viz(reconstruct32),
//                    viz(spatialFilter32, 1), vizMag(fftResult32), viz(result32)
//            );
//            ShowImages.showWindow(show, fraction + " / " + filterBlur + " - original / filter - fft / multiplied - reconstructed / result");
//            watch.mark("visualize");
//        }

        System.out.println(" --> " + watch);
    }

    private BufferedImage viz(ImageFloat32 image32) {
        return viz(image32, -1);
    }
    private BufferedImage viz(ImageFloat32 image32, double normalize) {
        return VisualizeImageData.grayMagnitude(image32, null, normalize);
    }

    public static BufferedImage vizMag(InterleavedF32 fft) {
        fft = fft.clone();
        DiscreteFourierTransformOps.shiftZeroFrequency(fft, true);
        ImageFloat32 viz32 = new ImageFloat32(fft.getWidth(), fft.getHeight());
        DiscreteFourierTransformOps.magnitude(fft, viz32);
        return VisualizeImageData.grayMagnitude(viz32, null, 50);
    }
}
