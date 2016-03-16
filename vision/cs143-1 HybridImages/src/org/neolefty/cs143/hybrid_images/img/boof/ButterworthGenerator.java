package org.neolefty.cs143.hybrid_images.img.boof;

import boofcv.struct.image.ImageFloat32;
import org.neolefty.cs143.hybrid_images.ui.ProcessorParam;

/** Generates Butterworth filters. */
public class ButterworthGenerator extends FilterGenerator {
    private ProcessorParam order
            = new ProcessorParam("order", 1, 1, 12, true, "What order of Butterworth filter?");

    /** Create a Butterworth filter generator. */
    public ButterworthGenerator() {
        super.addParam(order);
    }

    @Override
    public ImageFloat32 generate(int w, int h) {
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
        int rdr = order.intValue();
        //noinspection StatementWithEmptyBody
        if (rdr != 1) {
            float w2 = w * w;
            switch (rdr) {
                case 2: w = w2; break;
                case 3: w = w2 * w; break;
                default: {
                    float w4 = w2 * w2;
                    switch (rdr) {
                        case 4: w = w4; break;
                        case 5: w = w4 * w; break;
                        case 6: w = w4 * w2; break;
                        case 7: w = w4 * w2 * w; break;
                        case 8: w = w4 * w4; break;
                        case 9: w = w4 * w4 * w; break;
                        case 10: w = w4 * w4 * w2; break;
                        default: w = (float) Math.pow(w, rdr);
                    }
                }
            }
        }
        return (float) (1. / Math.sqrt(1 + w));
    }

//    @Override
//    public String toString() {
//        return "Butterworth " + (isLowPass() ? "low" : "high") + "-pass " + getFraction() + " [" + order + "]";
//    }

    @Override public String toString() { return "Butterworth"; }
}
