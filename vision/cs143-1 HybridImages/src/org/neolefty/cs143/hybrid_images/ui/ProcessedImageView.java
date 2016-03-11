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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Display an image that has been processed, with controls for the processor. */
public class ProcessedImageView extends StackImageView {
    private ExecutorService ex;
    private ChangeListener<Number> paramListener;

    // when the processor changes, reprocess the current image
    private ReadOnlyObjectWrapper<ImageProcessor> processorProperty = new ReadOnlyObjectWrapper<>();

    public ProcessedImageView(PrefStuff pref, ImageProcessor processor,
                              ObjectProperty<ProcessedBI> source,
                              ExecutorService executorService)
    {
        if (executorService == null)
            executorService = Executors.newSingleThreadExecutor();
        ex = executorService;
        processorProperty.addListener((observable, oldValue, newValue) -> {
            // show debug info in tooltip
            Tooltip tip = new Tooltip(newValue == null ? "" : newValue.toString());
            if (newValue != null && newValue instanceof HasDebugWindow) {
                SwingNode node = new SwingNode();
                ((HasDebugWindow) newValue).debugWindowProperty().addListener((obsDebug, oldDebug, newDebug) -> {
                    node.setContent(newDebug);
                    Platform.runLater(() -> tip.graphicProperty().setValue(node));
                });
            }
            Tooltip.install(this, tip);

            reprocessImage();
        });

        setImageProcessor(processor);

        source.addListener((observable, oldValue, newValue) -> {
            setUnprocessedImage(newValue);
        });

        paramListener = (observable, oldValue, newValue) -> {
            reprocessImage();
        };

        // controls for processor parameters -- at top
        ProcessorControlView controlView = new ProcessorControlView
                (pref, processorProperty.getReadOnlyProperty(), readOnlyImageProperty());
        if (getControlPane().getTop() != null) throw new IllegalStateException();
        getControlPane().setTop(controlView);
    }

    /** reprocess the current image */
    private void reprocessImage() {
        ProcessedBI processed = imageProperty().getValue();
        setUnprocessedImage(processed == null ? null : processed.getPredecessor());
    }

    public void setImageProcessor(ImageProcessor processor) {
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

    private void setUnprocessedImage(ProcessedBI image) {
        if (image != imageProperty().getValue()) {
            ex.execute(() -> {
                ProcessedBI processed = image.process(processorProperty.getValue());
                imageProperty().set(processed);
            });
        }
    }
}
