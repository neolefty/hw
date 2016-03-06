package org.neolefty.cs143.hybrid_images.ui;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.scene.control.ComboBox;

import java.util.List;
import java.util.stream.Collectors;

/** Menu of {@link ImageProcessorMenuItem}s, most recently-used first.
 *  Assumes that the list won't be changed after this is created.
 *  Unlike {@link FileHistoryMenu}, updates history weighting when something is chosen. */
public class ImageProcessorMenu extends ComboBox<ImageProcessorMenuItem> {
    private ImageProcessorHistory history;
    private ObservableListWrapper<ImageProcessorMenuItem> items;

    public ImageProcessorMenu(ImageProcessorHistory history) {
        this.history = history;
        items = new ObservableListWrapper<>(createItems());
        setItems(items);
        setValue(items.get(0));
        // update history weighting whenever a choice is made
        valueProperty().addListener((observable, oldValue, newValue)
                -> history.getHistory().add(newValue.toString()));
    }

    private List<ImageProcessorMenuItem> createItems() {
        return history.getHistory().values().stream()
                .map(name -> new ImageProcessorMenuItem(history.getProcessor(name)))
                .collect(Collectors.toList());
    }
}
