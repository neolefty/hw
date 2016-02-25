package nn.network;

/** A number generator. */
public interface NetworkSeeder {
    /** Initialize an input weight.
     *  @param layer the index of the layer the input is coming <em>from</em>
     *               (in other words, 0 <= layer <= n-2).
     *  @return the new input weight */
    double nextWeight(int layer);

    /** Initialize a node's bias.
     *  @param layer the index of the layer (starts at 1 since input nodes don't have a bias).
     *  @param inputCount the number of inputs this node has
     *  @return a new bias for this node */
    double nextBias(int layer, int inputCount);
}
