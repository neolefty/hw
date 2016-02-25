package nn.network;

import nn.data.Cohort;
import nn.data.MNistDatum;
import nn.data.MNistImage;
import nn.data.MNistSet;

import java.util.List;

/** Train a network, one step at a time. */
public class Trainer {
    public final MNistSet set;
    public NetworkState network;

    public Trainer(MNistSet set, Integer[] hiddenLayerSizes) {
        this.set = set;

        // figure out the layers in our neural network
        int hiddenLayerCount = (hiddenLayerSizes == null ? 0 : hiddenLayerSizes.length);
        Integer[] layerSizes = new Integer[hiddenLayerCount + 2];
        MNistImage templateImage = set.images[0];
        // inputs: number of pixels
        layerSizes[0] = templateImage.height * templateImage.width;
        // middle: hidden layers
        if (hiddenLayerCount > 0)
            System.arraycopy(hiddenLayerSizes, 0, layerSizes, 1, hiddenLayerSizes.length);
        // outputs: 10 possible digits
        layerSizes[layerSizes.length - 1] = 10;

        // seed the network with random initial weights
        FullyConnectedNetwork model = new FullyConnectedNetwork(layerSizes);
        PseudoGaussianSeeder seeder = new PseudoGaussianSeeder();
        network = new NetworkState(model);
        network.seed(seeder);
    }

    /** Do one full round of learning by gradient descent.
     *  @param nCohorts the number of cohorts to divide the data into
     *  @param rate the learning rate -- (negative of) how much to multiply
     *              the gradient by (must be positive) */
    public void gradientRound(int nCohorts, double rate) {
        if (rate < 0) throw new IllegalArgumentException("Learning rate is negative: " + rate);
        List<Cohort> cohorts = set.divide(nCohorts);
        for (Cohort cohort : cohorts)
            gradientStep(cohort, rate);
    }

    /** Do one step of learning by gradient descent.
     *  @param cohort the training set
     *  @param rate the learning rate -- (negative of) how much to multiply
     *              the gradient by (must be positive) */
    private void gradientStep(Iterable<MNistDatum> cohort, double rate) {
//        double[] gradient = gradient(cohort, )
        throw new UnsupportedOperationException("nyi");
    }
}
