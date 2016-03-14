package org.neolefty.cs143.hybrid_images.ui;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;

/** Control for a {@link ProcessorParam}. */
public class ParamSlider extends Slider {
    private ProcessorParam param;
    private static final Object sync = new Object();

    private ChangeListener<? super Number> sliderListener, paramListener;

    public ParamSlider(ProcessorParam param) {
        super();
        this.param = param;

        setMax(param.getMax());
        setMin(param.getMin());
        setValue(param.getDefault());
        setShowTickMarks(true);
        setShowTickLabels(true);

        Tooltip.install(this, new Tooltip(param.getName()));

        if (param.isInteger()) {
            setBlockIncrement(1);
            setMajorTickUnit(1);
            setMinorTickCount(0);
            setSnapToTicks(true);
        }
        else {
            setBlockIncrement(getRange() / 10);
            setMajorTickUnit(getRange() / 10);
            setMinorTickCount(10);
            setSnapToTicks(false);
        }

//        sliderListener = (observable, oldValue, newValue) -> {
//            synchronized (sync) {
//                param.removeListener(paramListener);  // avoid event loop
//                param.setValue(bound(newValue.doubleValue()));
//                param.addListener(paramListener);
//            }
//        };
//        paramListener = (observable, oldValue, newValue) -> {
//            synchronized (sync) {
//                valueProperty().removeListener(sliderListener); // avoid event loop
//                valueProperty().setValue(bound(newValue.doubleValue()));
//                valueProperty().addListener(sliderListener);
//            }
//        };
//        valueProperty().addListener(sliderListener);
//        param.addListener(paramListener);

        valueProperty().bindBidirectional(param);
    }

    public double getRange() { return getMax() - getMin(); }

    private double bound(double x) {
        x = Math.min(param.getMax(), x);
        return Math.max(param.getMin(), x);
    }
}
