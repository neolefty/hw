package org.neolefty.cs143.hybrid_images.img;

import boofcv.struct.image.ImageFloat32;
import org.neolefty.cs143.hybrid_images.util.Stopwatch;

/** Generates Butterworth filters. */
public class ButterworthGenerator extends FilterGenerator {
    private int order;

    /** Create a Butterworth filter generator.
     *  @param fraction The fraction of area in the center of the FFT, between 0 and 1.
     *                  Smaller numbers for a smaller circle.
     *  @param order What order of Butterworth filter (a positive integer). */
    public ButterworthGenerator(double fraction, Type type, int order) {
        super(type, fraction);
        this.order = order;
    }

    // TODO: consider using a thread pool
    @Override
    public ImageFloat32 generate(int w, int h) {
        Stopwatch watch = new Stopwatch(true);
// How to hack these so that odd works? Maybe two separate values, half & halfMinus1?
//        int wHalf = (w % 2 == 0) ? w / 2 : (w + 1) / 2;
//        int hHalf = (h % 2 == 0) ? h / 2 : (h + 1) / 2;
        int wHalf = w / 2, hHalf = h / 2;
        ImageFloat32 result = new ImageFloat32(w, h);
        // take advantage of symmetry
        float r2 = (float) (computeRadius(w, h) * computeRadius(w, h));
        for (int y = 0; y < hHalf; ++y) {
            for (int x = 0; x < wHalf; ++x) {
                float z = coeff2(x, y, r2);
                result.set(wHalf - x, hHalf - y, z);
                result.set(wHalf - x, hHalf + y, z);
                result.set(wHalf + x, hHalf + y, z);
                result.set(wHalf + x, hHalf - y, z);
            }
        }
        return result;
    }

    private float coeff2(int x, int y, float r2) {
        float w = isLowPass() ? (x*x + y*y) / r2 : r2 / (x*x + y*y);
        //noinspection StatementWithEmptyBody
        if (order != 1) {
            float w2 = w * w;
            switch (order) {
                case 2: w = w2; break;
                case 3: w = w2 * w; break;
                default: {
                    float w4 = w2 * w2;
                    switch (order) {
                        case 4: w = w4; break;
                        case 5: w = w4 * w; break;
                        case 6: w = w4 * w2; break;
                        case 7: w = w4 * w2 * w; break;
                        case 8: w = w4 * w4; break;
                        case 9: w = w4 * w4 * w; break;
                        case 10: w = w4 * w4 * w2; break;
                        default: w = (float) Math.pow(w, order);
                    }
                }
            }
        }
        return (float) (1. / Math.sqrt(1 + w));
    }

    @Override
    public String toString() {
        return "Butterworth " + (isLowPass() ? "low" : "high") + "-pass " + getFraction() + " [" + order + "]";
    }
}
