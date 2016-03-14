package org.neolefty.cs143.hybrid_images.ui;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.FlowPane;
import org.neolefty.cs143.hybrid_images.img.ImageProcessor;
import org.neolefty.cs143.hybrid_images.ui.util.PrefStuff;
import org.neolefty.cs143.hybrid_images.ui.util.ProcessedBI;
import org.neolefty.cs143.hybrid_images.util.CancellingExecutor;

import java.util.Collection;

/** A view of a processed image that lets you pick which processor to use. */
public class ChooseProcessorView extends ProcessedImageView {
    public ChooseProcessorView
            (PrefStuff pref, Collection<ImageProcessor> processors,
             Collection<ObjectProperty<ProcessedBI>> sources, CancellingExecutor uiExec)
    {
        super(pref, null, sources, uiExec);
        ImageProcessorHistory history = new ImageProcessorHistory(pref, processors);
        setProcessor(history.getTop());

        if (processors.size() > 1) {
            ImageProcessorMenu menu = new ImageProcessorMenu(history);
            menu.valueProperty().addListener((observable, oldValue, newValue) -> setProcessor(newValue.getProcessor()));
            // place the menu at the bottom
            FlowPane controls = new FlowPane();
            controls.setAlignment(Pos.BOTTOM_CENTER);
            controls.getChildren().add(menu);
            if (getControlPane().getBottom() != null) throw new IllegalStateException();
            getControlPane().setBottom(controls);
        }
    }
}
