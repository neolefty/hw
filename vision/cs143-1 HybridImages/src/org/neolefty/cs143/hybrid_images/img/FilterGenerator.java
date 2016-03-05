package org.neolefty.cs143.hybrid_images.img;

/** Base class for low- and high-pass filter generators. */
public abstract class FilterGenerator implements Image32Generator {
    public enum Type {
        lowPass, highPass
    }

    // how much of the FFT area do we remove? 0 to 1
    private double fraction;
    private Type type;

    /** Create a filter generator
     *  @param type low-pass or high-pass
     *  @param fraction The fraction of area in the center of the FFT, between 0 and 1.
     *                  Smaller numbers for a smaller circle. */
    public FilterGenerator(Type type, double fraction) {
        this.type = type;
        this.fraction = fraction;
    }

    /** The fraction of area in the center of the FFT, between 0 and 1.
     *  Smaller numbers for a smaller circle (lower frequencies). */
    public double getFraction() { return fraction; }

    /** Low-pass or high-pass? */
    public Type getFilterType() { return type; }

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
        return Math.sqrt(fraction * w * h / Math.PI);
    }

    public boolean isLowPass() { return type == Type.lowPass; }

    @Override
    public String toString() {
        return getFilterType() + " " + getFraction();
    }
}
