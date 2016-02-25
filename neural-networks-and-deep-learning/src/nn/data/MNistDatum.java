package nn.data;

/** An image and its label. */
public class MNistDatum {
    public final MNistImage image;
    public final byte label;

    public MNistDatum(MNistImage image, byte label) {
        this.image = image;
        this.label = label;
    }
}
