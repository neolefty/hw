package org.neolefty.cs143.hybrid_images;

import org.neolefty.cs143.hybrid_images.util.SwingSetup;

import javax.swing.*;

public class Main extends JFrame {
    public static void main(String[] args) {
        JFrame frame = new Main();
        SwingSetup.init(frame);
        frame.setVisible(true);
    }

    public Main() {
        super("Hybrid Images");
    }
}
