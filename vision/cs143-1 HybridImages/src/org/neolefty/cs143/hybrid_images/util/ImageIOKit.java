package org.neolefty.cs143.hybrid_images.util;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;

/** Utilities for image IO. */
public class ImageIOKit {
    public static BufferedImage loadImage(File file) throws IOException {
        String filename = file.getName();
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot >= filename.length() - 1)
            throw new UnsupportedEncodingException
                    ("Couldn't extract file type suffix from filename \"" + filename + "\".");

        String suffix = filename.substring(dot + 1);
        Iterator<ImageReader> readers = ImageIO.getImageReadersBySuffix(suffix);
        if (readers == null || !readers.hasNext())
            throw new UnsupportedEncodingException
                    ("Couldn't find a reader for \"" + filename + "\", type \"." + suffix + "\".");

        FileInputStream fileIn = null;
        try {
            fileIn = new FileInputStream(file);
            ImageIO.createImageInputStream(fileIn);
            ImageReader reader = readers.next();
            ImageInputStream in = new FileImageInputStream(file);
            reader.setInput(in);
            return reader.read(0);
        } finally {
            close(fileIn);
        }
    }

    private static void close(InputStream in) {
        if (in != null) try { in.close(); } catch (IOException ignored) { }
    }
}
