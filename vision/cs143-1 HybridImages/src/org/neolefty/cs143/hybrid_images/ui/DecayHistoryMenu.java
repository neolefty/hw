package org.neolefty.cs143.hybrid_images.ui;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.scene.control.ComboBox;
import org.neolefty.cs143.hybrid_images.util.DecayHistory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** A dropdown menu showing a {@link DecayHistory}. */
// TODO update when image changed interactively -- not important enough for now
public class DecayHistoryMenu extends ComboBox<Shortener> {
    private ObservableListWrapper<Shortener> items;
    public static final int DISPLAY_COUNT = 15;

    public DecayHistoryMenu(DecayHistory<String> history) {
        items = new ObservableListWrapper<>(new ArrayList<>(shorten(history.values())));
        setItems(items);
        if (items.size() > 0)
            setValue(items.get(0));
        history.addListener(this::update);
        update(history);
    }

    private void update(DecayHistory<String> history) {
        items.clear();
        items.addAll(shorten(history.values()));
    }

    private List<Shortener> shorten(Collection<String> paths) {
        List<Shortener> result = new ArrayList<>();
        int i = 0;
        for (String path : paths) {
            File file = new File(path);
            result.add(new Shortener(file.getParentFile().getName() + "/" + file.getName(), path));
            if (++i >= DISPLAY_COUNT)
                break;
        }
        return result;
    }
}
