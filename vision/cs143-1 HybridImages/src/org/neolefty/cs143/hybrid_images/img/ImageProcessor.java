package org.neolefty.cs143.hybrid_images.img;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import org.neolefty.cs143.hybrid_images.ui.ProcessorParam;

import java.awt.image.BufferedImage;
import java.util.Collection;

/** Alters an incoming image. */
public abstract class ImageProcessor implements HasProcessorParams {
    private ReadOnlyObjectWrapper<BufferedImage> processedImage;
//    private ReadOnlyObjectProperty<BufferedImage> processedImage;
    private SimpleObjectProperty<BufferedImage> unprocessedImage = new SimpleObjectProperty<>();

    public ImageProcessor() {
        processedImage = new ReadOnlyObjectWrapper<>();
        new ObjectBinding<BufferedImage>() {
            @Override
            protected BufferedImage computeValue() {
                return process(unprocessedImage.getValue());
            }
        };
    }

    /** What parameters does this processor have? May be null or empty if none. */
    @Override
    public Collection<ProcessorParam> getProcessorParams() {
        return null;
    }

    /** Implement this to do the processing. */
    public abstract BufferedImage process(BufferedImage original);

    @Override public String toString() { return getClass().getSimpleName(); }

    public BufferedImage getProcessedImage() { return processedImage.getValue(); }
    public BufferedImage getUnprocessedImage() { return unprocessedImage.getValue(); }

    public ReadOnlyObjectProperty<BufferedImage> processedImageProperty() {
        return processedImage.getReadOnlyProperty();
    }

    public ObjectProperty<BufferedImage> unprocessedImageProperty() {
        return unprocessedImage;
    }
}
