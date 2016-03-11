package org.neolefty.cs143.hybrid_images.attic;

import javax.swing.*;
import java.awt.*;

/** Displays an image. */
public class ImagePanel extends JPanel {
    private Image image;

    @Override
    public void paint(Graphics g) {
        super.paint(g); // background
        if (image != null)
            g.drawImage(image, 0, 0, null);
    }

    public void setImage(Image image) {
        this.image = image;
        repaint();
    }
}
