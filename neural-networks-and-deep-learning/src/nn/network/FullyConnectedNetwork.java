package nn.network;

import java.util.Arrays;
import java.util.List;

/** The structure of a neural network. Assumes full connectedness from layer to layer. */
public class FullyConnectedNetwork {
    /** The number of nodes in each layer, starting with inputs (0) and ending with outputs (n-1). */
    public List<Integer> layerSizes;

    /** The transfer function to convert unbounded inputs (weighted & biased)
     *  to bounded outputs. */
    public TransferFunction function;

    public FullyConnectedNetwork(Integer[] layerSizes) {
        this(layerSizes, TransferFunction.sigmoid);
    }

    public FullyConnectedNetwork(Integer[] layerSizes, TransferFunction function) {
        this.layerSizes = Arrays.asList(layerSizes);
        this.function = function;
    }

    /** How many layers does this network have? */
    public int getLayerCount() { return layerSizes.size(); }

    /** How many weights are required for a given layer?
     *  @param layer between 0 and n-2. 0 for between layers 0 and 1, etc. */
    public int getWeightCount(int layer) {
        return layerSizes.get(layer) * layerSizes.get(layer + 1);
    }

    /** How many weights are required overall? */
    public int getTotalWeightCount() {
        int result = 0;
        for (int i = 0; i < layerSizes.size() - 1; ++i)
            result += getWeightCount(i);
        return result;
    }

    /** How many inputs does this have -- that is, nodes in layer 0? */
    public int getInputCount() { return getLayerSize(0); }

    /** How many outputs? */
    public int getOutputCount() { return getLayerSize(getLayerCount() - 1); }

    /** How many nodes are in a particular layer? */
    public int getLayerSize(int layerIndex) { return layerSizes.get(layerIndex); }

    /** How many biases are there in a particular layer? Note: none in layer #0. */
    public int getBiasCount(int layerIndex) {
        return layerIndex == 0 ? 0 : getLayerSize(layerIndex);
    }

    /** How many biases are required? Assumes that the input layer doesn't have biases. */
    public int getTotalBiasCount() {
        int result = 0;
        for (int i = 1; i < layerSizes.size(); ++i)
            result += layerSizes.get(i);
        return result;
    }

    public double[] forward(double[] inputs, NetworkState state) {
        assert state.model == this;
        assert inputs.length == getInputCount();
        assert state.weights.length == getTotalWeightCount();
        assert state.biases.length == getTotalBiasCount();

        int iWeight = 0, iBias = 0;
        // previous layer = inputs to this layer
        double[] layerOutputs = inputs;
        for (int iLayer = 0; iLayer < layerSizes.size() - 1; ++ iLayer) {
            double[] layerInputs = layerOutputs;
            // accumulate into next layer's inputs
            layerOutputs = new double[layerSizes.get(iLayer + 1)];

            // 1. sum
            for (int iLayerInput = 0; iLayerInput < layerSizes.get(iLayer); ++iLayerInput) {
                for (int iAcc = 0; iAcc < layerOutputs.length; ++iAcc) {
                    layerOutputs[iAcc] += layerInputs[iLayerInput] * state.weights[iWeight++];
                }
            }
            // 2. bias
            for (int iAcc = 0; iAcc < layerOutputs.length; ++iAcc)
                layerOutputs[iAcc] -= state.biases[iBias++];
            // 3. sigmoid
            for (int iAcc = 0; iAcc < layerOutputs.length; ++iAcc)
                layerOutputs[iAcc] = function.y(layerOutputs[iAcc]);
        }

        return layerOutputs;
    }

    public double[] backward(double[] outputs, NetworkState state) {
        assert state.model == this;
        assert outputs.length == getOutputCount();
        assert state.weights.length == getTotalWeightCount();
        assert state.biases.length == getTotalBiasCount();

        // iLayer is the index of the layer whose outputs we are converting into inputs
        double[] layerOutputs = outputs;
        for (int iLayer = getLayerCount() - 1; iLayer >= 0; --iLayer) {
            double[] layerInputs = new double[getLayerSize(iLayer)];
            assert layerOutputs.length == layerInputs.length;
            assert layerOutputs != layerInputs; // later we may relax this restriction, for efficiency
            double[] prevLayerOutputs = new double[getLayerSize(iLayer - 1)];
            for (int iTarget = 0; iTarget < layerInputs.length; ++iTarget) { // each target neuron
                layerInputs[iTarget] = function.getInverse().y(layerOutputs[iTarget]) + state.getBias(iLayer, iTarget);
                double weightSum = state.getInputWeightSum(iLayer, iTarget);
                for (int iSource = 0; iSource < prevLayerOutputs.length; ++iSource) {
                    double weight = state.getWeight(iLayer, iTarget, iSource);
                    weightSum += weight;
                }
            }
        }
        throw new UnsupportedOperationException("nyi");
    }

    /** The index into a weights array for the i'th weight in a layer. */
    public int getWeightIndex(int layer, int i) {
        int result = 0;
        for (int x = 0; x < layer; ++x)
            result += getWeightCount(x);
        return result + i;
    }

    /** A particular index into a weights array.
     *  @param layer the index of the input layer
     *  @param inputIndex the index of the input neuron, within its layer
     *  @param targetIndex the index of the target neuron, within its layer */
    public int getWeightIndex(int layer, int inputIndex, int targetIndex) {
        int targetLayerSize = getLayerSize(layer + 1);
        return getWeightIndex(layer, inputIndex * targetLayerSize) + targetIndex;
    }

    /** The index into a biases array for the i'th bias in a layer. */
    public int getBiasIndex(int layer, int i) {
        if (layer == 0)
            throw new IndexOutOfBoundsException("Layer #0 doesn't have biases.");
        int result = 0;
        for (int x = 0; x < layer; ++x)
            result += getBiasCount(x);
        return result + i;
    }
}
