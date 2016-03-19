package org.neolefty.cs143.hybrid_images.img.geom;

import org.neolefty.cs143.hybrid_images.test.TestKit;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Adjust images to have dimensions that are powers of 2. */
public class ImagePadKit {
    public static final List<Integer> POWERS_OF_TWO;
    static {
        List<Integer> pot = new ArrayList<>();
        for (long i = 1; i <= Integer.MAX_VALUE; i *= 2)
            pot.add((int) i);
        POWERS_OF_TWO = Collections.unmodifiableList(pot);
    }

    /** Pad <tt>in</tt> to have dimensions that are a power of two, with a grey border.
     *  No effect if its dimensions are already powers of 2. */
    public static BufferedImage padPowerOfTwo(BufferedImage in, boolean square) {
        int wIn = in.getWidth(), hIn = in.getHeight();
        int wOut = nextPowerOf2(wIn), hOut = nextPowerOf2(hIn);
        if (square) {
            wOut = Math.max(hOut, wOut);
            hOut = wOut;
        }
        return pad(in, wOut, hOut, false);
    }

    /** Pad <tt>in</tt> to be w x h.
     *  @param force if true, make a new copy, using 32-bit ARGB pixels,
     *               even if no size change is necessary. */
    public static BufferedImage pad(BufferedImage orig, int w, int h, boolean force) {
        int wOrig = orig.getWidth(), hOrig = orig.getHeight();
        // only shortcut if the size won't change & we're being asked to.
        if (!force && wOrig == w && hOrig == h)
            return orig;
        else {
            BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics g = result.getGraphics();
            g.setColor(new Color(127, 127, 127));
            g.fillRect(0, 0, w, h);
            int wd = (w - wOrig) / 2, hd = (h - hOrig) / 2;
            g.drawImage(orig, wd, hd, null);
            return result;
        }
    }

    /** Inclusive. For example, 63-> 64, 64 -> 64. */
    public static int nextPowerOf2(int x) {
        int index = Collections.binarySearch(POWERS_OF_TWO, x);
        if (index >= 0)
            return POWERS_OF_TWO.get(index);
        else
            return POWERS_OF_TWO.get(-index-1);
    }

    /** Inclusive. For example, 63 -> 32, 64 -> 64. */
    public static int prevPowerOf2(int x) {
        int index = Collections.binarySearch(POWERS_OF_TWO, x);
        if (index >= 0)
            return POWERS_OF_TWO.get(index);
        else
            return POWERS_OF_TWO.get(-index-2);
    }

    public static void main(String[] args) {
        TestKit.checkAssert();
        assert(prevPowerOf2(1023) == 512);
        assert(prevPowerOf2(1024) == 1024);
        assert(prevPowerOf2(1025) == 1024);
        assert(nextPowerOf2(1023) == 1024);
        assert(nextPowerOf2(1024) == 1024);
        assert(nextPowerOf2(1025) == 2048);
        System.out.println("Passed");
    }
}
