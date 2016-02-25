package nn.data;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/** A set of MNist images and labels. */
public class MNistSet implements Iterable<MNistDatum> {
    private static final Random r = new Random();

    public final String name;
    public final byte[] labels;
    public final MNistImage[] images;

    public MNistSet(String name, byte[] labels, MNistImage[] images) {
        this.name = name;
        this.labels = labels;
        this.images = images;
        if (labels.length != images.length)
            throw new IllegalArgumentException("number of images ("
                    + images.length + ") disagrees with labels (" + labels.length + ")");
    }

    public MNistImage getImage(int i) {
        return images[i];
    }

    public byte getLabel(int i) {
        return labels[i];
    }

    public MNistDatum getDatum(int i) {
        return new MNistDatum(getImage(i), getLabel(i));
    }

    public int size() {
        return labels.length;
    }

    /** Divide into <tt>nCohorts</tt> cohorts. */
    public List<Cohort> divide(int nCohorts) {
        List<Cohort> result = new ArrayList<>();
        List<List<Integer>> indexes = divide(size(), nCohorts);
//        for (List<Integer> index : indexes)
//            result.add(new Cohort(this, index));
        result.addAll(indexes.stream().map(index -> new Cohort(this, index)).collect(Collectors.toList()));
        return result;
    }

    /** Helper function for dividing into cohorts. */
    public static List<List<Integer>> divide(int total, int nCohorts) {
        // initialize
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < nCohorts; ++i)
            result.add(new ArrayList<>());

        // initial assignment: random and unbalanced
        for (int i = 0; i < total; ++i)
            result.get(r.nextInt(nCohorts)).add(i);

        // even things out
        int maxDiff = (total % nCohorts == 0) ? 0 : 1;
        int min = total / nCohorts;
        int max = min + maxDiff;
        // - the lists that are too small or too large
        //   note: can't use HashSets here because the contents and therefore the hash codes change over time
        List<List<Integer>> smalls = new ArrayList<>(), larges = new ArrayList<>();
        for (List<Integer> list : result)
            if (list.size() < max) smalls.add(list);
            else if (list.size() > max) larges.add(list);
        // - shrink the large into the small
        for (List<Integer> large : larges) {
            // remove a random entry and put it into a small -- could be faster
            // if we just took from the end, but in practice it doesn't matter
            while(large.size() > max) {
                Integer i = large.remove(r.nextInt(large.size()));
                // I am pretty sure there must of necessity be smalls if there are larges
                List<Integer> small = smalls.iterator().next();
                small.add(i);
                // if the small is large enough now, remove it from the smalls
                if (small.size() >= max) smalls.remove(small);
            }
        }
        // - grow the remaining smalls?
        for (List<Integer> small : smalls) {
            while (small.size() < min) {
                for (List<Integer> list : result) {
                    if (list.size() > min) {
                        small.add(list.remove(r.nextInt(list.size())));
                        break;
                    }
                }
            }
        }

        return result;
    }

    /** Alternate helper function for dividing into cohorts. */
    public static List<List<Integer>> divide2(int total, int nCohorts) {
        // initialize
        List<List<Integer>> result = new ArrayList<>(), stillSmall = new ArrayList<>(),
                // this will contain lists with max # elements
                bigs = new ArrayList<>();
        for (int i = 0; i < nCohorts; ++i)
            result.add(new ArrayList<>());
        stillSmall.addAll(result);

        // what is our target size?
        int maxDiff = (total % nCohorts == 0) ? 0 : 1;
        int min = total / nCohorts;
        int max = min + maxDiff;

        int cut = r.nextInt(total); // cut the deck to avoid concentrating the last few together

        // fill them randomly, removing when they get big enough
        for (int i = 0; i < total; ++i) {
            int n = (i + cut) % total;
            int cohort = r.nextInt(stillSmall.size());
            List<Integer> sub = stillSmall.get(cohort);
            sub.add(n);
            if (sub.size() == max) {
                stillSmall.remove(cohort);
                bigs.add(sub);
            }
        }

        // identify still-anemic lists
        for (List<Integer> small : stillSmall) {
            while (small.size() < min) {
                List<Integer> big = bigs.get(r.nextInt(bigs.size()));
                small.add(big.remove(big.size() - 1));
                if (big.size() == min)
                    bigs.remove(big);
            }
        }

        return result;
    }

    // implement Iterable
    @Override
    public Iterator<MNistDatum> iterator() {
        return new Iterator<MNistDatum>() {
            private int i = 0;
            @Override public boolean hasNext() { return i < size(); }
            @Override public MNistDatum next() { return new MNistDatum(getImage(i), getLabel(i++)); }
        };
    }

    @Override
    public void forEach(Consumer<? super MNistDatum> action) {
        for (int i = 0; i < size(); ++i)
            action.accept(new MNistDatum(getImage(i), getLabel(i++)));
    }

    @Override
    public Spliterator<MNistDatum> spliterator() {
        throw new UnsupportedOperationException("not implemented");
    }
}
