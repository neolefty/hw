package org.neolefty.cs143.hybrid_images.ui.util;

import org.neolefty.cs143.hybrid_images.img.ImageProcessor;
import org.neolefty.cs143.hybrid_images.util.ImageIOKit;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/** A BufferedImage that knows its provenance, starting with what file it came from,
 *  and how it was subsequently processed. */
public class ProcessedBI {
    private Collection<ProcessedBI> predecessors;
    private BufferedImage image;
    private ImageProcessor processor;
    private File file;

    public ProcessedBI(File file) throws IOException {
        image = ImageIOKit.loadImage(file);
        this.file = file;
    }

    public ProcessedBI(ProcessedBI predecessor, ImageProcessor processor, BufferedImage image) {
        this.predecessors = Collections.singleton(predecessor);
        this.processor = processor;
        this.image = image;
    }

    public ProcessedBI(Collection<ProcessedBI> predecessors, ImageProcessor processor, BufferedImage image) {
        this.predecessors = predecessors;
        this.processor = processor;
        this.image = image;
    }

    public BufferedImage getImage() { return image; }

    /** The unprocessed version of this.
     *  Null if this is the original. */
    public Collection<ProcessedBI> getPredecessor() { return predecessors; }

    /** The processor used to turn {@link #getPredecessor()} into this.
     *  Null if none. */
    public ImageProcessor getProcessor() { return processor; }

    /** Get an original file this came from. If it's a combination of multiple files,
     *  return the first one we find among the ancestors.
     *  Null if it originated not from disk -- for example, a synthetic image. */
    public File getFirstFile() {
        if (file != null)
            return file;
        else if (predecessors == null)
            return null;
        else {
            for (ProcessedBI pred : predecessors) {
                File result = pred.getFirstFile();
                if (result != null)
                    return result;
            }
            // didn't find one
            return null;
        }
    }

    @Override public String toString() { return toString(" - ", false); }

    public String toString(String sep, boolean shorten) {
        String procName = processor == null ? "" : processor.toString();
        String filename = (file == null ? "" : file.getName());
        String before = procName + (procName.length() == 0 ? "" : sep) + filename;
        boolean print = false;
        if (shorten) {
            // remove suffix -- doesn't add meaning
            if (filename.lastIndexOf(".") > 0)
                filename = filename.substring(0, filename.lastIndexOf("."));
            if (filename.indexOf(".") > 0 && filename.indexOf(".") < filename.length() - 1)
                filename = filename.substring(filename.indexOf(".") + 1);
            // last 12 or fewer characters
            if (filename.length() > 12)
                // first & last 5 letters "abcde..vwxyz"
                filename = filename.substring(0, 5) + "__" + filename.substring(filename.length() - 5);
            if (procName.length() > 8)
                procName = procName.substring(0, 8);
//            print = true;
        }
        String result = procName.trim() + (procName.length() == 0 ? "" : sep) + filename.trim();
        String after = result;

        // describe predecessors as an array
        if (predecessors != null && predecessors.size() > 0) {
            // if we're shortening, just use the 1st ancestral branch -- ignore others
            if (shorten || predecessors.size() == 1)
                result = predecessors.iterator().next().toString(sep, shorten) + sep + result;
            else {
                String ancestors = "";
                for (ProcessedBI pred : predecessors)
                    ancestors += (ancestors.length() == 0 ? "" : ", ") + pred.toString(sep, shorten);
                result = "[" + ancestors + "]" + sep + result;
            }
        }

        while (result.endsWith(sep))
            result = result.substring(0, result.length() - sep.length());

        if (print)
            System.out.println("Before: \"" + before + "\"; After: \"" + after + "\"; Final: \"" + result + "\"");

        return result;
    }

    /** Process this image using <tt>processor</tt>.
     *  @return the result including history */
    public ProcessedBI process(ImageProcessor processor) {
        BufferedImage output = processor.process(Collections.singleton(getImage()));
        return new ProcessedBI(this, processor, output);
    }

    /** Process a collection of images using <tt>processor</tt>.
     *  @return the result, including history. */
    public static ProcessedBI process(ImageProcessor processor, Collection<ProcessedBI> inputs) {
        Collection<BufferedImage> biInputs = new ArrayList<>();
        for (ProcessedBI in : inputs)
            biInputs.add(in.getImage());
        BufferedImage output = processor.process(biInputs);
        return new ProcessedBI(inputs, processor, output);
    }
}
