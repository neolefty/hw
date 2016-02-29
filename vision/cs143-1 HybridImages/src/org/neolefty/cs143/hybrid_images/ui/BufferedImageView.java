package org.neolefty.cs143.hybrid_images.ui;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;

/** View a BufferedImage in JavaFX. */
public class BufferedImageView extends ImageView {
    private ObjectProperty<BufferedImage> imageProperty = new SimpleObjectProperty<>();

    public BufferedImageView() {
        super.setSmooth(true); // better quality scaling
        imageProperty.addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
//                GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
//                GraphicsDevice device = env.getDefaultScreenDevice();
//                GraphicsConfiguration conf = device.getDefaultConfiguration();
//                ImageCapabilities cap = newValue.getCapabilities(conf);
//                System.out.println("Accelerated? " + cap.isAccelerated() + " volatile? " + cap.isTrueVolatile());
                setImage(SwingFXUtils.toFXImage(newValue, null));
            });
        });
    }

    public ObjectProperty<BufferedImage> bufferedImageProperty() {
        return imageProperty;
    }
}
