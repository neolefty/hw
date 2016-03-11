package org.neolefty.cs143.hybrid_images.ui;

import org.neolefty.cs143.hybrid_images.img.ImageProcessor;
import org.neolefty.cs143.hybrid_images.ui.util.PrefStuff;
import org.neolefty.cs143.hybrid_images.util.DecayHistory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** A decay history of image processors. Enforces name uniqueness. */
public class ImageProcessorHistory {
    // History of names of filters that have been selected
    private DecayHistory<String> history;
    private Map<String, ImageProcessor> nameMap = new HashMap<>();

    public ImageProcessorHistory(PrefStuff pref, Collection<ImageProcessor> processors) {
        history = pref.getObject(null);
        if (history == null)
            history = new DecayHistory<>();
        // build a map of name to processors, and put all processors into history
        for (ImageProcessor proc : processors) {
            String name = proc.toString();
            if (nameMap.containsKey(name))
                throw new IllegalStateException("Name collision: two processors named \"" + name + "\".");
            nameMap.put(name, proc);
            if (!history.contains(name))
                // new filters (that have been added recently by the programmer) will show up at or near the top
                history.add(name);
        }
        // remove any old processor names that are not active
        //noinspection Convert2streamapi
        for (String name : history.values())
            if (!nameMap.containsKey(name))
                history.remove(name);

        history.addListener(pref::putObject);
    }

    /** The processor with the given name. */
    public ImageProcessor getProcessor(String name) {
        return nameMap.get(name);
    }

    /** The history of processor names. */
    public DecayHistory<String> getHistory() {
        return history;
    }

    public ImageProcessor getTop() {
        return getProcessor(history.getTop());
    }
}
