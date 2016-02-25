package nn.test;

import nn.data.MNistSet;

import java.util.*;

/** Test the learning process. */
public class TestLearning {
    private static final Random r = new Random();

    public static void main(String[] args) {
        TestAll.checkAssertionsEnabled();
        testCohortDivision();
//        testCohortDivision();
//        testCohortDivision2();
        testCohortDivision2();
//        System.out.println();
//        testCohortDivision();
//        testCohortDivision();
//        testCohortDivision2();
//        testCohortDivision2();
    }

    private static void testCohortDivision() {
        // make sure identical lists have the same hash code
        List<Integer> a = new ArrayList<>(), b = new ArrayList<>();
        a.add(5); b.add(5); a.add(9); b.add(9); a.add(6); b.add(6);
        a.add(6); b.add(6); a.add(-1); b.add(-1);
        assert a.hashCode() == b.hashCode() : "hash codes of identical lists should match";
        // to test randomness, check that the hashes of all the results are the same
        Set<Integer> hashes = new HashSet<>();
        Stopwatch watch = new Stopwatch();
        {
            int total = 500000, cohorts = 1000;
            List<List<Integer>> lists = MNistSet.divide(total, cohorts);
            watch.mark("divide " + total + " into " + cohorts + " cohorts");
            ensureDivided(lists, hashes);
            watch.mark("check " + total);
        }

        int n = 100;
        hashes.clear();
        for (int i = 0; i < n; ++i) {
            int total = 5000 + r.nextInt(10) * 1000;
            ensureDivided(MNistSet.divide(total + r.nextInt(99) + 1, 100), hashes);
            ensureDivided(MNistSet.divide(total, 100), hashes);
        }
        assert hashes.size() == n * 2;
        watch.mark("test " + n + " small cohorts");

        n = 5000;
        hashes.clear();
        for (int i = 0; i < n; ++i) {
            int total = 500 + r.nextInt(10) * 100;
            ensureDivided(MNistSet.divide(total + r.nextInt(9) + 1, 10), hashes);
            ensureDivided(MNistSet.divide(total, 10), hashes);
        }
        assert hashes.size() == n * 2;
        watch.mark("test " + n + " tiny cohorts");

        System.out.println(watch);
    }

    private static void testCohortDivision2() {
        // make sure identical lists have the same hash code
        List<Integer> a = new ArrayList<>(), b = new ArrayList<>();
        a.add(5); b.add(5); a.add(9); b.add(9); a.add(6); b.add(6);
        a.add(6); b.add(6); a.add(-1); b.add(-1);
        assert a.hashCode() == b.hashCode() : "hash codes of identical lists should match";
        // to test randomness, check that the hashes of all the results are the same
        Set<Integer> hashes = new HashSet<>();
        Stopwatch watch = new Stopwatch();
        {
            int total = 500000, cohorts = 1000;
            List<List<Integer>> lists = MNistSet.divide2(total, cohorts);
            watch.mark("-- divide2 " + total + " into " + cohorts + " cohorts");
            ensureDivided(lists, hashes);
            watch.mark("check " + total);
        }

        int n = 100;
        hashes.clear();
        for (int i = 0; i < n; ++i) {
            int total = 5000 + r.nextInt(10) * 1000;
            ensureDivided(MNistSet.divide2(total + r.nextInt(99) + 1, 100), hashes);
            ensureDivided(MNistSet.divide2(total, 100), hashes);
        }
        assert hashes.size() == n * 2;
        watch.mark("test " + n + " small cohorts");

        n = 5000;
        hashes.clear();
        for (int i = 0; i < n; ++i) {
            int total = 500 + r.nextInt(10) * 100;
            ensureDivided(MNistSet.divide2(total + r.nextInt(9) + 1, 10), hashes);
            ensureDivided(MNistSet.divide2(total, 10), hashes);
        }
        assert hashes.size() == n * 2;
        watch.mark("test " + n + " tiny cohorts");

        System.out.println(watch);
    }

    private static void ensureDivided(List<List<Integer>> lists, Set<Integer> hashes) {
        int hash = lists.hashCode();
        assert !hashes.contains(hash) : "test randomness -- each should be distinct";
        hashes.add(hash);

        Set<Integer> all = new HashSet<>();
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        int minSize = Integer.MAX_VALUE, maxSize = Integer.MIN_VALUE;
        int n = 0;
        for (List<Integer> list : lists) {
            minSize = Math.min(minSize, list.size());
            maxSize = Math.max(maxSize, list.size());
            for (Integer i : list) {
                min = Math.min(min, i);
                max = Math.max(max, i);
                all.add(i);
                ++n;
            }
        }

        assert min == 0 : "smallest number should be 0";
        assert max == n-1 : "largest number should be n-1";
        assert all.size() == n : "all numbers should be included";
        int cohorts = lists.size();
        assert minSize == n / cohorts : "the smallest list size is rounded down";
        if (n % cohorts == 0)
            assert maxSize == minSize : "evenly divisible groups should all be the same size";
        else
            assert maxSize == minSize + 1 : "group sizes differ at most by one";
    }
}
