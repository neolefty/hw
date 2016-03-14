package org.neolefty.cs143.hybrid_images.img.pixel;

import org.neolefty.cs143.hybrid_images.img.HasProcessorParams;
import org.neolefty.cs143.hybrid_images.ui.ProcessorParam;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/** A simple dimmer. */
public class Dimmer implements IntToIntFunction, HasProcessorParams {
    private ProcessorParam brightParam = new ProcessorParam("bright", 1, -5, 5, "Dimmer's brightness.");
    private List<ProcessorParam> params = Collections.singletonList(brightParam);
    private int bright = 127; // 0 to 255
    private boolean negative = false;

    public Dimmer() {
        brightParam.addListener((observable, oldValue, newValue) -> {
            bright = Math.abs((int) (newValue.doubleValue() * 255));
            negative = newValue.doubleValue() < 0;
        });
    }

    @Override
    public int apply(int pixel) {
        return (applyChannel(pixel & 0xff)) // blue
                + ((applyChannel((pixel & 0xff00) >> 8)) << 8) // green
                + ((applyChannel((pixel & 0xff0000) >> 16)) << 16) // red
                + (pixel & 0xff000000); // alpha
    }

    private int applyChannel(int x) {
        int y = x * bright / 255;
        if (negative) y = bright - y;
        return Math.max(0, Math.min(255, y));
    }

    @Override public Collection<ProcessorParam> getProcessorParams() { return params; }
    @Override public String toString() { return "Dimmer"; }
}
