package nn.network;

import nn.data.MNistDatum;
import nn.network.NetworkState;

/** How good is a network? */
public class MeasureNetwork {
    public final NetworkState network;

    public MeasureNetwork(NetworkState network) {
        this.network = network;
    }

    /** Mean squared error. */
    public double mse(Iterable<MNistDatum> i) {
        double error2 = 0;
        int n = 0;
        for (MNistDatum d : i) {
            error2 += error2(d);
            ++n;
        }
        return error2 / n;
    }

    /** Standard cost function for this network = mse / 2. */
    public double cost(Iterable<MNistDatum> i) {
        return mse(i) / 2;
    }

    /** Squared error for a single datum. */
    public double error2(MNistDatum d) {
        double[] out = network.compute(d.image.getDoubles());
        double result = 0;
        for (int i = 0; i < out.length; ++i) {
            double e = out[i] - (d.label == i ? 1 : 0);
            result += e * e;
        }
        return result;
    }
}
