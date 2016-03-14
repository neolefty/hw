package org.neolefty.cs143.hybrid_images.ui;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingNode;
import javafx.scene.control.Tooltip;
import org.neolefty.cs143.hybrid_images.img.ImageProcessor;
import org.neolefty.cs143.hybrid_images.ui.util.PrefStuff;
import org.neolefty.cs143.hybrid_images.ui.util.ProcessedBI;
import org.neolefty.cs143.hybrid_images.util.CancellingExecutor;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Display an image that has been processed, with controls for the processor. */
public class ProcessedImageView extends StackImageView {
    private CancellingExecutor exec;
    private ChangeListener<Number> paramListener;
    Collection<ObjectProperty<ProcessedBI>> sources;

    Tooltip tip = new Tooltip();
    private ChangeListener<JComponent> debugVisualListener = (observable, oldValue, newValue) -> {
        SwingNode node = new SwingNode();
        node.setContent(newValue);
        Platform.runLater(() -> tip.graphicProperty().setValue(node));
    };

    // when the processor changes, reprocess the current image
    private ReadOnlyObjectWrapper<ImageProcessor> processorProperty = new ReadOnlyObjectWrapper<>();

    public ProcessedImageView(PrefStuff pref, ImageProcessor processor,
                              Collection<ObjectProperty<ProcessedBI>> sources,
                              CancellingExecutor exec)
    {
        this.exec = exec;
        this.sources = sources;
        Tooltip.install(this, tip);

        // whenever the processor is changed ...
        processorProperty.addListener((observable, oldValue, newValue) -> {
            // ... update the tooltip ...
            tip.setText(newValue == null ? "" : newValue.toString());
            if (oldValue != null && oldValue instanceof HasDebugWindow)
                ((HasDebugWindow) oldValue).debugWindowProperty().removeListener(debugVisualListener);
            if (newValue != null && newValue instanceof HasDebugWindow)
                ((HasDebugWindow) newValue).debugWindowProperty().addListener(debugVisualListener);
            else // doesn't have graphical debug info, so clear out the old one
                Platform.runLater(() -> tip.graphicProperty().setValue(null));
            // ... and reprocess the current inputs
            reprocess();
        });

        // initialize the processor
        setProcessor(processor);

        // whenever an input is changed, reprocess
        for (ObjectProperty<ProcessedBI> source : sources)
            source.addListener((observable, oldValue, newValue) -> {
                reprocess();
            });

        // parameters changed: reprocess inputs
        paramListener = (observable, oldValue, newValue) -> reprocess();

        // controls for processor parameters -- at top
        ProcessorControlView controlView = new ProcessorControlView
                (pref, processorProperty.getReadOnlyProperty(), readOnlyImageProperty());
        if (getControlPane().getTop() != null) throw new IllegalStateException();
        getControlPane().setTop(controlView);
    }

    /** Reprocess the current inputs and update the output image. */
    private void reprocess() {
        exec.submit(this, () -> {
            setImage(ProcessedBI.process(getProcessor(), getInputs()));
        });
    }

    public List<ProcessedBI> getInputs() {
        List<ProcessedBI> result = new ArrayList<>();
        for (ObjectProperty<ProcessedBI> source : sources)
            result.add(source.get());
        return result;
    }

    public ImageProcessor getProcessor() { return processorProperty.getValue(); }

    public void setProcessor(ImageProcessor processor) {
        // stop listening to the old processor's parameters
        ImageProcessor oldP = processorProperty.getValue();
        if (oldP != null && oldP.getProcessorParams() != null)
            for (ProcessorParam param : oldP.getProcessorParams())
                param.removeListener(paramListener);
        // use the new processor -- the change event will trigger reprocessing
        processorProperty.setValue(processor);
        // start listening to the new processor's parameters
        if (processor != null && processor.getProcessorParams() != null)
            for (ProcessorParam param : processor.getProcessorParams())
                param.addListener(paramListener);
    }
}
