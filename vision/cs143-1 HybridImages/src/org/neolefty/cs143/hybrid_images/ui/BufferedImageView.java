package org.neolefty.cs143.hybrid_images.ui;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import org.neolefty.cs143.hybrid_images.ui.util.ProcessedBI;

/** View a ProcessedBufferedImage in JavaFX. */
public class BufferedImageView extends ImageView {
    private ReadOnlyObjectWrapper<ProcessedBI> processedImageProperty = new ReadOnlyObjectWrapper<>();

    public BufferedImageView() {
        super.setSmooth(true); // better quality scaling
        processedImageProperty.addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
//                GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
//                GraphicsDevice device = env.getDefaultScreenDevice();
//                GraphicsConfiguration conf = device.getDefaultConfiguration();
//                ImageCapabilities cap = newValue.getCapabilities(conf);
//                System.out.println("Accelerated? " + cap.isAccelerated() + " volatile? " + cap.isTrueVolatile());
                setImage(SwingFXUtils.toFXImage(newValue.getImage(), null));
            });
        });
    }

    public ObjectProperty<ProcessedBI> processedImageProperty() {
        return processedImageProperty;
    }

    public ReadOnlyObjectProperty<ProcessedBI> readOnlyProcessedImageProperty() {
        return processedImageProperty.getReadOnlyProperty();
    }
}
