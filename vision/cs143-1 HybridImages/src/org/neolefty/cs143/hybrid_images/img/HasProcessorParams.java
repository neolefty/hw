package org.neolefty.cs143.hybrid_images.img;

import org.neolefty.cs143.hybrid_images.ui.ProcessorParam;

import java.util.Collection;

/** Something with parameters for image processing. */
public interface HasProcessorParams {
    /** What parameters does this processor have? May be null or empty if none. */
    Collection<ProcessorParam> getProcessorParams();
}
