package org.neolefty.cs143.hybrid_images.util;

public class TestKit {
    public static void checkAssert() {
        try {
            assert false;
            throw new IllegalStateException("Assertions not enabled. Use vm arg -ea.");
        } catch(AssertionError ignored) {}
    }
}
