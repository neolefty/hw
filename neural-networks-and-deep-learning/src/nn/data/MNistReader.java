package nn.data;

import java.io.*;
import java.util.zip.GZIPInputStream;

/** Read Yann LeCun's MNIST files, from http://yann.lecun.com/exdb/mnist/ */
public class MNistReader {
    /** Magic numbers. Actually these follow a formula given on the web page mentioned above. */
    public static final int MAGIC_LABELS = 2049, MAGIC_IMAGES = 2051;

    /** Load a set from disk.
     *  @param dir the directory to read from
     *  @param prefix the first word in the filenames, such as "train" or "t10k" */
    public static MNistSet readMnistSet(File dir, String prefix) throws IOException {
        File labelsFile = new File(dir, prefix + "-labels-idx1-ubyte.gz");
        File imagesFile = new File(dir, prefix + "-images-idx3-ubyte.gz");
        byte[] labels = readMnistLabels(labelsFile);
        MNistImage[] images = readMnistImages(imagesFile);
        return new MNistSet(prefix, labels, images);
    }

    public static byte[] readMnistLabels(File file) throws IOException {
        FileInputStream fin = new FileInputStream(file);
        GZIPInputStream gzin = new GZIPInputStream(new BufferedInputStream(fin));
        DataInputStream din = new DataInputStream(gzin);
        int magic = din.readInt();
        if (magic != MAGIC_LABELS)
            throw new IOException("Unexpected magic number for labels: "
                    + magic + "; expected " + MAGIC_LABELS + ".");
        int n = din.readInt();
        byte[] labels = new byte[n];
        for (int i = 0; i < n; ++i)
            labels[i] = din.readByte();
        return labels;
    }

    public static MNistImage[] readMnistImages(File file) throws IOException {
        FileInputStream fin = new FileInputStream(file);
        GZIPInputStream gzin = new GZIPInputStream(new BufferedInputStream(fin));
        DataInputStream din = new DataInputStream(gzin);
        int magic = din.readInt();
        if (magic != MAGIC_IMAGES)
            throw new IllegalStateException("Unexpected magic number for images: " + magic + "; expected " + MAGIC_IMAGES + ".");
        int n = din.readInt();
        int height = din.readInt();
        int width = din.readInt();
        MNistImage[] images = new MNistImage[n];
        for (int i = 0; i < n; ++i)
            images[i] = readMnistImage(height, width, din);
        return images;
    }

    private static MNistImage readMnistImage(int height, int width, DataInputStream din) throws IOException {
        int a = height * width;
        byte[] bytes = new byte[a];
        int n = din.read(bytes, 0, a);
        while (n < a) {
            int q = din.read(bytes, n, a - n);
            if (q == 0) // stalled?
                throw new IllegalStateException("Read only " + n + " bytes; expected " + a);
            n += q;
        }
        return new MNistImage(height, width, bytes);
    }
}
