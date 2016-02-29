package org.neolefty.cs143.hybrid_images;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;

import java.awt.image.BufferedImage;

/** Processed image -- alters an incoming image. */
public abstract class ImageProcessor {
    private ReadOnlyObjectWrapper<BufferedImage> processedImage;
//    private ReadOnlyObjectProperty<BufferedImage> processedImage;
    private SimpleObjectProperty<BufferedImage> unprocessedImage = new SimpleObjectProperty<>();

    public ImageProcessor() {
        processedImage = new ReadOnlyObjectWrapper<>();
//        processedImage = new ReadOnlyObjectWrapper<BufferedImage>() {
//            private BufferedImage processedImage = null;
//            @Override
//            protected void invalidated() {
//                System.out.println("Processed image - invalidated");
//                processedImage = null;
//            }
//
//            @Override
//            public BufferedImage getValue() {
//                if (processedImage == null)
//                    processedImage = process(unprocessedImage.getValue());
//                return processedImage;
//            }
//
//            @Override
//            public BufferedImage get() {
//                return getValue();
//            }
//        };

//        processedImage.bind(unprocessedImage);
        new ObjectBinding<BufferedImage>() {
            @Override
            protected BufferedImage computeValue() {
                return process(unprocessedImage.getValue());
            }
        };
    }

    /** Implement this to do the processing. */
    public abstract BufferedImage process(BufferedImage original);

    public BufferedImage getProcessedImage() { return processedImage.getValue(); }
    public BufferedImage getUnprocessedImage() { return unprocessedImage.getValue(); }

    public ReadOnlyObjectProperty<BufferedImage> processedImageProperty() {
        return processedImage.getReadOnlyProperty();
    }

    public ObjectProperty<BufferedImage> unprocessedImageProperty() {
        return unprocessedImage;
    }
}
