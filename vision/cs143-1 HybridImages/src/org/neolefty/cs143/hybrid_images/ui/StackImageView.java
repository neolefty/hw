package org.neolefty.cs143.hybrid_images.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.neolefty.cs143.hybrid_images.ui.util.ProcessedBI;

/** Displays a BufferedImage as the bottom layer of a StackPane. */
public class StackImageView extends StackPane implements HasBufferedImageProperty {
    private BufferedImageView imageView;
    private BorderPane controlPane;

    public StackImageView() {
        imageView = new BufferedImageView();
        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(widthProperty());
        imageView.fitHeightProperty().bind(heightProperty());
        getChildren().add(imageView);
        controlPane = new BorderPane();
        getChildren().add(controlPane);
    }

    /** The top pane in the stack, where interactive controls live. */
    public BorderPane getControlPane() { return controlPane; }

    public ObjectProperty<ProcessedBI> imageProperty() {
        return imageView.processedImageProperty();
    }

    public ReadOnlyObjectProperty<ProcessedBI> readOnlyImageProperty() {
        return imageView.readOnlyProcessedImageProperty();
    }

    public void setImage(ProcessedBI image) {
        imageProperty().setValue(image);
    }
}
