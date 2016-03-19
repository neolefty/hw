package org.neolefty.cs143.hybrid_images.img.two;

import org.neolefty.cs143.hybrid_images.img.boof.HasProcessorParamsBase;

/** Split integers into RGB and then perform an operation on them. Uses transparency channel from first pixel. */
public class TwoIntegerRGBSplitter extends HasProcessorParamsBase implements BinaryIntFunction {
    private BinaryIntFunction wrapped;

    public TwoIntegerRGBSplitter(BinaryIntFunction wrapped) {
        this.wrapped = wrapped;
        addIfHasParams(wrapped);
    }

    @Override
    public int apply(int a, int b) {
        int ra = (a >> 16) & 0xff, rb = (b >> 16) & 0xff,
                ga = (a >> 8) & 0xff, gb = (b >> 8) & 0xff,
                ba = a & 0xff, bb = b & 0xff;
        int ro = wrapped.apply(ra, rb), go = wrapped.apply(ga, gb), bo = wrapped.apply(ba, bb);
        int result = (a & 0xff000000) + (ro << 16) + (go << 8) + bo;
        return result;
//        return a & 0xff000000 // transparency
//                + wrapped.apply(ra, rb) << 16
//                + wrapped.apply(ga, gb) << 8
//                + wrapped.apply(ba, bb);
    }

    @Override
    public String toString() {
        return wrapped.toString();
    }
}
