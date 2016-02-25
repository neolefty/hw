package nn.network;

/** A function to condense unbounded inputs to a range between 0 and 1. For example a sigmoid. */
public interface TransferFunction {
    double y(double x);
    TransferFunction getInverse();

    TransferFunction sigmoid = new TransferFunction() {
        @Override public double y(double x) { return 1 / (1 + Math.exp(-x)); }
        @Override public TransferFunction getInverse() { return sigmoidPrime; }
        @Override public String toString() { return "exponential sigmoid"; }
    };

    TransferFunction sigmoidPrime = new TransferFunction() {
        @Override public double y(double x) { return Math.log(x/(1-x)); }
        @Override public TransferFunction getInverse() { return sigmoid; }
        @Override public String toString() { return "inverse sigmoid"; }
    };

    /** x = y, capped at 0 and 1. ___/---- */
    TransferFunction cappedIdentity = new TransferFunction() {
        @Override
        public double y(double x) {
            if (x < 0) return 0;
            else if (x > 1) return 1;
            else //noinspection SuspiciousNameCombination
                return x;
        }

        @Override
        public TransferFunction getInverse() {
            throw new UnsupportedOperationException("Not invertable (not one-to-one)");
        }

        public String toString() { return "capped identity"; }
    };
}
