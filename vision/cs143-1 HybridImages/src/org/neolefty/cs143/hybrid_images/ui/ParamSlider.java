package org.neolefty.cs143.hybrid_images.ui;

import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;

/** Control for a {@link ProcessorParam}. */
public class ParamSlider extends Slider {
    private ProcessorParam param;
    private static final Object sync = new Object();

//    private ChangeListener<? super Number> sliderListener, paramListener;

    public ParamSlider(ProcessorParam param) {
        super();
        double max = param.getMax(), min = param.getMin(), range = max - min;
        setMax(max);
        setMin(min);
        setValue(param.getDefault());
        setShowTickMarks(true);
        setShowTickLabels(true);
        if (param.isInteger()) {
            setBlockIncrement(1);
            setMajorTickUnit(1);
            setMinorTickCount(0);
            setSnapToTicks(true);
        }
        else {
            setBlockIncrement(range / 20);
            setMajorTickUnit(range / 5);
            setMinorTickCount(4);
            setSnapToTicks(false);
        }
        setBlockIncrement(param.isInteger() ? 1 : range / 20);
        this.param = param;
        Tooltip.install(this, new Tooltip(param.getName()));

//        sliderListener = (observable, oldValue, newValue) -> {
//            synchronized (sync) {
//                param.removeListener(paramListener);  // avoid event loop
//                param.setValue(bound(newValue.doubleValue()));
//                param.addListener(paramListener);
//            }
//        };
//
//        paramListener = (observable, oldValue, newValue) -> {
//            synchronized (sync) {
//                valueProperty().removeListener(sliderListener); // avoid event loop
//                valueProperty().setValue(bound(newValue.doubleValue()));
//                valueProperty().addListener(sliderListener);
//            }
//        };

        valueProperty().bindBidirectional(param);

//        valueProperty().addListener(sliderListener);
//        param.addListener(paramListener);
    }

//    private double bound(double x) {
//        x = Math.min(param.getMax(), x);
//        return Math.max(param.getMin(), x);
//    }
}
