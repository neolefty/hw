package org.neolefty.cs143.hybrid_images.ui.util;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

/** Reminds contents to shrink, which sometimes they forget to do, by setting min dimensions on resize. */
public class StrictHBox extends HBox {
    public StrictHBox(Region... children) {
        super(children);
        widthProperty().addListener((observable, oldValue, newValue) -> {
            for (Region child : children)
                child.setMinWidth(newValue.doubleValue() / children.length);
        });
        heightProperty().addListener((observable, oldValue, newValue) -> {
            for (Region child : children)
                child.setMinHeight(newValue.doubleValue());
        });
    }
}
