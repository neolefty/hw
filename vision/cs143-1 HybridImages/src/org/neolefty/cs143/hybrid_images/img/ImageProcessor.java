package org.neolefty.cs143.hybrid_images.img;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import org.neolefty.cs143.hybrid_images.ui.ProcessorParam;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/** Alters incoming images. */
public abstract class ImageProcessor implements HasProcessorParams {
    private ReadOnlyObjectWrapper<BufferedImage> output;
    private List<ObjectProperty<BufferedImage>> inputs = new ArrayList<>();
    private List<ObjectProperty<BufferedImage>> inputsUnmod
            = Collections.unmodifiableList(inputs);

    /** An image processor with zero inputs. */
    public ImageProcessor() {
        output = new ReadOnlyObjectWrapper<>();
    }

    /** Increase the number of inputs by one. */
    public ObjectProperty<BufferedImage> addInput() {
        SimpleObjectProperty<BufferedImage> input = new SimpleObjectProperty<>();
        inputs.add(input);
        // recompute the output if an input image changes
        input.addListener((observable, oldValue, newValue) -> reprocess());
        return input;
    }

    private void reprocess() {
        List<BufferedImage> images = new ArrayList<>();
        for (ObjectProperty<BufferedImage> input : inputs)
            images.add(input.get());
        output.set(process(images));
    }

    /** What parameters does this processor have? Override this if it has any.
     *  @return null or empty if none. */
    @Override
    public Collection<ProcessorParam> getProcessorParams() {
        return null;
    }

    /** Implement this to do the processing. */
    public abstract BufferedImage process(Collection<BufferedImage> originals);

    @Override public String toString() { return getClass().getSimpleName(); }

    public BufferedImage getOutput() { return output.getValue(); }

    public ReadOnlyObjectProperty<BufferedImage> outputProperty() {
        return output.getReadOnlyProperty();
    }

    public List<ObjectProperty<BufferedImage>> inputsProperty() { return inputsUnmod; }

    /** Check the actual number of inputs against the allowed number. */
    public static void checkImageCount(int size, int min, int max) {
        if (size > max || size < min) {
            String range = (min == max) ? min + (min == 1 ? " image" : " images")
                    : min + " to " + max + " images";
            throw new IllegalStateException("Only " + range + " supported (found " + size + ").");
        }
    }
}
