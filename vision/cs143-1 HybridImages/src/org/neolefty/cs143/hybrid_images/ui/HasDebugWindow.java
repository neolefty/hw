package org.neolefty.cs143.hybrid_images.ui;

import boofcv.alg.transform.fft.DiscreteFourierTransformOps;
import boofcv.gui.image.VisualizeImageData;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.InterleavedF32;
import javafx.beans.property.ReadOnlyObjectProperty;

import javax.swing.*;
import java.awt.image.BufferedImage;

/** Has a property that shows debug info in a Swing panel. */
public interface HasDebugWindow {
    /** A property pointing to the debug window. */
    ReadOnlyObjectProperty<JComponent> debugWindowProperty();

}
