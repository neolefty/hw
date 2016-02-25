package nn.data;

import nn.data.MNistDatum;
import nn.data.MNistImage;
import nn.data.MNistSet;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

/** A cohort of MNIST images. */
public class Cohort implements Iterable<MNistDatum> {
    public final MNistSet all;
    public final List<Integer> selectedIndexes;

    public Cohort(MNistSet all, List<Integer> selectedIndexes) {
        this.all = all;
        this.selectedIndexes = selectedIndexes;
    }

    /** The i'th datum in this cohort. */
    public MNistDatum getDatum(int i) { return new MNistDatum(getImage(i), getLabel(i)); }

    /** The i'th label in this cohort. */
    public byte getLabel(int i) { return all.getLabel(selectedIndexes.get(i)); }

    /** The i'th image in this cohort. */
    public MNistImage getImage(int i) { return all.getImage(selectedIndexes.get(i)); }

    @Override
    public Iterator<MNistDatum> iterator() {
        return new Iterator<MNistDatum>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < selectedIndexes.size();
            }

            @Override
            public MNistDatum next() {
                return getDatum(i++);
            }
        };
    }

    @Override
    public void forEach(Consumer<? super MNistDatum> action) {
        for (int i = 0; i < selectedIndexes.size(); ++i)
            action.accept(getDatum(i));
    }

    @Override
    public Spliterator<MNistDatum> spliterator() {
        return new Split();
    }

    private class Split implements Spliterator<MNistDatum> {
        private int i = 0, end = selectedIndexes.size();
        private Split() { this(0, selectedIndexes.size()); }
        private Split(int i, int end) { this.i = i; this.end = end; }

        @Override public long estimateSize() { return end - i; }

        @Override
        public boolean tryAdvance(Consumer<? super MNistDatum> action) {
            if (i < end) {
                action.accept(getDatum(i));
                return true;
            }
            else
                return false;
        }

        @Override
        public Spliterator<MNistDatum> trySplit() {
            if (end - i >= 2) {
                int j = i + (end - i) / 2;
                Spliterator<MNistDatum> result = new Split(i, j);
                i = j;
                return result;
            }
            else
                return null;
        }

        @Override
        public int characteristics() {
            return Spliterator.NONNULL | Spliterator.IMMUTABLE | Spliterator.ORDERED
                    | Spliterator.SIZED | Spliterator.SUBSIZED;
        }

    }
}
