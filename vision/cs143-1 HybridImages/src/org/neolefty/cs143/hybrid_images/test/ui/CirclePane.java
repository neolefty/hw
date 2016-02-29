package org.neolefty.cs143.hybrid_images.test.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class CirclePane extends Canvas {
    public CirclePane(int w, int h, Color c) {
        super(w, h);
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(c);
//        gc.setFill(new LinearGradient(0, 0, w, h, true, null,
//                new Stop(0.0, Color.TRANSPARENT), new Stop(1., c)));
        gc.fillOval(0, 0, w, h);
    }
}
