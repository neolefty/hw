package org.neolefty.cs143.hybrid_images.ui;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.layout.VBox;
import org.neolefty.cs143.hybrid_images.img.HasProcessorParams;
import org.neolefty.cs143.hybrid_images.ui.util.PrefStuff;
import org.neolefty.cs143.hybrid_images.ui.util.ProcessedBI;

/** Controls {@link ProcessorParam}s. */
public class ProcessorControlView extends VBox {
    private final ReadOnlyObjectProperty<ProcessedBI> imageProperty;
    private final PrefStuff pref;

    public ProcessorControlView
            (PrefStuff pref,
             ReadOnlyObjectProperty<? extends HasProcessorParams> processorProperty,
             ReadOnlyObjectProperty<ProcessedBI> imageProperty)
    {
        this.pref = pref;
        this.imageProperty = imageProperty;
        processorProperty.addListener((observable, oldValue, newValue) -> update(newValue));
        update(processorProperty.getValue());
    }

    private void update(HasProcessorParams value) {
        getChildren().clear();
        if (value != null && value.getProcessorParams() != null) {
            for (ProcessorParam param : value.getProcessorParams()) {
                getChildren().add(new ParamSlider(param));
                new ParameterSaver(pref, param, imageProperty, param.getDefault());
            }
        }
    }
}
