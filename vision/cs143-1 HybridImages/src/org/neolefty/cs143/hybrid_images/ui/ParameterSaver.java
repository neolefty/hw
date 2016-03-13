package org.neolefty.cs143.hybrid_images.ui;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import org.neolefty.cs143.hybrid_images.ui.ProcessorParam;
import org.neolefty.cs143.hybrid_images.ui.util.PrefStuff;
import org.neolefty.cs143.hybrid_images.ui.util.ProcessedBI;

/** Saves image filter params to prefs, customized for individual files. */
public class ParameterSaver {
    private ReadOnlyObjectProperty<ProcessedBI> imageProperty;
    private ProcessorParam param;
    private PrefStuff pref;
    private double def;

    public ParameterSaver
            (PrefStuff pref, ProcessorParam param, ReadOnlyObjectProperty<ProcessedBI> imageProperty, double def)
    {
        this.pref = pref;
        this.imageProperty = imageProperty;
        this.def = def;
        this.param = param;

        // when the value of the parameter changes, save it as a preference
        param.addListener((observable, oldValue, newValue) -> savePref(newValue.doubleValue()));
        // when the file changes, load the value that was saved for it
        imageProperty.addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> param.set(loadPref()));
        });
    }

    private void savePref(double newValue) {
        getCurPref().putDouble(newValue);
    }

    public double loadPref() {
        return getCurPref().getDouble(def);
    }

    private PrefStuff getCurPref() {
        return pref.createChild(imageProperty.getValue().toString(".", true));
    }
}
