package org.neolefty.cs143.hybrid_images.ui.util;

import org.neolefty.cs143.hybrid_images.util.SetupKit;

import java.io.Serializable;
import java.util.prefs.Preferences;

public class PrefStuff {
    private Class prefsClass;
    private String prefsKey;
    private Preferences prefCache;

    public PrefStuff(Class prefsClass, String prefsKey) {
        this.prefsClass = prefsClass;
        this.prefsKey = prefsKey;
    }

    public Class getPrefsClass() { return prefsClass; }
    public String getPrefsKey() { return prefsKey; }

    public PrefStuff createChild(String suffix) {
        String newKey = prefsKey == null ? suffix : prefsKey + "." + suffix;
        return new PrefStuff(prefsClass, newKey);
    }

    public Preferences getNode() {
        if (prefCache == null)
            prefCache = Preferences.userNodeForPackage(prefsClass);
        return prefCache;
    }

    public double getDouble(double def) { return getNode().getDouble(prefsKey, def); }
    public void putDouble(double value) { getNode().putDouble(prefsKey, value); }
    public int getInt(int def) { return getNode().getInt(prefsKey, def); }
    public String getString(String def) { return getNode().get(prefsKey, def); }

    public <T extends Serializable> T getObject(T def) {
        //noinspection unchecked
        T result = (T) SetupKit.loadPref(prefsClass, prefsKey);
        if (result == null)
            result = def;
        return result;
    }

    public <T extends Serializable> void putObject(T value) {
        SetupKit.savePref(prefsClass, prefsKey, value);
    }
}
