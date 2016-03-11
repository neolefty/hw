package org.neolefty.cs143.hybrid_images.ui.util;

import org.neolefty.cs143.hybrid_images.img.ImageProcessor;
import org.neolefty.cs143.hybrid_images.util.ImageIOKit;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/** A BufferedImage that knows its provenance, starting with what file it came from,
 *  and how it was subsequently processed. */
public class ProcessedBI {
    private ProcessedBI predecessor;
    private BufferedImage image;
    private ImageProcessor processor;
    private File file;

    public ProcessedBI(File file) throws IOException {
        image = ImageIOKit.loadImage(file);
        this.file = file;
    }

    public ProcessedBI(ProcessedBI predecessor, ImageProcessor processor, BufferedImage image) {
        this.predecessor = predecessor;
        this.processor = processor;
        this.image = image;
    }

    public ProcessedBI process(ImageProcessor processor) {
        return new ProcessedBI(this, processor, processor.process(image));
    }

    public BufferedImage getImage() { return image; }

    /** The unprocessed version of this.
     *  Null if this is the original. */
    public ProcessedBI getPredecessor() { return predecessor; }

    /** The processor used to turn {@link #getPredecessor()} into this.
     *  Null if none. */
    public ImageProcessor getProcessor() { return processor; }

    /** Get the original file this came from.
     *  Null if it originated not from disk -- for example, a synthetic image. */
    public File getFile() {
        return file == null ? (predecessor == null ? null : predecessor.getFile()) : file;
    }

    @Override public String toString() { return toString(" - ", false); }

    public String toString(String sep, boolean shorten) {
        String procName = processor == null ? "" : processor.toString() + sep;
        String filename = (file == null ? "" : file.getName() + sep);
        if (shorten) {
            // remove suffix -- doesn't add meaning
            if (filename.lastIndexOf(".") > 0)
                filename = filename.substring(0, filename.lastIndexOf("."));
            if (filename.indexOf(".") > 0 && filename.indexOf(".") < filename.length() - 1)
                filename = filename.substring(filename.indexOf(".") + 1);
            // last 12 or fewer characters
            if (filename.length() > 12)
                filename = filename.substring(filename.length() - 12);
            if (procName.length() > 8 + sep.length())
                procName = procName.substring(0, 8) + sep;
        }
        String result = (predecessor == null ? "" : predecessor.toString(sep, shorten) + sep)
                + procName
                + filename;
        while (result.endsWith(sep))
            result = result.substring(0, result.length() - sep.length());
        return result;
    }
}
