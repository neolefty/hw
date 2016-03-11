package org.neolefty.cs143.hybrid_images.ui;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.FlowPane;
import org.neolefty.cs143.hybrid_images.img.ImageProcessor;
import org.neolefty.cs143.hybrid_images.ui.util.ProcessedBI;
import org.neolefty.cs143.hybrid_images.ui.util.PrefStuff;

import java.util.Collection;
import java.util.concurrent.ExecutorService;

/** A view of a processed image that lets you pick which processor to use. */
public class ChooseProcessorView extends ProcessedImageView {
    public ChooseProcessorView
            (PrefStuff pref, Collection<ImageProcessor> processors,
             ObjectProperty<ProcessedBI> source, ExecutorService executorService)
    {
        super(pref, null, source, executorService);
        ImageProcessorHistory history = new ImageProcessorHistory(pref, processors);
        ImageProcessorMenu menu = new ImageProcessorMenu(history);

        FlowPane controls = new FlowPane();
        controls.setAlignment(Pos.BOTTOM_CENTER);
        controls.getChildren().add(menu);
        menu.valueProperty().addListener((observable, oldValue, newValue) -> setImageProcessor(newValue.getProcessor()));
        setImageProcessor(history.getTop());
        if (getControlPane().getBottom() != null) throw new IllegalStateException();
        getControlPane().setBottom(controls);
    }
}