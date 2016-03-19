package org.neolefty.cs143.hybrid_images.img.two;

import org.neolefty.cs143.hybrid_images.img.HasProcessorParams;
import org.neolefty.cs143.hybrid_images.ui.ProcessorParam;

import java.util.Collection;
import java.util.Collections;

/** Use this to add two images together, weighting one or the other. */
public class AddTwoWeighted implements BinaryIntFunction, HasProcessorParams {
    private ProcessorParam balance = new ProcessorParam("balance", 0, -1, 1,
            "Balance between first & second image. 0 means weight them equally.");
    private Collection<ProcessorParam> params = Collections.singleton(balance);

    /** Helper variables for calculations. */
    private double aCoefficient, bCoefficient;

    public AddTwoWeighted() {
        balance.addListener((observable, oldValue, newValue) -> updateCoefficients());
        updateCoefficients(); // initialize
    }

    /** Negative favors a, positive favors b. */
    private void updateCoefficients() {
        bCoefficient = (balance.doubleValue() + 1) * 0.5; // range from 0 to 1
        aCoefficient = 1 - bCoefficient; // range from 1 to 0
    }

    @Override
    public int apply(int a, int b) {
        return Math.min(255, Math.max(0, (int) (a * aCoefficient + b * bCoefficient)));
    }

    @Override
    public Collection<ProcessorParam> getProcessorParams() {
        return params;
    }

    @Override
    public String toString() {
        return "Add Two Weighted";
    }
}
