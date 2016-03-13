package org.neolefty.cs143.hybrid_images.img;

import org.neolefty.cs143.hybrid_images.ui.ProcessorParam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/** Base class for low- and high-pass filter generators. */
public abstract class FilterGenerator implements Image32Generator {
    public enum Type {
        lowPass, highPass
    }

    // how much of the FFT area do we remove? 0 to 1
    private ProcessorParam fraction = new ProcessorParam
            ("fraction", 0.2, -1, 1,
                    "How strong is the filter? Negative for high-pass, positive for low-pass.");

    private List<ProcessorParam> params = Collections.singletonList(fraction);

    protected void addParam(ProcessorParam param) {
        List<ProcessorParam> tmp = new ArrayList<>(params);
        tmp.add(param);
        params = Collections.unmodifiableList(tmp);
    }

    /** The fraction of area in the circle in the center of the FFT.
     *  Smaller numbers for a smaller circle (lower frequencies affected).
     *  Negative is high-pass, positive is low-pass.
     *  Simplification: Ignore the portion of the circle outside the FFT rectangle. */
    public double getFraction() { return fraction.doubleValue(); }

    /** Low-pass or high-pass? */
    public Type getFilterType() {
        return fraction.doubleValue() <= 0 ? Type.highPass : Type.lowPass;
    }

    @Override public Collection<ProcessorParam> getProcessorParams() { return params; }

    /** Compute the radius of this filter for a given FFT size, based on this filter's {@link #getFraction}. */
    public double computeRadius(int w, int h) {
        // To simplify the math, assume that the circle doesn't reach the edge of the image.
        // Solve for radius:
        //
        //     circle area / rectangle area = fraction
        //
        // But if it does reach the edge, it degrades gracefully: the circle will truncate,
        // and the fraction will no longer be accurate, but it will still work.
        //
        // In the extreme case, if the circle completely covers the image,
        // the result will be an all-pass filter.
        double f = Math.abs(fraction.doubleValue());
        return Math.sqrt(f * w * h / Math.PI);
    }

    public boolean isLowPass() { return getFilterType() == Type.lowPass; }

    @Override
    public String toString() {
        return getFilterType() + " " + getFraction();
    }
}
