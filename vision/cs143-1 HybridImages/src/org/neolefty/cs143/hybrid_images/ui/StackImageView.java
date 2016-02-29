package org.neolefty.cs143.hybrid_images.ui;

import javafx.beans.property.ObjectProperty;
import javafx.scene.layout.StackPane;

import java.awt.image.BufferedImage;

/** Displays a BufferedImage as the bottom layer of a StackPane. */
public class StackImageView extends StackPane implements HasBufferedImageProperty {
    private BufferedImageView imageView;

    public StackImageView() {
        imageView = new BufferedImageView();
        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(widthProperty());
        imageView.fitHeightProperty().bind(heightProperty());
        getChildren().add(imageView);
    }

    public ObjectProperty<BufferedImage> bufferedImageProperty() {
        return imageView.bufferedImageProperty();
    }

    public void setImage(BufferedImage image) {
        bufferedImageProperty().setValue(image);
    }
}
