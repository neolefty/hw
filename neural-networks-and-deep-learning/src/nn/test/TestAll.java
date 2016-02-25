package nn.test;

public class TestAll {
    public static void main(String[] args) throws Exception {
        TestNetwork.main(args);
        TestLearning.main(args);
        TestPerformance.main(args);
    }

    public static void checkCloseTo(double a, double b, double epsilon) {
        assert Math.abs(a - b) < Math.abs(epsilon) : a + " and " + b + " differ by less than " + epsilon;
    }

    public static void checkAssertionsEnabled() {
        try {
            assert false;
            throw new IllegalStateException("Assertions disabled. Add vm option '-ea' to enable assertions.");
        } catch(AssertionError ignored) {}
    }
}
