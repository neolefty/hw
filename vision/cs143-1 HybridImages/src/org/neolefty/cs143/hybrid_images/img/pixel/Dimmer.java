package org.neolefty.cs143.hybrid_images.img.pixel;

import org.neolefty.cs143.hybrid_images.img.HasProcessorParams;
import org.neolefty.cs143.hybrid_images.ui.ProcessorParam;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/** A simple dimmer. */
public class Dimmer implements IntToIntFunction, HasProcessorParams {
    private ProcessorParam param = new ProcessorParam("bright", 0.5, 0, 2, "Dimmer's brightness.");
    private List<ProcessorParam> params = Collections.singletonList(param);
    private int bright = 127; // 0 to 255

    public Dimmer() {
        param.addListener((observable, oldValue, newValue) -> {
            bright = (int) (newValue.doubleValue() * 255);
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
        return Math.max(0, Math.min(255, x * bright / 255));
    }

    @Override public Collection<ProcessorParam> getProcessorParams() { return params; }
    @Override public String toString() { return "Dimmer"; }
}
