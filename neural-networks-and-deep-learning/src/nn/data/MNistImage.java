package nn.data;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.ref.WeakReference;

/** A single image from the MNIST data. */
public class MNistImage {
    public final byte[] bytes;
    public final int height, width;

    public MNistImage(int height, int width, byte[] bytes) {
        this.bytes = bytes;
        this.height = height;
        this.width = width;
    }

    private transient WeakReference<double[]> doubleCache = null;
    public double[] getDoubles() {
        double[] fs = null;
        if (doubleCache != null)
            fs = doubleCache.get();
        if (fs == null) {
            fs = new double[bytes.length];
            for (int i = 0; i < bytes.length; ++i)
                fs[i] = ((float) getUnsigned(bytes[i])) / 256;
            doubleCache = new WeakReference<>(fs);
        }
        return fs;
    }

    public byte getByte(int x, int y) {
        return bytes[y * width + x];
    }

    public int getUnsigned(byte b) {
        int result = b;
        if (result < 0)
            result += 256;
        return result;
    }

    public int getRGB(int x, int y) {
        int b = getUnsigned(getByte(x, y));
        b = 255 - b;
//        return b + b * 256 + b * 65536;
        return b | b << 8 | b << 16;
    }

    public Image getImage() {
//        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; ++y)
            for (int x = 0; x < width; ++x)
                image.setRGB(x, y, getRGB(x, y));
        return image;
    }
}
