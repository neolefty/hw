package org.neolefty.cs143.hybrid_images.ui;

/** Used to abbreviate items in a file history list but retain the full path behind the scenes. */
class Shortener {
    private String abbrev, verbose;

    public Shortener(String abbrev, String verbose) {
        this.abbrev = abbrev;
        this.verbose = verbose;
    }

    public String getAbbrev() {
        return abbrev;
    }

    public String getVerbose() {
        return verbose;
    }

    @Override public String toString() {
        return abbrev;
    }
}
