package org.neolefty.cs143.hybrid_images.ui;

import javafx.beans.property.ReadOnlyDoubleWrapper;

/** A parameter for an image processor. */
public class ProcessorParam extends ReadOnlyDoubleWrapper {
    private double min, max, def;
    private String name;
    private String comment;
    private boolean integer;

    /** Construct a non-integer parameter. */
    public ProcessorParam(String name, double def, double min, double max, String comment) {
        this(name, def, min, max, false, comment);
    }

    /** Construct a parameter. */
    public ProcessorParam
        (String name, double def, double min, double max, boolean integer, String comment)
    {
        super(def);
        this.name = name;
        this.def = def;
        this.min = min;
        this.max = max;
        this.integer = integer;
        this.comment = comment;
        set(def);
    }

    /** High bound. */
    public double getMin() { return min; }
    /** Low bound. */
    public double getMax() { return max; }

    /** Default value. */
    public double getDefault() { return def; }

    public boolean isInteger() { return integer; }
    public void setInteger(boolean integer) { this.integer = integer; }

    public double doubleValue() { return getValue(); }
    public int intValue() {
        if (!isInteger())
            throw new IllegalStateException("Not an integer");
        return (int) doubleValue();
    }

    public String getComment() { return comment; }

    @Override public String getName() { return name; }
}
