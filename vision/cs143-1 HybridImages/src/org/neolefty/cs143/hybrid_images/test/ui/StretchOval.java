package org.neolefty.cs143.hybrid_images.test.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.Random;

public class StretchOval extends StackPane {
    private Canvas canvas;
    private Random r = new Random();

    public StretchOval() {
        widthProperty().addListener(observable -> {
            adjust();
        });
        heightProperty().addListener(observable -> {
            adjust();
        });
    }

    private void adjust() {
        if (canvas != null) {
            getChildren().remove(canvas);
            canvas = null;
        }

        double w = getWidth(), h = getHeight();
        canvas = new Canvas(w, h);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(new Color(r.nextDouble(), r.nextDouble(), r.nextDouble(), r.nextDouble()));
        gc.fillOval(0, 0, w, h);
        getChildren().add(canvas);
    }
}
