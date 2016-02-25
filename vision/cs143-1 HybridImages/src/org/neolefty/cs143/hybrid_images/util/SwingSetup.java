package org.neolefty.cs143.hybrid_images.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;

public class SwingSetup {
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
        SwingSetup.addWindowRememberer(frame, frame.getClass());
        SwingSetup.setColors(frame);
    }
}
