package org.neolefty.cs143.hybrid_images.ui;

/** Used to abbreviate items in a file history list but retain the full path behind the scenes. */
class FilenameShortener {
    private String abbrev, verbose;

    public FilenameShortener(String abbrev, String verbose) {
        this.abbrev = abbrev;
        this.verbose = verbose;
    }

    public String getVerbose() {
        return verbose;
    }

    @Override public String toString() {
        return abbrev;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilenameShortener that = (FilenameShortener) o;
        return verbose.equals(that.verbose);
    }

    @Override
    public int hashCode() {
        return verbose.hashCode();
    }
}
