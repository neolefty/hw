package nn.network;

import java.text.NumberFormat;

/** Weights and biases for a {@link FullyConnectedNetwork}. */
public class NetworkState implements Cloneable {
    /** Weights, ordered like this (from largest to smallest divisions):
     *  <ol>
     *  <li>layer - each layer has #inputs x #targets weights</li>
     *  <li>input - each input on each layer has #targets weights</li>
     *  <li>target - each target on each layer has 1 weight per input</li>
     *  </ol> */
    public double[] weights;

    /** Biases, grouped by layer, starting with the first internal layer (inputs don't have biases). */
    public double[] biases;

    /** The network this is a state for. */
    public final FullyConnectedNetwork model;

    public NetworkState(FullyConnectedNetwork model) {
        this.model = model;
        weights = new double[model.getTotalWeightCount()];
        biases = new double[model.getTotalBiasCount()];
    }

    /** Copy all the coefficients so that we can tweak them without messing anything up.
     *  Uses the same NetworkState without making a copy of it. */
    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    protected Object clone() throws CloneNotSupportedException {
        NetworkState result = new NetworkState(model);
        System.arraycopy(weights, 0, result.weights, 0, weights.length);
        System.arraycopy(biases, 0, result.biases, 0, biases.length);
        return result;
    }

    public void seed(NetworkSeeder seeder) {
        seedWeights(seeder);
        seedBiases(seeder);
    }

    /** The sum of the weights of the inputs to a particular target node.\
     *  @param layer the layer of the target node
     *  @param node the index of the target node */
    public double getInputWeightSum(int layer, int node) {
        double result = 0;
        int inputCount = model.getLayerSize(layer - 1);
        for (int i = 0; i < inputCount; ++i)
            result += getWeight(layer - 1, node, i);
        return result;
    }

    public void seedWeights(NetworkSeeder seeder) {
        int i = 0;
        for (int iLayer = 0; iLayer < model.getLayerCount() - 1; ++iLayer)
            for (int iWeight = 0; iWeight < model.getWeightCount(iLayer); ++iWeight)
                weights[i++] = seeder.nextWeight(iLayer);
    }

    public void seedBiases(NetworkSeeder seeder) {
        int i = 0;
        for (int iLayer = 1; iLayer < model.getLayerCount(); ++iLayer)
            for (int iBias = 0; iBias < model.getLayerSize(iLayer); ++iBias) {
                // number of inputs to this layer = number of nodes in previous layer
                int inputCount = model.getLayerSize(iLayer - 1);
                biases[i++] = seeder.nextBias(iLayer, inputCount);
            }
    }

    @Override
    public String toString() { return toString(20); }

    public String toString(int maxListLength) {
        String result = "";
        NumberFormat f = NumberFormat.getNumberInstance();
        f.setMinimumFractionDigits(2);
        for (int layer = 0; layer < model.getLayerCount(); ++layer) {
            boolean showWeights = (layer < model.getLayerCount() - 1);
            boolean showBiases = (layer > 0);
            result += "Layer " + layer + ": ";
            if (showWeights) {
                result += "weights [";
                for (int i = 0; i < model.getWeightCount(layer) && i < maxListLength; ++i)
                    result += (i == 0 ? "" : ", ") + f.format(getWeight(layer, i));
                if (maxListLength < model.getWeightCount(layer))
                    result += ", ...";
                result += "]";
            }
            if (showWeights && showBiases) result += "; ";
            if (showBiases) {
                result += "biases [";
                for (int i = 0; i < model.getBiasCount(layer) && i < maxListLength; ++i)
                    result +=(i == 0 ? "" : ", ") + f.format(getBias(layer, i));
                if (maxListLength < model.getBiasCount(layer))
                    result += ", ...";
                result += "]";
            }
            result += "\n";
        }
        return result;
    }

    /** The i'th weight in a layer. */
    public double getWeight(int layer, int i) {
        return weights[model.getWeightIndex(layer, i)];
    }

    /** A particular weight.
     *  @param layer the index of the input layer
     *  @param inputIndex the index of the input neuron, within its layer
     *  @param targetIndex the index of the target neuron, within its layer */
    public double getWeight(int layer, int targetIndex, int inputIndex) {
        return weights[model.getWeightIndex(layer, inputIndex, targetIndex)];
    }

    /** The i'th bias in a layer. */
    public double getBias(int layer, int i) {
        return biases[model.getBiasIndex(layer, i)];
    }

    /** Run the inputs through the network and get outputs. */
    public double[] compute(double[] inputs) {
        return model.forward(inputs, this);
    }
}
