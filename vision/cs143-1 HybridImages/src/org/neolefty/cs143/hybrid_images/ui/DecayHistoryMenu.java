package org.neolefty.cs143.hybrid_images.ui;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.scene.control.ComboBox;
import org.neolefty.cs143.hybrid_images.util.DecayHistory;

import java.util.ArrayList;

/** A dropdown menu showing a {@link DecayHistory}. */
public class DecayHistoryMenu extends ComboBox<String> {
    private ObservableListWrapper<String> items;
    // TODO limit count
    public static final int DISPLAY_COUNT = 15;
    // TODO shrink size using toString?

    public DecayHistoryMenu(DecayHistory<String> history) {
        items = new ObservableListWrapper<>(new ArrayList<>(history.values()));
        setItems(items);
        history.addListener(this::update);
        update(history);
    }

    private void update(DecayHistory<String> history) {
        items.clear();
        items.addAll(history.values());
    }
}
