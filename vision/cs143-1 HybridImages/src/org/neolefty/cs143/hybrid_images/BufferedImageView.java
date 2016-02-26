package org.neolefty.cs143.hybrid_images;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;

/** View a BufferedImage in JavaFX. */
public class BufferedImageView extends ImageView {
    public void setImage(BufferedImage image) {
        double imgH = image.getHeight(), imgW = image.getWidth();
        setScaleX(300. / imgW);
        setScaleY(300. / imgH);
        setTranslateX((300-imgW)/2);
        setTranslateY((300-imgH)/2);
        setImage(SwingFXUtils.toFXImage(image, null));
    }
}
