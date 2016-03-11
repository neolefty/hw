package org.neolefty.cs143.hybrid_images.ui;

import javafx.beans.property.ObjectProperty;
import org.neolefty.cs143.hybrid_images.ui.util.ProcessedBI;

public interface HasBufferedImageProperty {
    ObjectProperty<ProcessedBI> imageProperty();
}
