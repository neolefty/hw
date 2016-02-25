package nn.network;

import java.util.Random;

/** A random network seeder that approximates a Gaussian distribution
 * by aggregating multiple rolls of a "Die" -- that is, Random.nextDouble().
 *  Weights and biases are all between -1 and 1. */
public class PseudoGaussianSeeder implements NetworkSeeder {
    /** How many rolls of Random.nextDouble() do we add up? */
    public static final int DEFAULT_DICE_COUNT = 5;

    private static final Random r = new Random();

    /** How many Random.nextDouble()'s should be aggregated
     *  to make a random number? */
    public final int diceCount;

    public PseudoGaussianSeeder() { this(DEFAULT_DICE_COUNT); }

    public PseudoGaussianSeeder(int diceCount) { this.diceCount = diceCount; }

    public double nextDouble() {
        double s = 0;
        for (int i = 0; i < diceCount; ++i)
            s += r.nextDouble();
        return s/diceCount;
    }

    @Override
    public double nextWeight(int layer) {
        return 2*(nextDouble() - 0.5);
    }

    @Override
    public double nextBias(int layer, int inputCount) {
        return 2*(nextDouble()-0.5);
    }
}
