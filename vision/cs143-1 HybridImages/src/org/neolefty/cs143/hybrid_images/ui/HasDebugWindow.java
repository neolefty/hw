package org.neolefty.cs143.hybrid_images.ui;

import javafx.beans.property.ReadOnlyObjectProperty;

import javax.swing.*;

/** Has a property that shows debug info in a Swing panel. */
public interface HasDebugWindow {
    /** A property pointing to the debug window. */
    ReadOnlyObjectProperty<JComponent> debugWindowProperty();
}
