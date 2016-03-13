package org.neolefty.cs143.hybrid_images.ui.util;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import org.neolefty.cs143.hybrid_images.util.DecayHistory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** A dropdown menu showing a {@link DecayHistory} of files.
 *  Assumes that whenever a file is selected, it will be added to the history externally. */
// TODO update when image changed interactively -- not important enough for now
public class FileHistoryMenu extends ComboBox<FilenameShortener> {
    private ObservableListWrapper<FilenameShortener> items;
    public static final int DISPLAY_COUNT = 15;

    public FileHistoryMenu(DecayHistory<String> history) {
        items = new ObservableListWrapper<>(new ArrayList<>(shorten(history.values())));
        setItems(items);
        if (items.size() > 0)
            setValue(items.get(0));
        // whenever a file is added to the history, update our display
        history.addListener(this::update);
    }

    private void update(DecayHistory<String> history) {
        List<FilenameShortener> shortened = shorten(history.values());
        Platform.runLater(() -> { // need to do this because a side-effect of retainAll() can be a UI change
            try {
                // non-destructive update
                items.retainAll(shortened);
                for (FilenameShortener fs : shortened)
                    if (!items.contains(fs))
                        items.add(fs);
            } catch(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private List<FilenameShortener> shorten(Collection<String> paths) {
        List<FilenameShortener> result = new ArrayList<>();
        int i = 0;
        for (String path : paths) {
            File file = new File(path);
            result.add(new FilenameShortener(file.getParentFile().getName() + "/" + file.getName(), path));
            if (++i >= DISPLAY_COUNT)
                break;
        }
        return result;
    }
}
