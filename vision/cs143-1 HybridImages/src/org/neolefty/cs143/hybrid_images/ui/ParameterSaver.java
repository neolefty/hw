package org.neolefty.cs143.hybrid_images.ui;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import org.neolefty.cs143.hybrid_images.ui.util.PrefStuff;
import org.neolefty.cs143.hybrid_images.ui.util.ProcessedBI;

/** Saves image filter params to prefs, customized for individual files. */
public class ParameterSaver {
    private ReadOnlyObjectProperty<ProcessedBI> imageProperty;
    private PrefStuff prefBase;
    private double def;

    public ParameterSaver
            (PrefStuff prefBase, ProcessorParam param, ReadOnlyObjectProperty<ProcessedBI> imageProperty, double def)
    {
        this.prefBase = prefBase;
        this.imageProperty = imageProperty;
        this.def = def;

        // when the value of the parameter changes, save it as a preference
        param.addListener((observable, oldValue, newValue) -> savePref(newValue.doubleValue()));
        // when the file changes, load the value that was saved for it
        imageProperty.addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> param.set(loadPref()));
        });
    }

    private void savePref(double newValue) {
        PrefStuff curPref = getCurPref();
        if (curPref != null)
            curPref.putDouble(newValue);
    }

    public double loadPref() {
        PrefStuff curPref = getCurPref();
        return curPref == null ? def : curPref.getDouble(def);
    }

    private PrefStuff getCurPref() {
        ProcessedBI image = imageProperty.getValue();
        return image == null ? null : prefBase.createChild(image.toString(".", true));
    }
}
