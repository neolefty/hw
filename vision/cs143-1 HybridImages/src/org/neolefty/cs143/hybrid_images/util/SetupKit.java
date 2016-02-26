package org.neolefty.cs143.hybrid_images.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.Base64;
import java.util.prefs.Preferences;

public class SetupKit {
    /** Automatically save & restore size of window from prefs. */
    public static void addWindowRememberer
        (final JFrame frame, Class classForPrefs)
    {
        // dismiss move operations until we've established our position from prefs
        final boolean[] windowOpened = { false };
        final Preferences prefs = getPrefs(classForPrefs);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                frame.setBounds(getStartingBounds(prefs));
                windowOpened[0] = true;
            }
            @Override
            public void windowClosing(WindowEvent e) {
                saveWindowBounds(frame, prefs);
            }
        });
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (windowOpened[0]) // we'll get this event before windowOpened
                    saveWindowBounds(frame, prefs);
            }
            @Override
            public void componentMoved(ComponentEvent e) {
                if (windowOpened[0]) // we'll get this event before windowOpened
                    saveWindowBounds(frame, prefs);
            }
        });
    }

    public static void saveWindowBounds(JFrame frame, Preferences prefs) {
        if (frame.isVisible()) {
            Rectangle r = frame.getBounds();
            prefs.putInt(UIConstants.PREFS_WINDOW_LEFT, r.x);
            prefs.putInt(UIConstants.PREFS_WINDOW_TOP, r.y);
            prefs.putInt(UIConstants.PREFS_WINDOW_WIDTH, r.width);
            prefs.putInt(UIConstants.PREFS_WINDOW_HEIGHT, r.height);
        }
    }

    public static Rectangle getStartingBounds(Preferences prefs) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int left = prefs.getInt(UIConstants.PREFS_WINDOW_LEFT, screenSize.width / 4);
        int top = prefs.getInt(UIConstants.PREFS_WINDOW_TOP, screenSize.height / 4);
        int width = prefs.getInt(UIConstants.PREFS_WINDOW_WIDTH, screenSize.width / 2);
        int height = prefs.getInt(UIConstants.PREFS_WINDOW_HEIGHT, screenSize.height / 2);

        return new Rectangle(left, top, width, height);
    }

    private static Preferences getPrefs(Class cls) {
        return Preferences.userNodeForPackage(cls);
    }

    public static void setColors(Component comp) {
        setColors(comp, UIConstants.FG, UIConstants.BG);
    }

    // Recursively set the colors & font of comp to match our scheme
    public static void setColors(Component comp, Color fg, Color bg) {
        comp.setForeground(fg);
        comp.setBackground(bg);
        if (comp.getFont() != null)
            comp.setFont(comp.getFont().deriveFont(30f));
        if (comp instanceof Container) {
            Container panel = (Container) comp;
            synchronized (panel.getTreeLock()) {
                for (Component child : panel.getComponents())
                    setColors(child, fg, bg);
            }
        }
    }

    public static void init(JFrame frame) {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        SetupKit.addWindowRememberer(frame, frame.getClass());
        SetupKit.setColors(frame);
    }

    public static Object deserialize(byte[] buf) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buf));
        return in.readObject();
    }

    /** Deserialize an object, but if there are exceptions, print them and return null. */
    public static Object deserializePrintExceptions(byte[] buf) {
        try {
            return deserialize(buf);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] serializePrintExceptions(Object o) {
        try {
            return serialize(o);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] serialize(Object o) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bytes);
        out.writeObject(o);
        out.close();
        return bytes.toByteArray();
    }

    public static void savePref(Class clz, String key, Serializable value) {
        Preferences prefs = Preferences.userNodeForPackage(clz);
        if (value == null)
            prefs.remove(key);
        else {
            byte[] raw = serializePrintExceptions(value);
            // if this returns null, maybe remove the pref
            if (raw != null)
                prefs.putByteArray(key, Base64.getMimeEncoder().encode(raw));
        }
    }

    public static Object loadPref(Class clz, String key) {
        Preferences prefs = Preferences.userNodeForPackage(clz);
        byte[] encoded64 = prefs.getByteArray(key, null);
        if (encoded64 == null)
            return null;
        else {
            byte[] decoded = Base64.getMimeDecoder().decode(encoded64);
            return deserializePrintExceptions(decoded); // if this returns null maybe erase the pref
        }
    }
}
