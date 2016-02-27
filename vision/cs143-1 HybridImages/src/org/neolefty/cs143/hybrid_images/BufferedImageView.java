package org.neolefty.cs143.hybrid_images;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;

/** View a BufferedImage in JavaFX. */
public class BufferedImageView extends ImageView {
    private ObjectProperty<BufferedImage> imageProperty = new SimpleObjectProperty<>();

    public BufferedImageView() {
        imageProperty.addListener((observable, oldValue, newValue) -> {
            setImage(SwingFXUtils.toFXImage(newValue, null));
        });
    }

//    public void setBufferedImage(BufferedImage image) {
//        imageProperty.setValue(image);
//    }
//
//    public BufferedImage getBufferedImage() {
//        return imageProperty.get();
//    }

    public ObjectProperty<BufferedImage> bufferedImageProperty() {
        return imageProperty;
    }
}
