package org.neolefty.cs143.hybrid_images.ui;

import javafx.beans.property.ObjectProperty;

import java.awt.image.BufferedImage;

public interface HasBufferedImageProperty {
    ObjectProperty<BufferedImage> bufferedImageProperty();
}
