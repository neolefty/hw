package org.neolefty.cs143.hybrid_images.ui.util;

import java.awt.*;

/** Constants for the UI. */
public class UIConstants {
    /** Production mode (true) or development mode (false)? */
    public static final boolean PRODUCTION = false;

    public static final long SCREENSAVER_TIMEOUT = 10 * 60 * 1000; // 10 minutes
//    public static final long SCREENSAVER_TIMEOUT = 3 * 1000; // 3 seconds
//    public static final long SCREENSAVER_TIMEOUT = 1000; // 1 second

    public static final String PREFS_WINDOW_LEFT = "Window Left";
    public static final String PREFS_WINDOW_TOP = "Window Top";
    public static final String PREFS_WINDOW_WIDTH = "Window Width";
    public static final String PREFS_WINDOW_HEIGHT = "Window Height";

//    public static final int AUTOPLAY_STEP_MILLIS = 650;
    public static final int AUTOPLAY_STEP_MILLIS = 200;

    // Colors
    public static final Color FG = Color.WHITE; /** Text & border foreground */

    public static final Color BG = Color.BLACK; /** Normal background */
}
